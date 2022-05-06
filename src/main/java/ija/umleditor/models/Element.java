/**
 * @brief Declaration of Element class
 * Element is base class for (almost) all elements within ClassDiagram.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22
 *
 * @file Element.java
 * @date 22/04/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

/**
 * Declaration of Element class.
 */
public abstract class Element {
    protected StringProperty nameProperty;

    /**
     * Class Element constructor
     * @param name Element's name
     */
    public Element(String name) {
        nameProperty = new SimpleStringProperty(name);
    }

    /**
     * Returns instance of StringProperty
     * @return Instance of StringProperty
     */
    public StringProperty getNameProperty() {
        return nameProperty;
    }

    /**
     * Returns element's name
     * @return Element's name
     */
    public String getName() {
        return nameProperty.get();
    }

    /**
     * Sets new element's name
     * @param name Element's name
     */
    protected void setName(String name) {
        nameProperty.set(name);
    }

    /**
     * Creates JSON representation of element's content.
     */
    public abstract JSONObject createJsonObject();
}
