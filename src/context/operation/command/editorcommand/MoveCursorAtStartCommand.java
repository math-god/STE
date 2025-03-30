package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.EditorState;

public class MoveCursorAtStartCommand implements UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private final EditorState state;
    private final EditorProducer producer;

    public MoveCursorAtStartCommand(EditorState state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    @Override
    public boolean execute() {
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        var rowResult = state.setCursorRowIndex(rowIndex + 1);
        var columnResult = state.setCursorColumnIndex(0);

        producer.notifyCursorChanged(PrimitiveOperation.SET_CURSOR, state);

        return rowResult && columnResult;
    }

    @Override
    public void unexecute() {
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        producer.notifyCursorChanged(PrimitiveOperation.SET_CURSOR, state);
    }
}
