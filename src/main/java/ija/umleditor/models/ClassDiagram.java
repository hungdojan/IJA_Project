package ija.umleditor.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ClassDiagram extends Element {

    /** Collection of classifiers */
    protected List<UMLClassifier> classElements;
    /** Collection of sequence diagrams */
    protected List<SequenceDiagram> sequenceDiagrams = null;

    public static UMLClassifier undefClassifier = new UMLClassifier("#UNDEF");

    /**
     * Class {@code ClassDiagram} constructor
     */
    public ClassDiagram() {
        super("");
        classElements = new ArrayList<>();
    }

    /**
     * Class {@code ClassDiagram} constructor
     * @param name Class diagram name
     */
    public ClassDiagram(String name) {
        super(name);
        classElements = new ArrayList<>();
    }

    /**
     * Sets new class diagram name
     * @param name New class diagram's name
     */
    public void setName(String name) {
        super.setName(name);
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
        System.out.println("Imma save this shit");
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
                .filter(x -> Objects.equals(x.getName(), classifier.getName()))
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
                .filter(x -> Objects.equals(x.getName(), classifier.getName()))
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
     * Updates classifier's name.
     * If classifier is not define in the diagram or
     * other classifier with new name is already defined, operation is halted and class diagram is unchanged.
     * @param classifier Instance of classifier
     * @param name       New classifier's name
     * @return true if name is successfully changed; false when {@code classifier} is not in the diagram
     *          or name is already occupied
     */
    public boolean changeClassifierName(UMLClassifier classifier, String name) {
        if (!classElements.contains(classifier))
            return false;

        var c = getClassifier(name);
        if (c != null)
            return false;

        classifier.setName(name);
        return true;
    }

    /**
     * Updates classifier's name.
     * If there is no classifier with {@code oldName}
     * or {@code newName} is already occupied, operation is halted and class diagram is unchanged.
     * @param oldName Old classifier's name
     * @param newName New classifier's name
     * @return true if name is successfully changed; false when classifier with {@code oldName}
     *          is not in the diagram or {@code newName} is already occupied
     */
    public boolean changeClassifierName(String oldName, String newName) {
        var oldClassifier = getClassifier(oldName);
        var newClassifier = getClassifier(newName);
        if (oldClassifier == null || newClassifier != null)
            return false;

        oldClassifier.setName(newName);
        return true;
    }

    /**
     * Search for a classifier in class diagram.
     * @param name Name of the classifier
     * @return Found classifier in diagram; null if not found
     */
    public UMLClassifier getClassifier(String name) {
        return classElements.stream()
                .filter(x -> Objects.equals(x.getName(), name))
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
                .filter(x -> Objects.equals(x.getName(), name) && x instanceof UMLClass)
                .findFirst().orElse(null);
    }

    public List<UMLClass> getClasses() {
        List<UMLClass> classes = new ArrayList<>();
        for (var classifier : classElements) {
            if (classifier instanceof UMLClass)
                classes.add((UMLClass) classifier);
        }
        return classes;
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
                .filter(x -> Objects.equals(x.getName(), name))
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
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst().orElse(null);

        // no diagram was found
        if (sd == null) {
            sd = new SequenceDiagram(name);
            sequenceDiagrams.add(sd);
        }
        return sd;
    }

    public boolean addSequenceDiagram(SequenceDiagram sd) {
        // TODO:
        return false;
    }

    public SequenceDiagram getSequenceDiagram(String name) {
        return null;
    }

    /**
     * Add indefinite number of sequence diagrams.
     * Duplicates are skipped.
     * @param diagrams Indefinite number of sequence diagrams.
     */
    public void addSequenceDiagrams(SequenceDiagram... diagrams) {

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
                .filter(x -> Objects.equals(x.getName(), name))
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

    /**
     * Clear collection of sequence diagrams.
     */
    public void clearSequenceDiagrams() {
        sequenceDiagrams.clear();
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject object = new JSONObject();
        object.put("_class", "ClassDiagram");
        object.put("name", nameProperty.getValue());
        Set<UMLRelation> setOfRelations = new HashSet<>();

        // add class elements
        JSONArray jsonClassifiers = new JSONArray();
        for (var classifier : classElements) {
            if (!(classifier instanceof UMLClass) && classifier.getObservers().size() < 1)
                continue;
            var jsonClassifier = classifier.createJsonObject();
            jsonClassifiers.put(jsonClassifier);
            if (classifier instanceof UMLClass)
                setOfRelations.addAll(((UMLClass) classifier).relations);
        }
        object.put("classElements", jsonClassifiers);

        // add sequence diagrams
        // JSONArray jsonSequenceDiagrams = new JSONArray();
        // for (var sequenceDiagram : sequenceDiagrams) {
        //     var jsonSequenceDiagram = sequenceDiagram.createJsonObject();
        //     jsonSequenceDiagrams.put(jsonSequenceDiagram);
        // }
        // object.append("sequenceDiagrams", jsonSequenceDiagrams);

        // // add relations
        // JSONArray jsonRelations = new JSONArray();
        // for (var relation : setOfRelations) {
        //     var jsonRelation = relation.createJsonObject();
        //     jsonRelations.put(jsonRelation);
        // }
        // object.append("relations", jsonRelations);
        return object;
    }
}
