package context.operation;

public interface UndoCommand extends Command {
    void unexecute();
}
