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
import java.util.*;

/**
 * Declaration of JsonParser class.
 */
public class JsonParser {
    /**
     * Loads JSON file content into class diagram.
     * @param filePath Path to JSON file with content.
     * @throws IOException Exception cased by unsuccessful file loading.
     * @return Instance of class diagram.
     */
    public static ClassDiagram initFromFile(String filePath) throws IOException {
        if (filePath == null)
            return null;

        File file = new File(filePath);
        InputStream is = new FileInputStream(file);
        JSONObject object = new JSONObject(new JSONTokener(is));
        if (!Objects.equals(object.get("_class"), "ClassDiagram")) {
            return null;
        }
        var classDiagram = new ClassDiagram((String) object.get("name"));
        // load class elements
        JSONArray jsonClassElements = (JSONArray) object.get("classElements");
        loadClassElements(classDiagram, jsonClassElements);

        // load sequence diagrams
        JSONArray jsonSequenceDiagrams = (JSONArray) object.get("sequenceDiagrams");
        loadSequenceDiagrams(classDiagram, jsonSequenceDiagrams);

        JSONArray jsonRelations = (JSONArray) object.get("relations");
        loadRelations(classDiagram, jsonRelations);

        is.close();
        return classDiagram;
    }

    /**
     * Loads realtions from list of JSONRelations.
     * @param cd Main class diagram.
     * @param jsonRelations Json list of relations.
     */
    private static void loadRelations(ClassDiagram cd, JSONArray jsonRelations) {
        for (var item : jsonRelations) {
            JSONObject jsonRelation = (JSONObject) item;
            if (!(Objects.equals(jsonRelation.get("_class"), "UMLRelation")))
                continue;
            String srcName = (String) jsonRelation.get("src");
            String dstName = (String) jsonRelation.get("dest");
            String srcMsgName = (String) jsonRelation.get("srcMsg");
            String dstMsgName = (String) jsonRelation.get("dstMsg");
            RelationType typeName = RelationType.valueOf(String.valueOf(jsonRelation.get("relationType")));
            var src = cd.getClass(srcName);
            if (src == null)
                continue;
            var dst = cd.getClass(dstName);
            if (dst == null)
                continue;
            src.addRelation(dst, typeName);
            var relation = src.getRelation(dst);
            relation.setSrcMsg(srcMsgName);
            relation.setDestMsg(dstMsgName);
        }
    }

    /**
     * Loads sequence diagrams from list of JSONObjects.
     * @param cd Main class diagram.
     * @param jsonSequenceDiagrams Json list of sequence diagrams.
     */
    private static void loadSequenceDiagrams(ClassDiagram cd, JSONArray jsonSequenceDiagrams) {
        for (var item : jsonSequenceDiagrams) {
            JSONObject jsonSequenceDiagram = (JSONObject) item;
            if (!Objects.equals(jsonSequenceDiagram.get("_class"), "SequenceDiagram"))
                continue;

            String diagramName = (String) jsonSequenceDiagram.get("name");
            var sequenceDiagram = new SequenceDiagram(diagramName);
            for (var o : (JSONArray) jsonSequenceDiagram.get("objects")) {
                JSONObject jsonObject = (JSONObject) o;
                if (!Objects.equals(jsonObject.get("_class"), "UMLObject"))
                    continue;
                String objectName = (String) jsonObject.get("name");
                UMLClass baseClass = cd.getClass((String) jsonObject.get("classOfInstance"));
                UMLObject object = new UMLObject(objectName, baseClass);
                sequenceDiagram.addObject(object);
            }

            for (var m : (JSONArray) jsonSequenceDiagram.get("messages")) {
                JSONObject jsonMessage = (JSONObject) m;
                String msgName = (String) jsonMessage.get("name");
                String srcName = (String) jsonMessage.get("sender");
                var sender = sequenceDiagram.getObject(srcName);
                String dstName = (String) jsonMessage.get("receiver");
                var receiver = sequenceDiagram.getObject(dstName);
                String baseOperation = (String) jsonMessage.get("message");
                MessageType messageType = MessageType.valueOf((String) jsonMessage.get("messageType"));
                UMLOperation operation;
                if (receiver == null)
                    operation = SequenceDiagram.undefOperation;
                else
                    operation = (UMLOperation) receiver.getClassOfInstance().getAttribute(baseOperation);
                UMLMessage message = new UMLMessage(msgName, sender, receiver, operation);
                message.setMessageType(messageType);
                sequenceDiagram.addMessage(message);
            }
            cd.addSequenceDiagram(sequenceDiagram);
        }
    }

    /**
     * Loads attribute from JSONObject.
     * @param jsonAttribute JSON representation of attribute.
     * @param mapOfClassifiers Map of available classifiers.
     * @return Created instance of UMLAttribute, null when error occur.
     */
    private static UMLAttribute loadAttribute(JSONObject jsonAttribute, Map<String, UMLClassifier> mapOfClassifiers) {
        String typeName = (String) jsonAttribute.get("type");
        String attrName = (String) jsonAttribute.get("name");

        UMLClassifier classElement = mapOfClassifiers.get(typeName);
        if (classElement == null)
            return null;
        var attribute = UMLClass.createAttribute(false, attrName, classElement);
        attribute.setVisibility(((String) jsonAttribute.get("visibility")).charAt(0));
        return attribute;
    }

    /**
     * Loads operation from JSONObject.
     * @param jsonOperation JSON representation of operation object.
     * @param mapOfClassifiers Map of available classifiers.
     * @return Created instance of UMLOperation, null when error occur.
     */
    private static UMLOperation loadOperation(JSONObject jsonOperation, Map<String, UMLClassifier> mapOfClassifiers) {
        String typeName = (String) jsonOperation.get("type");
        String attrName = (String) jsonOperation.get("name");

        UMLClassifier classElement = mapOfClassifiers.get(typeName);
        if (classElement == null)
            return null;

        UMLOperation operation = (UMLOperation) UMLClass.createAttribute(true, attrName, classElement);
        JSONArray jsonParameters = (JSONArray) jsonOperation.get("operationParameters");
        for (var item : jsonParameters) {
            JSONObject jsonParam = (JSONObject) item;
            var param = loadAttribute(jsonParam, mapOfClassifiers);
            if (param != null)
                operation.addParameter(param);
        }
        operation.setVisibility(((String) jsonOperation.get("visibility")).charAt(0));
        return operation;
    }

    /**
     * Loads all attributes from JSON class representation.
     * @param jsonClass JSON class element representation.
     * @param mapOfClassifiers Map of available classifiers.
     */
    private static void loadAttributes(JSONObject jsonClass, Map<String, UMLClassifier> mapOfClassifiers) {
        UMLClass cls = (UMLClass) mapOfClassifiers.get((String) jsonClass.get("name"));
        JSONArray jsonAttributes = (JSONArray) jsonClass.get("attributes");
        for (var item : jsonAttributes) {
            JSONObject jsonAttribute = (JSONObject) item;
            if (Objects.equals(jsonAttribute.get("_class"), "UMLAttribute")) {
                UMLAttribute attr = loadAttribute(jsonAttribute, mapOfClassifiers);
                cls.addAttribute(attr);
            } else if (Objects.equals(jsonAttribute.get("_class"), "UMLOperation")) {
                UMLOperation operation = loadOperation(jsonAttribute, mapOfClassifiers);
                cls.addAttribute(operation);
            }
        }
    }

    /**
     * Loads class elements from list of JSONObjects.
     * @param cd Instance of main class diagram.
     * @param array JSON array of classifiers.
     */
    private static void loadClassElements(ClassDiagram cd, JSONArray array) {
        Map<String, UMLClassifier> mapOfClassifiers = new HashMap<>();
        for (var item : array) {
            JSONObject jsonClassElement = (JSONObject) item;
            String name = (String) jsonClassElement.get("name");
            int x = (int) jsonClassElement.get("x");
            int y = (int) jsonClassElement.get("y");
            UMLClassifier element;
            if (Objects.equals(jsonClassElement.get("_class"), "UMLClassifier")) {
                element = ClassDiagram.createClassifier(name, false);
            } else {
                element = ClassDiagram.createClassifier(name, true);
                ((UMLClass) element).setAbstract((boolean) jsonClassElement.get("isAbstract"));
            }
            element.x = x;
            element.y = y;
            mapOfClassifiers.put(name, element);
            cd.addClassifier(element);
        }

        for (var item : array) {
            JSONObject jsonClass = (JSONObject) item;
            if (Objects.equals(jsonClass.get("_class"), "UMLClass"))
                loadAttributes(jsonClass, mapOfClassifiers);
        }
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
