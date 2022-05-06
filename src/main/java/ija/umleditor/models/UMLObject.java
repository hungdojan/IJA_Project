/**
 * @brief Declaration of UMLObject class.
 * UMLClass represents abstract structure of user-defined object in the sequence diagram.
 * It is made from text label resembling rectangle which states type and name of the object and
 * dashed line that represents lifeline of the object.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file UMLObject.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.*;

/**
 * Declaration of UMLObject class.
 */
public class UMLObject extends Element implements IObserver, ISubject {

    private double x = 0;
    private final Set<IObserver> observers;
    private UMLClass classOfInstance;
    private final StringProperty toStringProperty = new SimpleStringProperty();

    /**
     * Returns instance of StringProperty.
     * @return Instance of StringProperty
     */
    public StringProperty getToStringProperty() {
        return toStringProperty;
    }

    /**
     * Gets the X coordinate of object.
     * @return X coordinate of type double
     */
    public double getX() {
        return x;
    }

    /**
     * Sets X coordinate of object.
     * @param x number to set as X coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
    }

    /**
     * Class UMLObject constructor
     * @param name              Object's name
     * @param classOfInstance   Referenced class
     */
    public UMLObject(String name, UMLClass classOfInstance) {
        super(name);
        if (classOfInstance == null)
            classOfInstance = SequenceDiagram.undefClass;
        setClassOfInstance(classOfInstance);
        observers = new HashSet<>();
        this.classOfInstance.attach(this);
        updateName();
    }

    /**
     * Updates name and its bindings.
     */
    public void updateName() {
        toStringProperty.bind(Bindings.concat(classOfInstance.nameProperty, " : ", nameProperty));
    }

    /**
     * Returns object's referenced class
     * @return Referenced class
     */
    public UMLClass getClassOfInstance() {
        return classOfInstance;
    }

    /**
     * Updates object's referenced class,
     * when null is passed, special undef class is set to prevent null pointer exception
     * @param newClass Instance of UMLClass
     */
    public void setClassOfInstance(UMLClass newClass) {
        if (classOfInstance != null && classOfInstance != SequenceDiagram.undefClass)
            classOfInstance.detach(this);
        classOfInstance = newClass;
        if (classOfInstance == null)
            classOfInstance = SequenceDiagram.undefClass;
        if (classOfInstance != SequenceDiagram.undefClass)
            classOfInstance.attach(this);
        updateName();
    }

    /**
     * Returns list of object's operations
     * @return Object's operations
     */
    public List<UMLOperation> getOperations() {
        return classOfInstance.getOperations();
    }

    /**
     * Class destructor.
     */
    public void close() {
        if (classOfInstance != SequenceDiagram.undefClass)
            classOfInstance.detach(this);
    }

    @Override
    public void update(String msg) {
        if (Objects.equals(msg, "DELETE")) {
            setClassOfInstance(SequenceDiagram.undefClass);
        }
    }

    @Override
    public void attach(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(IObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notify(String msg) {
        for (var o : observers)
            o.update(msg);
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject object = new JSONObject();
        object.put("_class", "UMLObject");
        object.put("name", nameProperty.getValue());
        object.put("classOfInstance", classOfInstance.getName());
        return object;
    }
}
