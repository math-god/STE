package context.operation.command.editor;

import common.Operation;
import context.operation.command.UndoCommand;
import context.operation.state.EditorState;
import context.operation.state.State;
import input.InputReader;
import output.Consumer;

public class InputCharCommand extends UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private int ch;

    public InputCharCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private InputCharCommand(InputCharCommand obj) {
        super(obj.getState(), obj.consumer);
        rowIndex = obj.rowIndex;
        columnIndex = obj.columnIndex;
        ch = obj.ch;
    }

    @Override
    public void execute() {
        var state = getState();

        if (ch == 0) {
            ch = InputReader.getInputChar();
        }
        state.addChar(ch);

        rowIndex = state.getCursorRowIndex();
        columnIndex = state.getCursorColumnIndex();

        state.moveCursorRight();

        consumer.consume(getWriteModel(Operation.TEXT));
        consumer.consume(getWriteModel(Operation.CURSOR));

        undoComplete = false;
    }

    @Override
    public void unexecute() {
        var state = getState();

        state.deleteChar(rowIndex, columnIndex);
        state.setCursorRowIndex(rowIndex);
        state.setCursorColumnIndex(columnIndex);

        consumer.consume(getWriteModel(Operation.TEXT));
        consumer.consume(getWriteModel(Operation.CURSOR));

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

    @Override
    protected EditorState getState() {
        return (EditorState) super.getState();
    }
}
