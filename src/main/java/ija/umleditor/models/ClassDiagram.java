package ija.umleditor.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClassDiagram extends Element {

    /** Collection of classifiers */
    protected List<UMLClassifier> classElements;
    /** Collection of sequence diagrams */
    protected List<SequenceDiagram> sequenceDiagrams = null;

    /**
     * Class {@code ClassDiagram} constructor
     * @param name Class diagram name
     */
    public ClassDiagram(String name) {
        super(name);
        classElements = new ArrayList<>();
    }

    /**
     * Returns unmodifiable collection of class elements
     * @return Immutable collection of class elements
     */
    public List<UMLClassifier> getClassElements() {
        return Collections.unmodifiableList(classElements);
    }

    /**
     * Returns unmodifiable collection of sequence diagrams
     * @return Immutable collection of sequence diagram
     */
    public List<SequenceDiagram> getSequenceDiagrams() {
        return Collections.unmodifiableList(sequenceDiagrams);
    }

    /**
     * Loads content saved in the JSON file.
     * @param path File path
     */
    public static ClassDiagram initClassDiagramFromFile(String path) {
        // TODO:
        return null;
    }

    /**
     * Store class diagram to the file.
     * Content is stored in JSON format.
     * @param classDiagram Instance of ClassDiagram to store
     * @param path         Path to file
     */
    public static void saveClassDiagramToFile(ClassDiagram classDiagram, String path) {
        // TODO:
    }

    /**
     * Factory method that creates new classifier.
     * User can decide whether to create new classifier or specific class.
     * @param name    Classifier name
     * @param isClass Creates new UMLClass if true
     * @return Created classifier
     */
    public static UMLClassifier createClassifier(String name, boolean isClass) {
        if (isClass)
            return new UMLClass(name);
        return new UMLClassifier(name);
    }

    /**
     * Adds new classifier to the class diagram.
     * If classifier with same name already exists in the diagram OLD ELEMENT IS PRESERVED.
     * @param classifier New classifier
     * @return true when element was added successfully; false otherwise
     */
    public boolean addClassifier(UMLClassifier classifier) {
        // search for classifier with identical name
        UMLClassifier c = classElements.stream()
                .filter(x -> Objects.equals(x.name, classifier.name))
                .findFirst().orElse(null);

        // no classifier with identical name found
        if (c == null) {
            return classElements.add(classifier);
        }
        return false;
    }

    /**
     * Adds new classifier to the class diagram.
     * If classifier with same name already exists in the diagram OLD ELEMENT IS REPLACED BY THE NEW ONE.
     * @param classifier New classifier
     * @return Old classifier is returned when it has been replaced by new one; null when diagram didn't contain classifier with identical name
     */
    public UMLClassifier addOrReplaceClassifier(UMLClassifier classifier) {
        // search for classifier with identical name
        UMLClassifier c = classElements.stream()
                .filter(x -> Objects.equals(x.name, classifier.name))
                .findFirst().orElse(null);

        classElements.add(classifier);
        // remove old classifier and return it
        if (c != null) {
            classElements.remove(c);
        }
        return c;
    }

    /**
     * Add multiple classifiers. Only classifiers with unique name are added.
     * @param classifiers Indefinite number of UMLClassifier instances
     */
    public void addClassifiers(UMLClassifier...classifiers) {
        for (var item : classifiers) {
            addClassifier(item);
        }
    }

    /**
     * Search for a classifier in class diagram.
     * @param name Name of the classifier
     * @return Found classifier in diagram; null if not found
     */
    public UMLClassifier getClassifier(String name) {
        return classElements.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);
    }

    /**
     * Search for a class in class diagram.
     * @param name Name of the class
     * @return Found class in diagram; null if not found
     */
    public UMLClass getClass(String name) {
        // search for class with identical name
        return (UMLClass) classElements.stream()
                .filter(x -> Objects.equals(x.name, name) && x instanceof UMLClass)
                .findFirst().orElse(null);
    }

    public boolean isInClassDiagram(UMLClassifier classifier) {
        return classElements.contains(classifier);
    }

    /**
     * Removes classifier from the class diagram.
     * When classifier is found in the class diagram, element is removed from the class diagram.
     * If given classifier is also instance of UMLClass, all its relations are removed.
     * @param name Name of the classifier
     * @return true if element was found and removed; false otherwise
     */
    public boolean removeClassElement(String name) {
        // search for classifier with identical name
        UMLClassifier classifier = classElements.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);

        // remove all relations of the class
        if (classifier instanceof UMLClass) {
            ((UMLClass) classifier).clearAllRelations();
        }
        return classElements.remove(classifier);
    }

    /**
     * Removes classifier from the class diagram.
     * When classifier is found in the class diagram, element is removed from the class diagram.
     * If given classifier is also instance of UMLClass, all its relations are removed.
     * @param classElement Instance of classifier to remove
     * @return true if element was found and removed; false otherwise
     */
    public boolean removeClassElement(UMLClassifier classElement) {
        // remove all relations of the class
        if (classElements.contains(classElement) && classElement instanceof UMLClass) {
            ((UMLClass) classElement).clearAllRelations();
        }
        return classElements.remove(classElement);
    }

    /**
     * Creates and adds new sequence diagram to the class diagram.
     * Old sequence diagram is PRESERVED if class diagram already contains sequence diagram with identical name.
     * @param name Name of new sequence diagram
     * @return Instance of newly created SequenceDiagram; null when no diagram was created.
     */
    public SequenceDiagram addSequenceDiagram(String name) {
        // search for sequence diagram with identical name
        SequenceDiagram sd = sequenceDiagrams.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);

        // no diagram was found
        if (sd == null) {
            sd = new SequenceDiagram(name);
            sequenceDiagrams.add(sd);
        }
        return sd;
    }

    /**
     * Removes instance of sequence diagram with a given name from class diagram.
     * Returns removed diagram if found.
     * @param name Name of the sequence diagram
     * @return Found and removed sequence diagram; null otherwise
     */
    public SequenceDiagram removeSequenceDiagram(String name) {
        // search for sequence diagram with identical name
        SequenceDiagram sd = sequenceDiagrams.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);

        sequenceDiagrams.remove(sd);
        return sd;
    }

    /**
     * Removes instance of sequence diagram from class diagram.
     * @param sd Instance of sequence diagram
     * @return true if found and removed; false otherwise
     */
    public boolean removeSequenceDiagram(SequenceDiagram sd) {
        return sequenceDiagrams.remove(sd);
    }
}
