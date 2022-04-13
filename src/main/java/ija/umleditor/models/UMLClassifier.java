package ija.umleditor.models;

public class UMLClassifier extends Element {
    private final boolean isUserDefined;

    /**
     * Class {@code UMLClassifier} constructor.
     * Classifier is not user defined.
     * @param name Name of the classifier
     */
    public UMLClassifier(String name) {
        super(name);
        isUserDefined = false;
    }

    /**
     * Class {@code UMLClassifier} constructor.
     * @param name           Name of the classifier
     * @param isUserDefined  Bool value of classifier definition status
     */
    public UMLClassifier(String name, boolean isUserDefined) {
        super(name);
        this.isUserDefined = isUserDefined;
    }

    /**
     * Returns definition status of this classifier.
     * @return Is this classifier user defined?
     */
    public boolean isUserDefined() {
        return isUserDefined;
    }

    /**
     * Sets new classifier name
     * @param name Element's name
     */
    public void setName(String name) {
        super.setName(name);
    }
}
