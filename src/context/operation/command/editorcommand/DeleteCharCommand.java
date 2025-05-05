package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
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
        super(obj.state, obj.consumer);
        ch = obj.ch;
        this.type = obj.type;
    }

    @Override
    public void execute() {
        if (type == Type.BACKSPACE) {
            state.moveCursorLeft();
            consumer.consume(getWriteModel(PrimitiveOperation.CURSOR_LEFT));
        }

        ch = state.deleteCharAtCursorAndGetChar();
        if (ch == AsciiConstant.CARRIAGE_RETURN) {
            var firstRowIndex = state.getCursorRowIndex();
            var secondRowIndex = state.getCursorRowIndex() + 1;
            state.joinRows(firstRowIndex, secondRowIndex);
            state.deleteRow(secondRowIndex);
        }

        consumer.consume(getWriteModel(PrimitiveOperation.DELETE_CHAR));
    }

    @Override
    public void unexecute() {
        state.addChar(ch);
        consumer.consume(getWriteModel(PrimitiveOperation.ADD_CHAR));

        if (type == Type.BACKSPACE) {
            state.moveCursorRight();
            consumer.consume(getWriteModel(PrimitiveOperation.CURSOR_RIGHT));
        }
    }

    @Override
    public UndoCommand copy() {
        return new DeleteCharCommand(this);
    }

    public enum Type {
        DEL,
        BACKSPACE
    }
}
