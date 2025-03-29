package context.operation.command.editorcommand;

import common.Action;
import common.PrimitiveOperation;
import context.operation.command.Command;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.EditorState;

public class MoveCursorCommand implements Command {
    private final EditorState state;
    private final EditorProducer producer;
    private final Action action;

    public MoveCursorCommand(EditorState state, Consumer consumer, Action action) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
        this.action = action;
    }

    @Override
    public void execute() {
        PrimitiveOperation operation;

        switch (action) {
            case MOVE_CURSOR_UP -> {
                state.moveCursorUp();
                operation = PrimitiveOperation.CURSOR_UP;
            }
            case MOVE_CURSOR_DOWN -> {
                state.moveCursorDown();
                operation = PrimitiveOperation.CURSOR_DOWN;
            }
            case MOVE_CURSOR_LEFT -> {
                state.moveCursorLeft();
                operation = PrimitiveOperation.CURSOR_LEFT;
            }
            case MOVE_CURSOR_RIGHT -> {
                state.moveCursorRight();
                operation = PrimitiveOperation.CURSOR_RIGHT;
            }
            default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
        }

        producer.notifyCursorChanged(operation, state);
    }
}
