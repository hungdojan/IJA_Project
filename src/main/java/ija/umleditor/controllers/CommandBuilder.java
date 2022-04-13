package ija.umleditor.controllers;

import java.util.ArrayList;
import java.util.List;

public class CommandBuilder {
    public static interface Command {
        void undo();
        void execute();
    }

    public static class Invoker {
        List<Command> commands = new ArrayList<>();

        public void execute(Command cmd) {
            commands.add(0, cmd);
            cmd.execute();
        }

        public void undo() {
            if (commands.size() > 0) {
                Command cmd = commands.remove(0);
                cmd.undo();
            }
        }
    }
}
