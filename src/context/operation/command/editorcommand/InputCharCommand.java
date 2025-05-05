package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.state.State;
import input.InputReader;
import output.Consumer;

public class InputCharCommand extends UndoCommand {

    private int rowIndex;
    private int columnIndex;

    public InputCharCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private InputCharCommand(InputCharCommand obj) {
        super(obj.state, obj.consumer);
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
    }

    @Override
    public void execute() {
        state.addChar(InputReader.getInputChar());
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.moveCursorRight();

        consumer.consume(getWriteModel(PrimitiveOperation.ADD_CHAR));
        consumer.consume(getWriteModel(PrimitiveOperation.CURSOR_RIGHT));
    }

    @Override
    public void unexecute() {
        state.deleteChar(rowIndex, columnIndex);
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        consumer.consume(getWriteModel(PrimitiveOperation.DELETE_CHAR));
        consumer.consume(getWriteModel(PrimitiveOperation.SET_CURSOR));
    }

    @Override
    public UndoCommand copy() {
        return new InputCharCommand(this);
    }

    @Override
    public String toString() {
        return rowIndex + " " + columnIndex;
    }
}
