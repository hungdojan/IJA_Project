package ija.umleditor.controllers;

import java.util.ArrayList;
import java.util.List;

/**
 * Class contains undo and redo stacks.
 */
public class CommandBuilder {

    private final List<ICommand> undoStack = new ArrayList<>();
    private final List<ICommand> redoStack = new ArrayList<>();

    /**
     * Load command into the structure and executes command.
     * @param cmd Instance of Command defining action.
     */
    public void execute(ICommand cmd) {
        redoStack.clear();
        undoStack.add(0, cmd);
        cmd.execute();
    }

    /**
     * Executes undo command.
     */
    public void undo() {
        if (undoStack.isEmpty())
            return;
        var cmd = undoStack.get(0);
        cmd.undo();
        undoStack.remove(0);
        redoStack.add(0, cmd);
    }

    /**
     * Executes redo command.
     */
    public void redo() {
        if (redoStack.isEmpty())
            return;
        var cmd = redoStack.get(0);
        cmd.redo();
        redoStack.remove(0);
        undoStack.add(0, cmd);
    }

    /**
     * Clears both stacks.
     */
    public void reset() {
        undoStack.clear();
        redoStack.clear();
    }
}
