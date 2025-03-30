package context.operation.command.transaction;

import context.operation.command.Command;
import context.operation.command.UndoCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class Transaction implements UndoCommand {
    private final Collection<Command> commands;

    private final LinkedList<Command> rollbackCommands;

    public Transaction(Collection<Command> commands) {
        this.commands = new ArrayList<>(commands);
        this.rollbackCommands = new LinkedList<>();
    }

    public Collection<Command> getCommands() {
        return commands;
    }

    @Override
    public boolean execute() {
        var transactionFailed = false;

        for (var command : commands) {
            var success = command.execute();

            if (success) rollbackCommands.addFirst(command);
            else {
                transactionFailed = true;
                break;
            }
        }

        if (transactionFailed) {
            rollbackCommands.stream()
                    .filter(m -> m instanceof UndoCommand)
                    .forEach(m -> ((UndoCommand) m).unexecute());
        }

        rollbackCommands.clear();
        return !transactionFailed;
    }

    @Override
    public void unexecute() {
        commands.stream()
                .filter(m -> m instanceof UndoCommand)
                .forEach(m -> ((UndoCommand) m).unexecute());
    }
}
