package ija.umleditor.models;

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
        // TODO:
        return null;
    }

    public UMLObject getObject(String name) {
        // TODO:
        return null;
    }

    public boolean removeObject(String name) {
        // TODO:
        return false;
    }

    public boolean removeObject(UMLObject object) {
        // TODO:
        return false;
    }

    public UMLMessage addMessage(UMLObject src, UMLObject dst) {
        // TODO:
        return null;
    }

    public boolean addMessage(UMLMessage msg) {
        // TODO:
        return false;
    }

    public UMLMessage getMessageAt(int pos) {
        // TODO:
        return null;
    }

    public int getMessagePosition(UMLMessage msg) {
        // TODO:
        return -1;
    }

    public boolean moveMessageIntoPosition(UMLMessage msg, int newPos) {
        // TODO:
        return false;
    }

    public boolean removeMessage(UMLMessage msg) {
        // TODO:
        return false;
    }

    public boolean removeMessage(int pos) {
        // TODO:
        return false;
    }

    public void close() {
        // TODO:
    }

    @Override
    public JSONObject createJsonObject() {
        return null;
    }
}
