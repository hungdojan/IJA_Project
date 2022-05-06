/**
 * @brief Declaration of UMLAttribute class.
 * UMLAttribute represents abstract structure of attributes in class element.
 * It specifies its visibility, type, name and parent.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file UMLAttribute.java
 * @date 06/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Declaration of UMLAttribute class.
 */
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

    /**
     * Gets string property of attribute to string value.
     * @return Instance of StringProperty
     */
    public StringProperty getToStringProperty() {
        return toStringProperty;
    }

    /**
     * Updates name of all bindings.
     */
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
        return type.getName() + " " + getName();
    }
}
