package ija.umleditor.models;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.*;

// TODO: implement ISubject
public class UMLObject extends Element implements IObserver, ISubject {

    private double x = 0;
    private final Set<IObserver> observers;
    private UMLClass classOfInstance;
    private final StringProperty toStringProperty = new SimpleStringProperty();

    public StringProperty getToStringProperty() {
        return toStringProperty;
    }

    public double getX() {
        return x;
    }

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
        // this.classOfInstance = classOfInstance;
        this.classOfInstance.attach(this);
        updateName();
        // toStringProperty.bind(Bindings.concat(this.classOfInstance.nameProperty, " : ", nameProperty));
//        toStringProperty.set(classOfInstance.getName() + " : " + name);
    }

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
     * Checks if object contains given operation
     * @param operation Asked operation
     * @return true if object contains given operation
     */
    public boolean containsOperation(UMLOperation operation) {
        // return classOfInstance.lofOperations.contains(operation);
        return false;
    }

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
