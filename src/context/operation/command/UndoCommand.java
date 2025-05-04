package context.operation.command;

public interface UndoCommand extends Command {
    void unexecute();
    UndoCommand copy();

}
