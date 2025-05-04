package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.State;

public class DelDeleteCommand implements UndoCommand {

    private int ch;
    private final State state;
    private final EditorProducer producer;

    public DelDeleteCommand(State state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    private DelDeleteCommand(DelDeleteCommand obj) {
        ch = obj.ch;
        state = obj.state;
        producer = obj.producer;
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

        producer.notifyTextChanged(PrimitiveOperation.DELETE_CHAR, state);
    }

    @Override
    public void unexecute() {

    }

    @Override
    public UndoCommand copy() {
        return new DelDeleteCommand(this);
    }
}
