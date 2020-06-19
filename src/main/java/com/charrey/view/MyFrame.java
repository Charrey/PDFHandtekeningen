package com.charrey.view;

import com.charrey.controller.Controller;
import com.charrey.model.PropertyReader;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * View of this application
 */
public class MyFrame  extends JFrame {

    private static MyFrame instance;

    /**
     * Returns the singleton instance.
     *
     * @return the instance of the view of this application.
     */
    public static MyFrame getInstance() {
        if (instance == null) {
            instance = new MyFrame();
        }
        return instance;
    }

    private final JButton loadInput = new JButton("Select input file/folder");
    private Path selectedIn;
    private final JLabel showInput = new JLabel("No input selected");

    private final JButton loadOutput = new JButton("Select output folder");
    private Path selectedOut;
    private final JLabel showOutput = new JLabel("No output selected");

    private final JButton loadSignature = new JButton("Select signature file");
    private Path selectedSignature;
    private final JLabel showSignature = new JLabel("No signature selected");

    private final JSpinner whichPage = new JSpinner();
    private int selectedPage;

    private final JSpinner xSelector = new JSpinner();
    private final JSpinner ySelector = new JSpinner();


    private final JCheckBox repeat = new JCheckBox("Repeat every nth page?", null, false);
    private boolean selectedRepeat;

    private final JButton goButton = new JButton("Go!");
    private final JTextArea log = new JTextArea("Hello there!\n");


    private final JFileChooser infc = new JFileChooser();
    private final JFileChooser outfc = new JFileChooser();
    private final JFileChooser signaturefc = new JFileChooser();


    private MyFrame() {
        whichPage.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        xSelector.setModel(new SpinnerNumberModel(0, 0, 595, 1));
        ySelector.setModel(new SpinnerNumberModel(0, 0, 842, 1));
        readProperties();
        this.setLayout(new GridBagLayout());
        GridBagConstraints c;
        MyFrame myframe = this;
        c = new GridBagConstraints();
        c.gridy = 0;
        loadInput.setPreferredSize(new Dimension(400, 40));
        infc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF files", "pdf");
        infc.setFileFilter(filter);
        loadInput.addActionListener(e -> {
            infc.setCurrentDirectory(selectedIn.toFile());
            int returnValue = infc.showOpenDialog(myframe);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selected = infc.getSelectedFile();
                if (selected.isDirectory()) {
                    addLog("Selected PDF input directory " + selected);
                    selectedIn = selected.toPath();
                    showInput.setText(selectedIn.toString());
                    try {
                        PropertyReader.writeIn(selectedIn);
                    } catch (IOException ioException) {
                        addLog("I/O exception while caching paths.");
                    }
                } else if (selected.getName().endsWith(".pdf")) {
                    addLog("Selected PDF input file " + selected);
                    selectedIn = selected.toPath();
                    showInput.setText(selectedIn.toString());
                    try {
                        PropertyReader.writeIn(selectedIn);
                    } catch (IOException ioException) {
                        addLog("I/O exception while caching paths.");
                    }
                } else {
                    JOptionPane.showMessageDialog(myframe, "Error: You may only select PDF files.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loadInput, c);

        c = new GridBagConstraints();
        c.gridy = 1;
        showInput.setPreferredSize(new Dimension(400, 20));
        add(showInput, c);

        c = new GridBagConstraints();
        c.gridy = 2;
        loadOutput.setPreferredSize(new Dimension(400, 40));
        outfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        loadOutput.addActionListener(e -> {
            outfc.setCurrentDirectory(selectedOut.toFile());
            int returnValue = outfc.showOpenDialog(myframe);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selected = outfc.getSelectedFile();
                addLog("Selected PDF output directory " + selected);
                selectedOut = selected.toPath();
                showOutput.setText(selected.toString());
                try {
                    PropertyReader.writeOut(selectedOut);
                } catch (IOException ioException) {
                    addLog("I/O exception while caching paths.");
                }
            }
        });
        add(loadOutput, c);

        c = new GridBagConstraints();
        c.gridy = 3;
        showOutput.setPreferredSize(new Dimension(400, 20));
        add(showOutput, c);

        c = new GridBagConstraints();
        c.gridy = 4;
        loadSignature.setPreferredSize(new Dimension(400, 40));
        signaturefc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        filter = new FileNameExtensionFilter("Image files", "jpeg", "jpg", "png", "bmp");
        signaturefc.setFileFilter(filter);
        loadSignature.addActionListener(e -> {
            signaturefc.setCurrentDirectory(selectedSignature.toFile());
            int returnValue = signaturefc.showOpenDialog(myframe);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selected = signaturefc.getSelectedFile();
                addLog("Selected signature file " + selected);
                selectedSignature = selected.toPath();
                showSignature.setText(selected.toString());
                try {
                    PropertyReader.writeSignature(selectedSignature);
                } catch (IOException ioException) {
                    addLog("I/O exception while caching paths.");
                }
            }
        });
        add(loadSignature, c);

        c = new GridBagConstraints();
        c.gridy = 5;
        showSignature.setPreferredSize(new Dimension(400, 20));
        add(showSignature, c);

        c = new GridBagConstraints();
        c.gridy = 6;
        JLabel instructions = new JLabel("On which page is the signature line?");
        instructions.setPreferredSize(new Dimension(400, 40));
        add(instructions, c);

        c = new GridBagConstraints();
        c.gridy = 7;
        whichPage.addChangeListener(e -> {
            try {
                PropertyReader.writePage(((Integer)((JSpinner)e.getSource()).getValue()));
            } catch (IOException ioException) {
                addLog("Could not cache preferred page.");
            }
        });
        whichPage.setPreferredSize(new Dimension(400, 40));
        add(whichPage, c);

        c = new GridBagConstraints();
        c.gridy = 8;
        repeat.setPreferredSize(new Dimension(400, 40));
        repeat.addActionListener(e -> {
            myframe.selectedRepeat = ((JCheckBox) e.getSource()).isSelected();
            try {
                PropertyReader.writeRepeat(myframe.selectedRepeat);
            } catch (IOException ioException) {
                addLog("Could not cache repeat state.");
            }
        });
        add(repeat, c);

        c = new GridBagConstraints();
        c.gridy = 9;
        instructions = new JLabel("Distance (pts) from top:");
        instructions.setPreferredSize(new Dimension(400, 40));
        add(instructions, c);

        c = new GridBagConstraints();
        c.gridy = 10;
        ySelector.setPreferredSize(new Dimension(400, 40));
        ySelector.addChangeListener(e -> {
            try {
                PropertyReader.writeY((Integer) ((JSpinner)e.getSource()).getValue());
            } catch (IOException ioException) {
                addLog("Could not cache Y value.");
            }
        });
        add(ySelector, c);

        c = new GridBagConstraints();
        c.gridy = 11;
        instructions = new JLabel("Distance (pts) from left:");
        instructions.setPreferredSize(new Dimension(400, 40));
        add(instructions, c);

        c = new GridBagConstraints();
        c.gridy = 12;
        xSelector.setPreferredSize(new Dimension(400, 40));
        xSelector.addChangeListener(e -> {
            try {
                PropertyReader.writeX((Integer) ((JSpinner)e.getSource()).getValue());
            } catch (IOException ioException) {
                addLog("Could not cache Y value.");
            }
        });
        add(xSelector, c);


        c = new GridBagConstraints();
        c.gridy = 13;
        goButton.setPreferredSize(new Dimension(400, 40));
        goButton.addActionListener(e -> {
            if (selectedIn.equals(Paths.get(System.getProperty("user.home")))) {
                JOptionPane.showMessageDialog(myframe, "Error: you must select some input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedOut.equals(Paths.get(System.getProperty("user.home")))) {
                JOptionPane.showMessageDialog(myframe, "Error: you must select some output.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedSignature.equals(Paths.get(System.getProperty("user.home")))) {
                JOptionPane.showMessageDialog(myframe, "Error: you must select some signature.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            myframe.disableButtons();
            Controller.activate(selectedIn, selectedOut, selectedSignature, (Integer) whichPage.getValue(), repeat.isSelected(), (Integer)xSelector.getValue(), (Integer)ySelector.getValue());
        });
        add(goButton, c);

        c = new GridBagConstraints();
        c.gridy = 14;
        ((DefaultCaret)log.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        log.setEditable(false);
        log.setLineWrap(true);
        log.setPreferredSize(new Dimension(400, 200));
        add(log, c);
        pack();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Disables all gui buttons.
     */
    public void disableButtons() {
        loadInput.setEnabled(false);
        loadOutput.setEnabled(false);
        loadSignature.setEnabled(false);
        goButton.setEnabled(false);
    }

    /**
     * Enables all gui buttons.
     */
    public void enableButtons() {
        loadInput.setEnabled(true);
        loadOutput.setEnabled(true);
        loadSignature.setEnabled(true);
        goButton.setEnabled(true);
    }



    private void readProperties() {
        try {
            selectedIn = PropertyReader.readIn();
            selectedOut = PropertyReader.readOut();
            selectedSignature = PropertyReader.readSignature();
            selectedPage = PropertyReader.readPage();
            selectedRepeat = PropertyReader.readRepeat();
            xSelector.setValue(PropertyReader.readX());
            ySelector.setValue(PropertyReader.readY());
        } catch (IOException e) {
            addLog("I/O exception while loading stored cache.");
        }
        selectedIn = selectedIn == null ? Paths.get(System.getProperty("user.home")) : selectedIn;
        selectedOut = selectedOut == null ? Paths.get(System.getProperty("user.home")) : selectedOut;
        selectedSignature = selectedSignature == null ? Paths.get(System.getProperty("user.home")) : selectedSignature;
        showInput.setText(selectedIn.toString());
        showOutput.setText(selectedOut.toString());
        showSignature.setText(selectedSignature.toString());
        repeat.setSelected(selectedRepeat);
        whichPage.setValue(selectedPage);
    }

    /**
     * Adds something to the log in the gui.
     *
     * @param toAdd string to add to the log.
     */
    public void addLog(String toAdd) {
        log.append(toAdd + "\n");
    }

}
