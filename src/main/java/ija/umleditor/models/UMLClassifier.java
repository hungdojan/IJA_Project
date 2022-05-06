/**
 * @brief Definition of UMLClassifier class.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file UMLClassifier.java
 * @date 03/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Declaration of UMLClassifier class.
 */
public class UMLClassifier extends Element implements ISubject {
    private final boolean isUserDefined;
    private final Set<IObserver> observers;
    protected double x;
    protected double y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Class {@code UMLClassifier} constructor.
     * Classifier is not user defined.
     * @param name Name of the classifier
     */
    public UMLClassifier(String name) {
        super(name);
        isUserDefined = false;
        observers = new HashSet<>();
    }

    /**
     * Class {@code UMLClassifier} constructor.
     * @param name           Name of the classifier
     * @param isUserDefined  Bool value of classifier definition status
     */
    public UMLClassifier(String name, boolean isUserDefined) {
        super(name);
        this.isUserDefined = isUserDefined;
        observers = new HashSet<>();
    }

    /**
     * Returns definition status of this classifier.
     * @return Is this classifier user defined?
     */
    public boolean isUserDefined() {
        return isUserDefined;
    }

    public Set<IObserver> getObservers() {
        return observers;
    }

    /**
     * Sets new classifier name
     * @param name Element's name
     */
    public void setName(String name) {
        super.setName(name);
        notify("UPDATE");
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
        for (var o : observers) {
            o.update(msg);
        }
    }

    public void close() {
        notify("DELETE");
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject object = new JSONObject();
        object.put("_class", "UMLClassifier");
        object.put("name", nameProperty.getValue());
        object.put("isUserDefined", isUserDefined);
        object.put("x", x);
        object.put("y", y);
        return object;
    }
}
