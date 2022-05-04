package ija.umleditor.models;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class UMLOperation extends UMLAttribute implements ISubject {

    private final List<IObserver> observers;
    private final List<UMLAttribute> operationParameters;
    private static int parameterCounter = 1;

    public static int getParameterCounter() {
        return parameterCounter++;
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        updateName();
    }
    @Override
    protected void updateName() {
        if (type == null || Objects.equals(type.getName(), "#UNDEF"))
            toStringProperty.set("#UNDEF");
        else
            toStringProperty.set(visibility + type.getName() + " " + getName() + "(" +
                    operationParameters.stream().map(UMLAttribute::toString).collect(Collectors.joining(", ")) +
                    ")");
    }
    /**
     * Class {@code UMLOperation} constructor
     * @param name Name of the operation
     * @param returnType Operation's return type
     * @param args Indefinite number of parameters; duplicates are ignored
     */
    public UMLOperation(final String name, final UMLClassifier returnType, UMLAttribute...args) {
        super(name, returnType);
        operationParameters = new ArrayList<>();
        observers = new ArrayList<>();
        for (var item : args) {
            // remove visibility of operation parameters
            item.visibility = '+';
            addParameter(item);
        }
        updateName();
    }

    /**
     * Returns collection of parameters of this operation.
     * TODO: fix
     * @return Immutable collection of parameters
     */
    public List<UMLAttribute> getOperationParameters() {
        return operationParameters;
    }

    /**
     * Adds new parameter to the operation.
     * Duplicates are ignored meaning OLD INSTANCE IS PRESERVED when inserting
     * parameter with identical name.
     * @param parameter Instance of the parameter
     * @return true if successfully added; false otherwise
     */
    public boolean addParameter(UMLAttribute parameter) {
        // search for parameter with similar name
        UMLAttribute attribute = operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), parameter.getName()))
                .findFirst().orElse(null);

        boolean result = false;
        // no duplicates found
        if (attribute == null) {
            // remove visibility of parameter
            parameter.visibility = 0;
            result = operationParameters.add(parameter);
            updateName();
        }
        return false;
    }

    // TODO: change position

    /**
     * Adds new parameter to the operation.
     * When parameter with identical name already exists in the operation OLD INSTANCE IS REPLACED by new one.
     * @param parameter Instance of parameter
     * @return
     */
    public UMLAttribute addOrReplaceParameter(UMLAttribute parameter) {
        // search for parameter with similar name
        UMLAttribute attribute = operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), parameter.getName()))
                .findFirst().orElse(null);

        // remove visibility of parameter
        parameter.visibility = 0;
        operationParameters.add(parameter);
        if (attribute != null) {
            operationParameters.remove(parameter);
        }
        updateName();
        return attribute;
    }

    /**
     * Returns parameter with given name.
     * @param name Parameter's name
     * @return Instance of found parameter; null otherwise
     */
    public UMLAttribute getParameterByName(String name) {
        return operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst().orElse(null);
    }

    /**
     * Removes parameter with given name.
     * @param name Parameter's name
     * @return true if successfully removed; false otherwise
     */
    public boolean removeParameter(String name) {
        UMLAttribute parameter = operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .findFirst().orElse(null);
        boolean result = operationParameters.remove(parameter);
        updateName();
        return result;
    }

    public void update() {
        updateName();
    }

    public boolean updateParameter(String oldName, String newName) {
        UMLAttribute oldParam = getParameterByName(oldName);
        UMLAttribute newParam = getParameterByName(newName);
        if (oldParam != null && newParam == null) {
            oldParam.setName(newName);
            updateName();
            return true;
        }
        return false;
    }
    /**
     * Removes instance of parameter from the operation
     * @param parameter Instance of parameter
     * @return true if successfully removed; false otherwise
     */
    public boolean removeParameter(UMLAttribute parameter) {
        boolean result = operationParameters.remove(parameter);
        updateName();
        return result;
    }

    /**
     * Clear operation from parameters.
     */
    public void clearParameters() {
        operationParameters.clear();
        updateName();
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
    public List<IObserver> getObservers() {
        return observers;
    }

    @Override
    public JSONObject createJsonObject() {
        var object = super.createJsonObject();
        object.remove("_class");
        object.put("_class", "UMLOperation");
        JSONArray jsonParameters = new JSONArray();
        for (var param : operationParameters) {
            JSONObject jsonParameter = param.createJsonObject();
            jsonParameters.put(jsonParameter);
        }
        object.put("operationParameters", jsonParameters);
        return object;
    }

    @Override
    public String toString() {
        return visibility + type.getName() + " " + getName() + "(" +
                operationParameters.stream().map(UMLAttribute::toString).collect(Collectors.joining(", ")) +
                ")";
    }
}
