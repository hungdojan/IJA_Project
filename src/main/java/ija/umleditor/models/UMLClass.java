/**
 * @brief Declaration of UMLClass class.
 * UMLClass represents abstract structure of user-defined class in the class diagram. It contains attributes and
 * operations that objects from this class can use in sequence diagram. Class can be set as abstract meaning
 * sequence diagrams cannot create object from this class. Class can derive from other classes or form other types
 * of relations between them.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file UMLClass.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Declaration of UMLClass class.
 */
public class UMLClass extends UMLClassifier {

    private int attributeCounter = 1;
    private int operationCounter = 1;
    private final BooleanProperty abstractProperty = new SimpleBooleanProperty(false);
    private final StringProperty stereotypeProperty = new SimpleStringProperty();
    protected List<UMLAttribute> attributes;
    protected Set<UMLRelation> relations = new HashSet<>();
    protected Set<UMLClass> parentClasses = new HashSet<>();

    /**
     * Class {@code UMLClass} constructor
     * @param name Element's name
     */
    public UMLClass(String name) {
        super(name, true);
        attributes = new ArrayList<>();
    }

    /**
     * Returns number of attributes.
     * @return Number of attributes
     */
    public int getAttributeCounter() {
        return attributeCounter;
    }

    /**
     * Returns number of operations.
     * @return Number of operations.
     */
    public int getOperationCounter() {
        return operationCounter;
    }

    /**
     * Returns stereotype of class.
     * @return Stereotype
     */
    public String getStereotype() {
        return stereotypeProperty.get();
    }

    /**
     * Sets stereotype to class.
     * @param stereotype Stereotype to set
     */
    public void setStereotype(String stereotype) {
        stereotypeProperty.set(stereotype);
    }

    /**
     * Returns if property is abstract.
     * @return Boolean value if property is abstract.
     */
    public BooleanProperty getAbstractProperty() {
        return abstractProperty;
    }

    /**
     * Returns stereotype as instance of StringProperty
     * @return Instance of StringProperty
     */
    public StringProperty getStereotypeProperty() {
        return stereotypeProperty;
    }

    /**
     * Class {@code UMLClass} constructor
     * @param name          Element's name
     * @param memberFields  Collection of {@code UMLAttributes} and {@code UMLOperations}
     */
    public UMLClass(String name, UMLAttribute...memberFields) {
        super(name);
        this.attributes.addAll(List.of(memberFields));
        attributeCounter += memberFields.length;
        operationCounter += memberFields.length;
    }

    /**
     * Checks if class is abstract.
     * @return true if class is abstract; false otherwise
     */
    public boolean isAbstract() {
        return abstractProperty.get();
    }

    /**
     * Update class abstract status.
     * @param anAbstract New abstract status
     */
    public void setAbstract(boolean anAbstract) {
        abstractProperty.set(anAbstract);
    }

    /**
     * Returns read-only collection of class attributes (attributes and operations).
     * @return Read-only collection of class attributes and operations
     */
    public List<UMLAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }

    /**
     * Returns set of relations associated with this class.
     * @return Immutable set of relations
     */
    public Set<UMLRelation> getRelations() {
        return Collections.unmodifiableSet(relations);
    }

    /**
     * Factory function that creates new attribute.
     * When no {@code args} are passed, function creates {@code UMLAttribute}, otherwise {@code UMLOperation} is created.
     * @param isOperation Choose whether to create operation or attribute
     * @param name Name of the attribute
     * @param type (Return) type of the attribute
     * @param args Indefinite number of operation arguments (optional)
     * @return Newly created instance of {@code UMLAttribute}
     */
    public static UMLAttribute createAttribute(boolean isOperation, String name, UMLClassifier type, UMLAttribute...args) {
        // FIXME: documentation
        if (!isOperation)
            return new UMLAttribute(name, type);
        return new UMLOperation(name, type, args);
    }

    /**
     * Adds new attribute to the class.
     * If attribute with identical name as given {@code attr} already exists in class OLD INSTANCE IS PRESERVED.
     * @param attr Attribute to add
     * @return true if insertion ended successfully; false otherwise
     */
    public boolean addAttribute(UMLAttribute attr) {
        // search for attribute with identical name
        UMLAttribute attribute = attributes.stream()
                .filter(x -> Objects.equals(x.getName(), attr.getName()))
                .findFirst().orElse(null);

        // nothing found
        if (attribute == null) {
            if (attr instanceof UMLOperation)
                operationCounter++;
            else
                attributeCounter++;
            return attributes.add(attr);
        }
        return false;
    }

    /**
     * Adds attributes to class.
     * @param attrs Instances of UMLAttribute to add
     */
    public void addAttributes(UMLAttribute... attrs) {
        for (var a : attrs) {
            addAttribute(a);
        }
    }

    /**
     * Adds new attribute to the class.
     * If attribute with identical name as given {@code attr} already exists in class OLD INSTANCE IS REPLACED by the new one.
     * @param attr Attribute to add
     * @return Instance of old attribute if it was replaced; null otherwise
     */
    public UMLAttribute addOrReplaceAttribute(UMLAttribute attr) {
        // search for attribute with identical name
        UMLAttribute attribute = attributes.stream()
                .filter(x -> Objects.equals(x.getName(), attr.getName()))
                .findFirst().orElse(null);

        if (attr instanceof UMLOperation)
            operationCounter++;
        else
            attributeCounter++;
        attributes.add(attr);
        // remove old attribute
        if (attribute != null)
            attributes.remove(attribute);
        return attribute;
    }

    /**
     * Returns instance of {@code UMLAttribute} in the class by a given name
     * @param name Asked attribute's name
     * @return Found attribute; null if not found
     */
    public UMLAttribute getAttribute(String name) {
        return attributes.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst().orElse(null);
    }

    /**
     * Returns collection of all callable operations.
     * Collection contains operation of this class and operations that it inherited from the base classes.
     * @return Collection of available operations
     */
    public List<UMLOperation> getOperations() {
        // list of operations from the base classes
        List<UMLOperation> parentOperations = parentClasses.stream().flatMap(e -> e.getOperations().stream()).collect(Collectors.toList());
        // list of operations from this class
        List<UMLOperation> currentOperations = attributes.stream().filter(x -> x instanceof UMLOperation).map(x -> (UMLOperation) x).collect(Collectors.toList());
        // return concatenated list
        return Stream.concat(currentOperations.stream(), parentOperations.stream()).collect(Collectors.toList());
    }

    /**
     * Returns attribute from a given position.
     * @param pos Asked position
     * @return Found attribute; null if position out of range
     */
    public UMLAttribute getAttributeAtPosition(int pos) {
        if (pos >= attributes.size())
            return null;
        return attributes.get(pos);
    }

    /**
     * Get attributes position in the class.
     * @param attr Instance of asked attribute
     * @return Index position of the attribute; -1 if not found in the class
     */
    public int getAttributePosition(UMLAttribute attr) {
        return attributes.indexOf(attr);
    }

    /**
     * Move attribute of a given name to the given position.
     * Method terminates when position is out of range or attribute was not found.
     * @param name Attribute' name
     * @param pos Attribute's new position
     * @return true if method ended successfully; false otherwise
     */
    public boolean moveAttributeToPosition(String name, int pos) {
        UMLAttribute attr = getAttribute(name);
        int index = attributes.indexOf(attr);
        if (pos < 0 || pos >= attributes.size() || index < 0)
            return false;

        // moving process
        attributes.remove(index);
        attributes.add(pos, attr);

        return true;
    }

    public boolean updateAttributeName(UMLAttribute attr, String newName) {
        // given attribute exists in the class
        if (!attributes.contains(attr))
            return false;

        // class doesn't contain attribute with "newName"
        if (getAttribute(newName) != null)
            return false;

        attr.setName(newName);
        return true;
    }

    public boolean updateAttributeName(String oldName, String newName) {
        // given attribute exists in the class
        UMLAttribute oldAttr = getAttribute(oldName);
        // class doesn't contain attribute with "newName"
        UMLAttribute newAttr = getAttribute(newName);

        if (oldAttr != null && newAttr == null) {
            oldAttr.setName(newName);
            return true;
        }
        return false;
    }

    /**
     * Removes attribute by a given name from the class.
     * @param name Attribute's name
     * @return Instance of removed attribute when successfully removed from the class; null otherwise
     */
    public UMLAttribute removeAttribute(String name) {
        UMLAttribute attr = attributes.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst().orElse(null);
        attributes.remove(attr);
        return attr;
    }

    /**
     * Removes attribute from the class.
     * @param attr Instance of attribute
     * @return true if successfully removed; false otherwise
     */
    public boolean removeAttribute(UMLAttribute attr) {
        return attributes.remove(attr);
    }

    /**
     * Adds new parent class.
     * Parent classes are used for sharing {@code UMLOperation}s.
     * @param parentClass Instance of parent class
     * @return true if successfully added; false otherwise
     */
    public boolean addParentClass(UMLClass parentClass) {
        return parentClasses.add(parentClass);
    }

    /**
     * Removes class from the set of parent classes.
     * Parent classes are used for sharing {@code UMLOperation}s.
     * @param parentClass Instance of parent class
     * @return true if successfully removed; false otherwise
     */
    public boolean removeParentClass(UMLClass parentClass) {
        return parentClasses.remove(parentClass);
    }

    /**
     * Adds new relation between this class and {@code} class.
     * @param dest Second class
     * @param type Type of relation
     * @return true if successfully added; false otherwise
     */
    public boolean addRelation(UMLClass dest, RelationType type) {
        UMLRelation relation = new UMLRelation(this, dest);
        relation.setRelationType(type);
        return relation.setRelationDependency();
    }

    /**
     * Adds instance of relation to the class.
     * @param relation Instance of relation.
     * @return true if successfully added; false otherwise
     */
    public boolean addRelation(UMLRelation relation) {
        return relations.add(relation);
    }

    /**
     * Gets relation between this and {@code dest} class.
     * @param dest Destination class
     * @return Instance of found relation; null otherwise
     */
    public UMLRelation getRelation(UMLClass dest) {
        return relations.stream().filter(x -> x.compareClassesInRelation(this, dest)).findFirst().orElse(null);
    }

    /**
     * Removes given relation from the class.
     * @param relation Instance of relation
     * @return true if successfully removed; false otherwise
     */
    public boolean removeRelation(UMLRelation relation) {
        return relations.remove(relation);
    }

    /**
     * Removes relation with given class.
     * @param cls Destination class
     * @return true if successfully removed; false oterwise
     */
    public boolean removeRelationWithClass(UMLClass cls) {
        // search for a relation that is shared between this UMLClass and cls
        // function UMLRelation::removeRelationDependency will be called to clear dependencies of both classes
        UMLRelation relation = relations.stream()
                .filter(x -> x.compareClassesInRelation(this, cls))
                .findFirst().orElse(null);

        // clear of dependencies
        if (relation != null)
            relation.removeRelationDependency();
        return relations.remove(relation);
    }

    /**
     * Clear all relations with this class.
     */
    public void clearAllRelations() {
        relations.forEach(UMLRelation::removeRelationDependency);
        relations.clear();
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject object = super.createJsonObject();
        object.remove("_class");
        object.put("_class", "UMLClass");
        object.put("isAbstract", abstractProperty.getValue());
        JSONArray jsonAttributes = new JSONArray();
        for (var attr : attributes) {
            var jsonAttribute = attr.createJsonObject();
            jsonAttributes.put(jsonAttribute);
        }
        object.put("attributes", jsonAttributes);
        return object;
    }
}
