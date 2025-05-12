package context.operation.command;

import common.CharCode;
import context.operation.command.abstraction.UndoCommand;
import context.operation.state.EditorState;

public class EnterNewRowCommand implements UndoCommand {
    private int rowIndex;
    private int columnIndex;
    private final EditorState state;
    private boolean undoComplete;

    public EnterNewRowCommand(EditorState state) {
        this.state = state;
    }

    private EnterNewRowCommand(EnterNewRowCommand obj) {
        this.rowIndex = obj.rowIndex;
        this.columnIndex = obj.columnIndex;
        this.state = obj.state;
        this.undoComplete = obj.undoComplete;
    }

    @Override
    public void execute() {
        rowIndex = state.addRow();

        state.addChar(CharCode.CARRIAGE_RETURN);
        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.setCursorRowIndex(rowIndex + 1);
        state.setCursorColumnIndex(0);

        undoComplete = false;
    }

    @Override
    public void unexecute() {
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        state.deleteChar(rowIndex, columnIndex);

        state.deleteRow(rowIndex + 1);

        undoComplete = true;
    }

    @Override
    public UndoCommand copy() {
        return new EnterNewRowCommand(this);
    }

    @Override
    public boolean isUndoComplete() {
        return undoComplete;
    }
}
