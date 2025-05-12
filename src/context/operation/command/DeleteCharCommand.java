package context.operation.command;

import common.Action;
import common.CharCode;
import context.operation.command.abstraction.UndoCommand;
import context.operation.state.EditorState;
import input.InputReader;

public class DeleteCharCommand implements UndoCommand {

    private int ch;
    public Action action;
    private final EditorState state;
    private boolean undoComplete;

    public DeleteCharCommand(EditorState state) {
        this.state = state;
    }

    private DeleteCharCommand(DeleteCharCommand obj) {
        this.state = obj.state;
        this.ch = obj.ch;
        this.action = obj.action;
        this.undoComplete = obj.undoComplete;
    }

    @Override
    public void execute() {
        action = InputReader.getAction();

        if (action == Action.BACKSPACE_DELETE) {
            state.moveCursorLeft();
        }

        ch = state.deleteCharAtCursorAndGetChar();
        if (ch == CharCode.CARRIAGE_RETURN) {
            var firstRowIndex = state.getCursorRowIndex();
            var secondRowIndex = state.getCursorRowIndex() + 1;
            state.joinRows(firstRowIndex, secondRowIndex);
            state.deleteRow(secondRowIndex);
        }

        undoComplete = false;
    }

    @Override
    public void unexecute() {
        state.addChar(ch);

        if (action == Action.BACKSPACE_DELETE) {
            state.moveCursorRight();
        }

        undoComplete = true;
    }

    @Override
    public UndoCommand copy() {
        return new DeleteCharCommand(this);
    }

    @Override
    public boolean isUndoComplete() {
        return undoComplete;
    }

}
