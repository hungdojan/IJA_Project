/**
 * @brief Interface for undo, redo and execute actions.
 *
 * This source code serves as submission for semester assignment of class IJA at FIT, BUT 2021/22.
 *
 * @file ICommand.java
 * @date 06/05/2022
 * @authors Hung Do      (xdohun00)
 *          Petr Kolarik (xkolar79)
 */
package ija.umleditor.controllers;

/**
 * Interface for undo, redo and execute actions.
 */
public interface ICommand {
    /**
     * Undo command.
     */
    void undo();

    /**
     * Redo command.
     */
    void redo();

    /**
     * Execute command.
     */
    void execute();
}
