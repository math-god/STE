package context.operation.command.abstraction;

public interface Command {
    void execute();
    boolean canUndo();
    boolean isUndoComplete();

    void unexecute();

    Command copy();
}
