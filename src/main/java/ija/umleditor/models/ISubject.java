/**
 * @brief Interface for attach, detach and notify actions.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file ISubject.java
 * @date 06/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

/**
 * Interface for attach, detach and notify action.
 */
public interface ISubject {

    /**
     * Adds observer to the set of observers
     * @param observer Instance of IObserver
     */
    void attach(IObserver observer);

    /**
     * Removes observer from the set of observers
     * @param observer Instance of IObserver
     */
    void detach(IObserver observer);

    /**
     * Sends message to all the observers.
     * @param msg Message to send.
     */
    void notify(String msg);
}
