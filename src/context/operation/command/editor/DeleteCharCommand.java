package context.operation.command.editor;

import common.AsciiConstant;
import common.Operation;
import context.operation.command.UndoCommand;
import context.operation.state.EditorState;
import context.operation.state.State;
import output.Consumer;

public class DeleteCharCommand extends UndoCommand {

    private int ch;
    public Type type;

    public DeleteCharCommand(State state, Consumer consumer, Type type) {
        super(state, consumer);
        this.type = type;
    }

    private DeleteCharCommand(DeleteCharCommand obj) {
        super(obj.getState(), obj.consumer);
        ch = obj.ch;
        this.type = obj.type;
    }

    @Override
    public void execute() {
        var state = getState();

        if (type == Type.BACKSPACE) {
            state.moveCursorLeft();
            consumer.consume(getWriteModel(Operation.CURSOR));
        }

        ch = state.deleteCharAtCursorAndGetChar();
        if (ch == AsciiConstant.CARRIAGE_RETURN) {
            var firstRowIndex = state.getCursorRowIndex();
            var secondRowIndex = state.getCursorRowIndex() + 1;
            state.joinRows(firstRowIndex, secondRowIndex);
            state.deleteRow(secondRowIndex);
        }

        consumer.consume(getWriteModel(Operation.TEXT));
        undoComplete = false;
    }

    @Override
    public void unexecute() {
        var state = getState();

        state.addChar(ch);
        consumer.consume(getWriteModel(Operation.TEXT));

        if (type == Type.BACKSPACE) {
            state.moveCursorRight();
            consumer.consume(getWriteModel(Operation.CURSOR));
        }

        undoComplete = true;
    }

    @Override
    public UndoCommand copy() {
        return new DeleteCharCommand(this);
    }

    @Override
    protected EditorState getState() {
        return (EditorState) super.getState();
    }

    public enum Type {
        DEL,
        BACKSPACE
    }
}
