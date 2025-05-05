package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.state.State;
import output.Consumer;

public class BackspaceDeleteCommand extends UndoCommand {

    private int ch;

    public BackspaceDeleteCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private BackspaceDeleteCommand(BackspaceDeleteCommand obj) {
        super(obj.state, obj.consumer);
        ch = obj.ch;
    }

    @Override
    public void execute() {
        state.moveCursorLeft();

        ch = state.deleteCharAtCursorAndGetChar();
        if (ch == AsciiConstant.CARRIAGE_RETURN) {
            var firstRowIndex = state.getCursorRowIndex();
            var secondRowIndex = state.getCursorRowIndex() + 1;
            state.joinRows(firstRowIndex, secondRowIndex);
            state.deleteRow(secondRowIndex);
        }

        consumer.consume(getWriteModel(PrimitiveOperation.DELETE_CHAR));
        consumer.consume(getWriteModel(PrimitiveOperation.CURSOR_LEFT));
    }

    @Override
    public void unexecute() {
        state.addChar(ch);
        state.moveCursorRight();

        consumer.consume(getWriteModel(PrimitiveOperation.ADD_CHAR));
        consumer.consume(getWriteModel(PrimitiveOperation.CURSOR_RIGHT));
    }

    @Override
    public UndoCommand copy() {
        return new BackspaceDeleteCommand(this);
    }
}
