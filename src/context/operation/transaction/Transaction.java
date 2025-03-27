package context.operation.transaction;

import context.operation.Command;
import context.operation.UndoCommand;

import java.util.Collection;

public class Transaction implements UndoCommand {
    private final Collection<Command> commands;

    public Transaction(Collection<Command> commands) {
        this.commands = commands;
    }

    public Collection<Command> getCommands() {
        return commands;
    }

    @Override
    public void execute() {
        commands.forEach(Command::execute);
    }

    @Override
    public void unexecute() {
        commands.stream()
                .filter(m -> m instanceof UndoCommand)
                .forEach(m -> ((UndoCommand) m).unexecute());
    }
}
