package ija.umleditor.models;

import javafx.beans.property.StringProperty;

import java.util.*;
import java.util.stream.Collectors;

public class UMLOperation extends UMLAttribute {

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
        for (var item : args) {
            // remove visibility of operation parameters
            item.visibility = "";
            addParameters(item);
        }
        updateName();
    }

    /**
     * Returns collection of parameters of this operation.
     * @return Immutable collection of parameters
     */
    public List<UMLAttribute> getOperationParameters() {
        return Collections.unmodifiableList(operationParameters);
    }

    /**
     * Adds new parameter to the operation.
     * Duplicates are ignored meaning OLD INSTANCE IS PRESERVED when inserting
     * parameter with identical name.
     * @param parameter Instance of the parameter
     * @return true if successfully added; false otherwise
     */
    public boolean addParameters(UMLAttribute parameter) {
        // search for parameter with similar name
        UMLAttribute attribute = operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), parameter.getName()))
                .findFirst().orElse(null);

        boolean result = false;
        // no duplicates found
        if (attribute == null) {
            // remove visibility of parameter
            parameter.visibility = "";
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
    public UMLAttribute addOrReplaceParameters(UMLAttribute parameter) {
        // search for parameter with similar name
        UMLAttribute attribute = operationParameters.stream()
                .filter(x -> Objects.equals(x.getName(), parameter.getName()))
                .findFirst().orElse(null);

        // remove visibility of parameter
        parameter.visibility = "";
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
    public String toString() {
        return visibility + type.getName() + " " + getName() + "(" +
               operationParameters.stream().map(UMLAttribute::toString).collect(Collectors.joining(", ")) +
                ")";
    }
}
