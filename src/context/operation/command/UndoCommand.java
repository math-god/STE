package context.operation.command;

import context.operation.state.State;
import output.Consumer;

public abstract class UndoCommand extends Command {

    private boolean undoComplete;

    public UndoCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    public boolean isUndoComplete() {
        return undoComplete;
    }

    public abstract void unexecute();

    public abstract UndoCommand copy();

    protected void setUndoComplete(boolean value) {
        undoComplete = value;
    }
}
