package ija.umleditor.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.Objects;

public class UMLAttribute extends Element implements IObserver {

    protected Character visibility;
    protected UMLClassifier type;
    protected StringProperty toStringProperty = new SimpleStringProperty();
    public UMLOperation parent = null;

    /**
     * Class {@code UMLAttribute} constructor
     * @param name Attribute's name
     * @param type Attribute's type
     */
    public UMLAttribute(String name, UMLClassifier type) {
        super(name);
        if (type == null)
            type = ClassDiagram.undefClassifier;
        if (type != ClassDiagram.undefClassifier) {
            this.type = type;
            this.type.attach(this);
        }
        visibility = '+';
        toStringProperty.set(visibility + type.getName() + " " + name);
    }

    /**
     * Updates attributes name
     * @param name Element's name
     */
    public void setName(String name) {
        super.setName(name);
        toStringProperty.set(visibility + type.getName() + " " + name);
    }

    public StringProperty getToStringProperty() {
        return toStringProperty;
    }

    protected void updateName() {
        toStringProperty.setValue(visibility + type.getName() + " " + getName());
        if (parent != null)
            parent.updateName();
    }

    protected void setToStringName(String value) {
        toStringProperty.set(value);
    }

    /**
     * Returns attribute's visibility
     * @return Attribute's visibility
     */
    public Character getVisibility() {
        return visibility;
    }

    /**
     * Sets attribute's visibility.
     * Given character will be displayed at the beginning of the {@code toString} output.
     * @param visibility Visibility type character
     */
    public void setVisibility(Character visibility) {
        if (visibility == null || visibility == '\0') {
            this.visibility = ' ';
        } else {
            this.visibility = visibility;
        }
        updateName();
    }

    /**
     * Returns attribute's type
     * @return Attribute's type
     */
    public UMLClassifier getType() {
        return type;
    }

    /**
     * Sets new attribute type
     * @param type New attribute's type
     */
    public void setType(UMLClassifier type) {
        if (this.type != ClassDiagram.undefClassifier)
            this.type.detach(this);
        this.type = type;
        if (this.type != ClassDiagram.undefClassifier)
            this.type.attach(this);
        updateName();
    }

    /**
     * Clear resources used as observer.
     */
    public void close() {
        if (type != ClassDiagram.undefClassifier)
            type.detach(this);
    }

    @Override
    public void update(String msg) {
        if (Objects.equals(msg, "DELETE")) {
            type = ClassDiagram.undefClassifier;
            updateName();
        } else if (Objects.equals(msg, "UPDATE")) {
            updateName();
        }
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject object = new JSONObject();
        object.put("_class", "UMLAttribute");
        object.put("name", nameProperty.getValue());
        object.put("type", type.getName());
        object.put("visibility", visibility);
        return object;
    }

    @Override
    public String toString() {
        String ret = "";
        if (visibility != 0)
            ret += visibility;
        return type.getName() + " " + getName();
    }
}
