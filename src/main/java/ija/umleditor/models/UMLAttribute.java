package ija.umleditor.models;

import java.util.Objects;

public class UMLAttribute extends Element {

    protected String visibility;
    protected UMLClassifier type;

    /**
     * Class {@code UMLAttribute} constructor
     * @param name Attribute's name
     * @param type Attribute's type
     */
    public UMLAttribute(String name, UMLClassifier type) {
        super(name);
        this.type = type;
        visibility = "";
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
    }

    @Override
    public String toString() {
        return visibility + type + " " + name;
    }
}
