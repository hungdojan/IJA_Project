package ija.umleditor.models;

import java.util.*;
import java.util.stream.Collectors;

public class UMLOperation extends UMLAttribute {

    private final List<UMLAttribute> operationParameters;

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
            item.visibility = null;
            addParameters(item);
        }
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
                .filter(x -> Objects.equals(x.name, parameter.name))
                .findFirst().orElse(null);

        // no duplicates found
        if (attribute == null) {
            // remove visibility of parameter
            parameter.visibility = null;
            return operationParameters.add(parameter);
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
                .filter(x -> Objects.equals(x.name, parameter.name))
                .findFirst().orElse(null);

        // remove visibility of parameter
        parameter.visibility = null;
        operationParameters.add(parameter);
        if (attribute != null) {
            operationParameters.remove(parameter);
        }
        return attribute;
    }

    /**
     * Returns parameter with given name.
     * @param name Parameter's name
     * @return Instance of found parameter; null otherwise
     */
    public UMLAttribute getParameterByName(String name) {
        return operationParameters.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);
    }

    /**
     * Removes parameter with given name.
     * @param name Parameter's name
     * @return true if successfully removed; false otherwise
     */
    public boolean removeParameter(String name) {
        UMLAttribute parameter = operationParameters.stream()
                .filter(x -> Objects.equals(x.name, name))
                .findFirst().orElse(null);
        return operationParameters.remove(parameter);
    }

    /**
     * Removes instance of parameter from the operation
     * @param parameter Instance of parameter
     * @return true if successfully removed; false otherwise
     */
    public boolean removeParameter(UMLAttribute parameter) {
        return operationParameters.remove(parameter);
    }

    /**
     * Clear operation from parameters.
     */
    public void clearParameters() {
        operationParameters.clear();
    }

    @Override
    public String toString() {
        return visibility + type.getName() + " " + name + "(" +
               operationParameters.stream().map(UMLAttribute::toString).collect(Collectors.joining(", ")) +
                ")";
    }
}
