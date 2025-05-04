package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.State;
import input.InputReader;

public class EnterNewRowCommand implements UndoCommand {
    private int rowIndex;
    private int columnIndex;
    private final State state;
    private final EditorProducer producer;

    public EnterNewRowCommand(State state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    private EnterNewRowCommand(EnterNewRowCommand obj) {
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
        state = obj.state;
        producer = obj.producer;
    }

    @Override
    public void execute() {
        rowIndex = state.addRow();

        state.addChar(InputReader.getInputChar());
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.setCursorRowIndex(rowIndex + 1);
        state.setCursorColumnIndex(0);

        producer.notifyTextChanged(PrimitiveOperation.ADD_ROW, state);
        producer.notifyCursorChanged(PrimitiveOperation.SET_CURSOR, state);
    }

    @Override
    public void unexecute() {
        state.deleteRow(rowIndex);
    }

    @Override
    public UndoCommand copy() {
        return new EnterNewRowCommand(this);
    }
}
