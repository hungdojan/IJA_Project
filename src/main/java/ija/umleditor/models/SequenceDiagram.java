/**
 * @brief Declaration of SequenceDiagram class.
 * SequenceDiagram class represents abstract structure that holds sequence diagram data.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file SequenceDiagram.java
 * @date 06/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Declaration of SequenceDiagram class.
 */
public class SequenceDiagram extends Element {

    private final List<UMLObject> objects;
    private final List<UMLMessage> messages;
    public static UMLClass undefClass          = new UMLClass("#UNDEF");
    public static UMLOperation undefOperation  = new UMLOperation("#UNDEF", ClassDiagram.undefClassifier);
    public static UMLObject undefObject        = new UMLObject("#UNDEF", undefClass);

    /**
     * Class {@code SequenceDiagram} constructor
     * @param name Name of the diagram
     */
    public SequenceDiagram(String name) {
        super(name);
        objects = new ArrayList<>();
        messages = new ArrayList<>();
    }

    /**
     * Gets list of all objects.
     * @return List of instances of UMLObject
     */
    public List<UMLObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Gets list of all messages.
     * @return List of instances of UMLMessage
     */
    public List<UMLMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    /**
     * Creates instance of UMLObject and adds into sequence diagram
     * @param instanceOfClass instance of UMLClass
     * @param name Name od GObject
     * @return Instance of GObject
     */
    public UMLObject addObject(UMLClass instanceOfClass, String name) {
        if (instanceOfClass == null)
            return null;
        var object = objects.stream().filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
        if (object != null)
            return null;
        object = new UMLObject(name, instanceOfClass);
        objects.add(object);
        return object;
    }

    /**
     * Checks for duplicate while adding into diagram
     * @param obj Instance of UMLObject
     * @return True if object was added successfully
     */
    public boolean addObject(UMLObject obj) {
        if (obj == null)
            return false;
        if (objects.contains(obj))
            return false;
        return objects.add(obj);
    }

    /**
     * Gets object from list of objects.
     * @param name Name of object to be found
     * @return Instance of UMLObject
     */
    public UMLObject getObject(String name) {
        return objects.stream().filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
    }

    /**
     * Removes object from list of objects.
     * @param object Instance of UMLObject
     * @return False if object was not found, true if it was successfully removed
     */
    public boolean removeObject(UMLObject object) {
        if (object == null)
            return false;
        return objects.remove(object);
    }

    /**
     * Adds new message to the list of messages.
     * @param src Source object
     * @param dst Destination object
     * @return Instance of UMLClass
     */
    public UMLMessage addMessage(UMLObject src, UMLObject dst) {
        var message = new UMLMessage("", src, dst, null);
        messages.add(message);
        return message;
    }

    /**
     * Adds new message to the list of messages.
     * @param msg Instance of UMLMessage to be added
     * @return True if message was successfully added.
     */
    public boolean addMessage(UMLMessage msg) {
        if (msg == null || messages.contains(msg))
            return false;
        return messages.add(msg);
    }

    /**
     * Clear dependencies.
     */
    public void close() {
        for (var o : objects)
            o.close();
        for (var m : messages)
            m.close();
    }

    @Override
    /**
     * Creates JSON representation of element's content.
     */
    public JSONObject createJsonObject() {
        JSONObject object = new JSONObject();
        object.put("_class", "SequenceDiagram");
        object.put("name", nameProperty.getValue());

        // add objects
        JSONArray jsonObjects = new JSONArray();
        for (var o : objects) {
            var jsonObject = o.createJsonObject();
            jsonObjects.put(jsonObject);
        }
        object.put("objects", jsonObjects);

        // add messages
        JSONArray jsonMessages = new JSONArray();
        for (var m : messages) {
            var jsonMessage = m.createJsonObject();
            jsonMessages.put(jsonMessage);
        }
        object.put("messages", jsonMessages);
        return object;
    }
}
