package ija.umleditor.models;

public class Element {
    protected String name;

    /**
     * Class Element constructor
     * @param name Element's name
     */
    public Element(String name) {
        this.name = name;
    }

    /**
     * Returns element's name
     * @return Element's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new element's name
     * @param name Element's name
     */
    public void setName(String name) {
        this.name = name;
    }
}
