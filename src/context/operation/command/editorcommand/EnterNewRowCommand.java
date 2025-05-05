package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.state.State;
import input.InputReader;
import output.Consumer;

public class EnterNewRowCommand extends UndoCommand {
    private int rowIndex;
    private int columnIndex;

    public EnterNewRowCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private EnterNewRowCommand(EnterNewRowCommand obj) {
        super(obj.state, obj.consumer);
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
    }

    @Override
    public void execute() {
        rowIndex = state.addRow();

        state.addChar(InputReader.getInputChar());
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.setCursorRowIndex(rowIndex + 1);
        state.setCursorColumnIndex(0);

        consumer.consume(getWriteModel(PrimitiveOperation.ADD_ROW));
        consumer.consume(getWriteModel(PrimitiveOperation.SET_CURSOR));
    }

    @Override
    public void unexecute() {
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        state.deleteChar(rowIndex, columnIndex);

        state.deleteRow(rowIndex + 1);

        consumer.consume(getWriteModel(PrimitiveOperation.DELETE_ROW));
        consumer.consume(getWriteModel(PrimitiveOperation.SET_CURSOR));
    }

    @Override
    public UndoCommand copy() {
        return new EnterNewRowCommand(this);
    }
}
