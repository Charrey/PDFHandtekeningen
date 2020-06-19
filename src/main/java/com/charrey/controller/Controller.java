package com.charrey.controller;

import java.nio.file.Path;

/**
 * The Controller of this MVC
 */
public class Controller {


    /**
     * Whether we are currently processing an assignment.
     */
    public static volatile boolean active = false;
    private static Thread myThread;

    /**
     * Places signatures in the PDF file.
     *
     * @param in        the input location
     * @param out       the output location
     * @param signature the signature file location
     * @param page      the page number
     * @param repeat    whether to repeat every n'th page
     * @param x         the horizontal location of the signature (from the left)
     * @param y         the vertical location of the signature (from the top).
     */
    public static void activate(Path in, Path out, Path signature, int page, boolean repeat, int x, int y) {
        if (!active) {
            myThread = new Thread(new ImagePlacer(in, out, signature, page, repeat, x, y));
            myThread.start();
        }
    }
}
