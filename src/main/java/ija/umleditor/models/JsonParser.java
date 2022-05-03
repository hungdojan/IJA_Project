package ija.umleditor.models;

import org.json.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonParser {
    /**
     * Loads JSON file content into class diagram.
     * @param filePath Path to JSON file with content.
     * @return Instance of class diagram.
     */
    public static ClassDiagram initFromFile(String filePath) {
        return null;
    }

    /**
     * Stores {@code classDiagram} content to {@code filePath}.
     * @param classDiagram Instance of class diagram.
     * @param filePath Path to file to store.
     */
    public static void saveToFile(ClassDiagram classDiagram, String filePath) {
        var object = classDiagram.createJsonObject();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath),
                StandardCharsets.UTF_8)) {
            writer.write(object.toString(4));
        } catch (IOException ignored) { }
    }
}
