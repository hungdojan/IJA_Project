package ija.umleditor.controllers;

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
