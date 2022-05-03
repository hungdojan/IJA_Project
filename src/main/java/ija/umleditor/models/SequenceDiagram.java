package ija.umleditor.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SequenceDiagram extends Element {

    private final List<UMLObject> objects;
    private final List<UMLMessage> messages;
    public static UMLClass undefClass          = new UMLClass("#UNDEF");
    public static UMLOperation undefOperation  = new UMLOperation("#UNDEF", ClassDiagram.undefClassifier);
    public static UMLObject undefObject        = new UMLObject("#UNDEF", undefClass);
    // TODO:
    public SequenceDiagram(String name) {
        super(name);
        objects = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public List<UMLObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public List<UMLMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

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

    public boolean addObject(UMLObject obj) {
        if (obj == null)
            return false;
        if (objects.contains(obj))
            return false;
        return objects.add(obj);
    }

    public UMLObject getObject(String name) {
        return objects.stream().filter(x -> x.getName().equals(name))
                .findFirst().orElse(null);
    }

    public boolean removeObject(String name) {
        var obj = getObject(name);
        return removeObject(obj);
    }

    public boolean removeObject(UMLObject object) {
        if (object == null)
            return false;
        return objects.remove(object);
    }

    public UMLMessage addMessage(UMLObject src, UMLObject dst) {
        var message = new UMLMessage("", src, dst, null);
        messages.add(message);
        return message;
    }

    public boolean addMessage(UMLMessage msg) {
        if (msg == null || messages.contains(msg))
            return false;
        return messages.add(msg);
    }

    public UMLMessage getMessageAt(int pos) {
        try {
            return messages.get(pos);
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    public int getMessagePosition(UMLMessage msg) {
        if (msg == null)
            return -1;
        return messages.indexOf(msg);
    }

    public boolean moveMessageIntoPosition(UMLMessage msg, int newPos) {
        // TODO:
        return false;
    }

    public boolean removeMessage(UMLMessage msg) {
        if (msg == null)
            return false;
        return messages.remove(msg);
    }

    public boolean removeMessage(int pos) {
        var msg = getMessageAt(pos);
        if (msg == null)
            return false;
        return messages.remove(msg);
    }

    public void close() {
        for (var o : objects)
            o.close();
        for (var m : messages)
            m.close();
    }

    @Override
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
