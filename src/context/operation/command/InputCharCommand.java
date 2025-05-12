package context.operation.command;

import context.operation.command.abstraction.UndoCommand;
import context.operation.state.EditorState;
import input.InputReader;

public class InputCharCommand implements UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private int ch;
    private final EditorState state;
    private boolean undoComplete;

    public InputCharCommand(EditorState state) {
        this.state = state;
    }

    private InputCharCommand(InputCharCommand obj) {
        this.state = obj.state;
        this.rowIndex = obj.rowIndex;
        this.columnIndex = obj.columnIndex;
        this.ch = obj.ch;
        this.undoComplete = obj.undoComplete;
    }

    @Override
    public void execute() {
        if (ch == 0) {
            ch = InputReader.getInputChar();
        }
        state.addChar(ch);

        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.moveCursorRight();

        undoComplete = false;
    }

    @Override
    public boolean isUndoComplete() {
        return false;
    }

    @Override
    public void unexecute() {
        state.deleteChar(rowIndex, columnIndex);
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        undoComplete = true;
    }

    @Override
    public UndoCommand copy() {
        var copy = new InputCharCommand(this);
        ch = 0;
        return copy;
    }

    @Override
    public String toString() {
        return ch + " " + rowIndex + " " + columnIndex;
    }
}
