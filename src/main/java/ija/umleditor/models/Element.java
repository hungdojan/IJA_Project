package ija.umleditor.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Element {
    protected StringProperty nameProperty = new SimpleStringProperty();

    /**
     * Class Element constructor
     * @param name Element's name
     */
    public Element(String name) {
        nameProperty = new SimpleStringProperty(name);
    }

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
}
