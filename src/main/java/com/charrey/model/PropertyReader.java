package com.charrey.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Reads and writes properties to files (the model of this MVC)
 */
public class PropertyReader {


    /**
     * Writes the selected input location to the cache file.
     *
     * @param selectedIn the selected input location
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeIn(Path selectedIn) throws IOException {
        writeGeneric("in", selectedIn.toString());
    }

    /**
     * Writes the selected output location to the cache file.
     *
     * @param selectedOut the selected output location
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeOut(Path selectedOut) throws IOException {
        writeGeneric("out", selectedOut.toString());
    }

    /**
     * Writes the selected signature file location to the cache file.
     *
     * @param selectedSignature the selected signature file location
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeSignature(Path selectedSignature) throws IOException {
        writeGeneric("signature", selectedSignature.toString());
    }

    /**
     * Writes the page number to the cache file.
     *
     * @param value the filled in page number
     * @throws IOException thrown when we could not write to the file
     */
    public static void writePage(Integer value) throws IOException {
        writeGeneric("page", String.valueOf(value));
    }

    /**
     * Writes whether the repeat checkbox was checked to the cache file.
     *
     * @param repeat whether the repeat-checkbox was checked
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeRepeat(boolean repeat) throws IOException {
        writeGeneric("repeat", repeat ? "1" : "0");
    }

    /**
     * Writes the horizontal position of the signature in the pdf to the cache file.
     *
     * @param x the horizontal position of the signature
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeX(int x) throws IOException {
        writeGeneric("x", String.valueOf(x));
    }

    /**
     * Writes the vertical position of the signature in the pdf to the cache file.
     *
     * @param y the vertical position of the signature
     * @throws IOException thrown when we could not write to the file
     */
    public static void writeY(int y) throws IOException {
        writeGeneric("y", String.valueOf(y));
    }

    /**
     * Reads the previously selected input location from cache.
     *
     * @return the previously selected input location
     * @throws IOException thrown when we could not read from the file
     */
    public static Path readIn() throws IOException {
        return Paths.get(readGeneric("in", System.getProperty("user.home")));
    }

    /**
     * Reads the previously selected output location from cache.
     *
     * @return the previously selected output location
     * @throws IOException thrown when we could not read from the file
     */
    public static Path readOut() throws IOException {
        return Paths.get(readGeneric("out", System.getProperty("user.home")));
    }

    /**
     * Reads the previously selected signature file location from cache.
     *
     * @return the previously selected signature file location
     * @throws IOException thrown when we could not read from the file
     */
    public static Path readSignature() throws IOException {
        return Paths.get(readGeneric("signature", System.getProperty("user.home")));
    }

    /**
     * Reads the previously selected page number from cache.
     *
     * @return the previously selected page number
     * @throws IOException thrown when we could not read from the file
     */
    public static int readPage() throws IOException {
        return Integer.parseInt(readGeneric("page", "1"));
    }

    /**
     * Reads from cache whether we previously checked the repeat checkbox.
     *
     * @return whether the repeat checkbox was selected last time.
     * @throws IOException thrown when we could not read from the file
     */
    public static boolean readRepeat() throws IOException {
        return readGeneric("repeat", "0").equals("1");
    }

    /**
     * Reads the previously selected horizontal location of the signature from cache.
     *
     * @return the previously selected horizontal location.
     * @throws IOException thrown when we could not read from the file
     */
    public static int readX() throws IOException {
        return Integer.parseInt(readGeneric("x", "0"));
    }

    /**
     * Reads the previously selected vertical location of the signature from cache.
     *
     * @return the previously selected vertical location.
     * @throws IOException thrown when we could not read from the file
     */
    public static int readY() throws IOException {
        return Integer.parseInt(readGeneric("y", "0"));
    }


    private static void writeJSON(JSONObject myJson) throws IOException {
        Files.write(Paths.get("./options.json"), myJson.toJSONString().getBytes());
    }

    private static final JSONParser jsonParser = new JSONParser();
    private static JSONObject readJSON() throws IOException {
        try {
            return (JSONObject) jsonParser.parse(new FileReader("options.json"));
        } catch (ParseException e) {
            throw new RuntimeException("JSON parse error");
        }
    }


    private static void createIniFile() throws IOException {
        if (!new File("options.json").exists()) {
            Files.createFile(Paths.get("options.json"));
            Files.write(Paths.get("./options.json"), new JSONObject().toJSONString().getBytes());
        }
    }



    private static void writeGeneric(String key, String value) throws IOException {
        createIniFile();
        JSONObject myJson = readJSON();
        myJson.put(key, value);
        writeJSON(myJson);
    }

    private static String readGeneric(String key, String defaultObject) throws IOException {
        createIniFile();
        JSONObject json = readJSON();
        if (json.containsKey(key)) {
            return (String) json.get(key);
        } else {
            return defaultObject;
        }
    }



}
