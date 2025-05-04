package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.State;
import input.InputReader;

public class InputPrintableCharCommand implements UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private final State state;
    private final EditorProducer producer;

    public InputPrintableCharCommand(State state, Consumer consumer) {
        this.producer = new EditorProducer(consumer);
        this.state = state;
    }

    private InputPrintableCharCommand(InputPrintableCharCommand obj) {
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
        state = obj.state;
        producer = obj.producer;
    }

    @Override
    public void execute() {
        state.addChar(InputReader.getInputChar());
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.moveCursorRight();

        producer.notifyTextChanged(PrimitiveOperation.ADD_CHAR, state);
        producer.notifyCursorChanged(PrimitiveOperation.CURSOR_RIGHT, state);
    }

    @Override
    public void unexecute() {
        state.deleteChar(rowIndex, columnIndex);
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        producer.notifyTextChanged(PrimitiveOperation.DELETE_CHAR, state);
        producer.notifyCursorChanged(PrimitiveOperation.SET_CURSOR, state);
    }

    @Override
    public UndoCommand copy() {
        return new InputPrintableCharCommand(this);
    }

    @Override
    public String toString() {
        return rowIndex + " " + columnIndex;
    }
}
