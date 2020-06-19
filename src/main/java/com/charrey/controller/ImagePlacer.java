package com.charrey.controller;

import com.charrey.view.MyFrame;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Class that performs the image placing.
 */
public class ImagePlacer implements Runnable {

    private final Path signature;
    private final Path in;
    private final Path out;
    private final int y;
    private final int x;
    private final boolean repeat;
    private final int page;

    /**
     * Instantiates a new ImagePlacer that can concurrently perform an image placing assignment.
     *
     * @param in        the input location
     * @param out       the output location
     * @param signature the signature file location
     * @param page      the page number
     * @param repeat    whether to repeat every n'th page
     * @param x         the horizontal location of the signature (from the left)
     * @param y         the vertical location of the signature (from the top).
     */
    public ImagePlacer(Path in, Path out, Path signature, int page, boolean repeat, int x, int y) {
        this.in = in;
        this.out = out;
        this.signature = signature;
        this.page = page;
        this.repeat = repeat;
        this.x = x;
        this.y = y;
    }


    @Override
    public void run() {
        MyFrame.getInstance().addLog("Reading signature...");
        BufferedImage signatureImage;
        try {
            signatureImage = loadImage(signature);
        } catch (IOException e) {
            MyFrame.getInstance().addLog("Could not load image.");
            MyFrame.getInstance().enableButtons();
            Controller.active = false;
            return;
        }
        if (!in.toFile().exists()) {
            MyFrame.getInstance().addLog("Selected input does not exist.");
            MyFrame.getInstance().enableButtons();
            Controller.active = false;
            return;
        }
        if (!out.toFile().exists()) {
            MyFrame.getInstance().addLog("Selected input does not exist.");
            MyFrame.getInstance().enableButtons();
            Controller.active = false;
            return;
        }
        MyFrame.getInstance().addLog("Reading PDF files...");
        java.util.List<File> files = getFiles(in);
        int successes = 0;
        for (int i = 0; i < files.size(); i++) {
            MyFrame.getInstance().addLog("Reading file " + i+1 + "/" + files.size());
            PDDocument document;
            File file = files.get(i);
            try {
                document = PDDocument.load(file);
                for (int pageIndex = this.page - 1; repeat ? pageIndex < document.getNumberOfPages() : pageIndex < this.page; pageIndex += this.page) {
                    PDPage page = document.getPage(pageIndex);
                    placeImageSafely(page, document, signatureImage, x, (int) ((page.getMediaBox().getHeight() - signatureImage.getHeight()) - y));
                }
                AccessPermission accessPermission = new AccessPermission();
                accessPermission.setCanExtractContent(false);
                StandardProtectionPolicy spp = new StandardProtectionPolicy("fwao872fwe98gagaegkag","",accessPermission);
                spp.setEncryptionKeyLength(256);
                spp.setPermissions(accessPermission);
                document.protect(spp);
                document.save(out.resolve(FilenameUtils.removeExtension(file.getName()) + "-signed.pdf").toFile());
                successes++;
            } catch (IOException e) {
                MyFrame.getInstance().addLog("Error reading file.");
            }
        }
        MyFrame.getInstance().addLog("Successfully signed " + successes + "/" + files.size() + " documents.");
        MyFrame.getInstance().enableButtons();
        Controller.active = false;
    }

    private void placeImageSafely(PDPage page, PDDocument document, BufferedImage signatureImage, int x, int y) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, false);
        PDImageXObject pdImage = JPEGFactory.createFromImage(document, signatureImage, 1f);
        contentStream.drawImage(pdImage, x, y);
        contentStream.close();
    }

    private List<File> getFiles(Path in) {
        if (in.toFile().isFile()) {
            List<File> toReturn = new LinkedList<>();
            toReturn.add(in.toFile());
            return toReturn;
        } else {
            return Arrays.asList(in.toFile().listFiles());
        }
    }

    private BufferedImage loadImage(Path signature) throws IOException {
        return ImageIO.read(signature.toFile());
    }
}
