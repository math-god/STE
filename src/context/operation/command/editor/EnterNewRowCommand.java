package context.operation.command.editor;

import common.AsciiConstant;
import common.Operation;
import context.operation.command.UndoCommand;
import context.operation.state.EditorState;
import context.operation.state.State;
import output.Consumer;

public class EnterNewRowCommand extends UndoCommand {
    private int rowIndex;
    private int columnIndex;

    public EnterNewRowCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private EnterNewRowCommand(EnterNewRowCommand obj) {
        super(obj.getState(), obj.consumer);
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
    }

    @Override
    public void execute() {
        var state = getState();

        rowIndex = state.addRow();

        state.addChar(AsciiConstant.CARRIAGE_RETURN);
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.setCursorRowIndex(rowIndex + 1);
        state.setCursorColumnIndex(0);

        consumer.consume(getWriteModel(Operation.TEXT));
        consumer.consume(getWriteModel(Operation.CURSOR));

        undoComplete = false;
    }

    @Override
    public void unexecute() {
        var state = getState();

        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        state.deleteChar(rowIndex, columnIndex);

        state.deleteRow(rowIndex + 1);

        consumer.consume(getWriteModel(Operation.TEXT));
        consumer.consume(getWriteModel(Operation.CURSOR));

        undoComplete = true;
    }

    @Override
    protected EditorState getState() {
        return (EditorState) super.getState();
    }

    @Override
    public UndoCommand copy() {
        return new EnterNewRowCommand(this);
    }
}
