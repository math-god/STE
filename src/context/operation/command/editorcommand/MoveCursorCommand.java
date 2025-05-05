package context.operation.command.editorcommand;

import common.Action;
import common.PrimitiveOperation;
import context.operation.command.Command;
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
        PrimitiveOperation operation;
        switch (action) {
            case MOVE_CURSOR_UP -> {
                operation = PrimitiveOperation.CURSOR_UP;
                state.moveCursorUp();
            }
            case MOVE_CURSOR_DOWN -> {
                operation = PrimitiveOperation.CURSOR_DOWN;
                state.moveCursorDown();
            }
            case MOVE_CURSOR_LEFT -> {
                operation = PrimitiveOperation.CURSOR_LEFT;
                state.moveCursorLeft();
            }
            case MOVE_CURSOR_RIGHT -> {
                operation = PrimitiveOperation.CURSOR_RIGHT;
                state.moveCursorRight();
            }
            default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
        }

        consumer.consume(getWriteModel(operation));
    }
}
