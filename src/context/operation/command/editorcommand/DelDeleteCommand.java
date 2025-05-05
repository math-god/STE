package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.state.State;
import output.Consumer;

public class DelDeleteCommand extends UndoCommand {

    private int ch;

    public DelDeleteCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    private DelDeleteCommand(DelDeleteCommand obj) {
        super(obj.state, obj.consumer);
        ch = obj.ch;
    }

    @Override
    public void execute() {
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

    }

    @Override
    public UndoCommand copy() {
        return new DelDeleteCommand(this);
    }
}
