package context.operation.command.editor;

import common.Action;
import common.Operation;
import context.operation.command.Command;
import context.operation.state.EditorState;
import context.operation.state.State;
import output.Consumer;

public class MoveCursorCommand extends Command {

    private final Action action;

    public MoveCursorCommand(State state, Consumer consumer, Action action) {
        super(state, consumer);
        this.action = action;
    }

    @Override
    public void execute() {
        var state = getState();
        switch (action) {
            case MOVE_CURSOR_UP -> state.moveCursorUp();
            case MOVE_CURSOR_DOWN -> state.moveCursorDown();
            case MOVE_CURSOR_LEFT -> state.moveCursorLeft();
            case MOVE_CURSOR_RIGHT -> state.moveCursorRight();
            default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
        }

        consumer.consume(getWriteModel(Operation.CURSOR));
    }

    @Override
    protected EditorState getState() {
        return (EditorState) super.getState();
    }
}
