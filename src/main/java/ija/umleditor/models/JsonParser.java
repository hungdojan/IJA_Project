/**
 * @brief Declaration of JsonParser class.
 * JsonParser contains two static functions that loads class diagram from JSON file
 * and store content of class diagram to JSON file.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file JsonParser.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import org.json.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class JsonParser {
    /**
     * Loads JSON file content into class diagram.
     * @param filePath Path to JSON file with content.
     * @return Instance of class diagram.
     */
    public static ClassDiagram initFromFile(String filePath) throws IOException {
        if (filePath == null)
            return null;

        File file = new File(filePath);
        InputStream is = new FileInputStream(file);
        JSONObject object = new JSONObject(new JSONTokener(is));
        if (!Objects.equals((String) object.get("_class"), "ClassDiagram")) {
            return null;
        }
        var classDiagram = new ClassDiagram((String) object.get("name"));
        JSONArray jsonClassElements = (JSONArray) object.get("classElements");

        is.close();
        return classDiagram;
    }

    private void loadClassElements(ClassDiagram cd, JSONArray array) {
        Map<String, UMLClassifier> mapOfClassifiers = new HashMap<>();
        for (var item : array) {
            JSONObject jsonClassElement = (JSONObject) item;
            String name = (String) jsonClassElement.get("name");
            UMLClassifier element = null;
            if (Objects.equals((String) jsonClassElement.get("_class"), "UMLClassifier")) {
                element = ClassDiagram.createClassifier(name, false);
            } else {
                element = ClassDiagram.createClassifier(name, true);
                ((UMLClass) element).setAbstract((boolean) jsonClassElement.get("isAbstract"));
            }
            mapOfClassifiers.put(name, element);
            cd.addClassifier(element);
        }

        // TODO: add attributes
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
