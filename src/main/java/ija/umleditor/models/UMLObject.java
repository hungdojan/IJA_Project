package ija.umleditor.models;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: implement ISubject
public class UMLObject extends Element implements IObserver {

    private UMLClass classOfInstance;
//    private final List<UMLInstancePeriod> instancePeriods;

    /**
     * Class UMLObject constructor
     * @param name              Object's name
     * @param classOfInstance   Referenced class
     */
    public UMLObject(String name, UMLClass classOfInstance) {
        super(name);
        this.classOfInstance = classOfInstance;
        classOfInstance.attach(this);
    }

    /**
     * Returns object's referenced class
     * @return Referenced class
     */
    public UMLClass getClassOfInstance() {
        return classOfInstance;
    }

    public void setClassOfInstance(UMLClass newClass) {
        if (classOfInstance != SequenceDiagram.undefClass)
            classOfInstance.detach(this);
        classOfInstance = newClass;
        if (classOfInstance != SequenceDiagram.undefClass)
            classOfInstance.attach(this);
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

    @Override
    public void update(String msg) {
        if (Objects.equals(msg, "DELETE")) {
            setClassOfInstance(SequenceDiagram.undefClass);
        }
    }

    @Override
    public JSONObject createJsonObject() {
        return null;
    }

//    /**
//     * Create new instance period of the object
//     * @param name  Instance period's name
//     * @param start Instance period starting position
//     * @param end   Instance period ending position
//     * @return Newly created object's instance period; null when unsuccessful
//     */
//    public UMLInstancePeriod addNewPeriod(String name, int start, int end) {
//        /*
//        TODO: check name's existence and clear period
//         */
//        UMLInstancePeriod ip = instancePeriods.stream().filter(x -> Objects.equals(x.getName(), name)).findFirst().orElse(null);
//        if (ip != null)
//            return null;
//
//        ip = new UMLInstancePeriod(name, this, start, end);
//        instancePeriods.add(ip);
//        return ip;
//    }
//
//    /**
//     * Returns an instance period of the object at a given position
//     * @param pos Asked position
//     * @return Found instance; null otherwise
//     */
//    public UMLInstancePeriod getPeriodAtPosition(int pos) {
//        return instancePeriods.stream().filter(x -> x.periodInRange(pos)).findFirst().orElse(null);
//    }
//
//    /**
//     * Remove instance period at a given position
//     * @param pos Asked position
//     * @return true when operation ended successfully
//     */
//    public boolean removePeriod(int pos) {
//        UMLInstancePeriod ip = instancePeriods.stream().filter(x -> x.periodInRange(pos)).findFirst().orElse(null);
//        return instancePeriods.remove(ip);
//    }
//
//    /**
//     * Remove instance period
//     * @param instancePeriod Instance to remove
//     * @return true when operation ended successfully
//     */
//    public boolean removePeriod(UMLInstancePeriod instancePeriod) {
//        return instancePeriods.remove(instancePeriod);
//    }

}
