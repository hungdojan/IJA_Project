package ija.umleditor.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Objects;

public class UMLAttribute extends Element {

    protected String visibility;
    protected UMLClassifier type;
    protected StringProperty toStringProperty = new SimpleStringProperty();

    /**
     * Class {@code UMLAttribute} constructor
     * @param name Attribute's name
     * @param type Attribute's type
     */
    public UMLAttribute(String name, UMLClassifier type) {
        super(name);
        this.type = type;
        visibility = "";
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
        toStringProperty.set(visibility + type.getName() + " " + getName());
    }

    protected void setToStringName(String value) {
        toStringProperty.set(value);
    }

    /**
     * Returns attribute's visibility
     * @return Attribute's visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets attribute's visibility.
     * Given character will be displayed at the beginning of the {@code toString} output.
     * @param visibility Visibility type character
     */
    public void setVisibility(Character visibility) {
        if (visibility == null || visibility == '\0') {
            this.visibility = "";
        } else {
            this.visibility = visibility + " ";
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
        this.type = type;
        updateName();
    }

    @Override
    public String toString() {
        return visibility + type.getName() + " " + getName();
    }
}
