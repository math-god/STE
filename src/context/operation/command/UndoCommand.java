package context.operation.command;

import context.operation.command.Command;

public interface UndoCommand extends Command {
    void unexecute();
}
