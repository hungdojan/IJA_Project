/**
 * @brief Interface for update action.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file IObserver.java
 * @date 06/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.models;

/**
 * Interface for update action.
 */
public interface IObserver {
    /**
     * Update name of message and its bindings.
     * @param msg Identification message
     */
    void update(String msg);
}
