package context.operation.command.abstraction;

public interface UndoCommand extends Command {
    boolean isUndoComplete();

    void unexecute();

    UndoCommand copy();
}
