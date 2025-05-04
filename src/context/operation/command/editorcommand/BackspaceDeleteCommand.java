package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.State;

public class BackspaceDeleteCommand implements UndoCommand {

    private int ch;
    private final State state;
    private final EditorProducer producer;

    public BackspaceDeleteCommand(State state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    private BackspaceDeleteCommand(BackspaceDeleteCommand obj) {
        ch = obj.ch;
        state = obj.state;
        producer = obj.producer;
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

        producer.notifyTextChanged(PrimitiveOperation.DELETE_CHAR, state);
        producer.notifyCursorChanged(PrimitiveOperation.CURSOR_LEFT, state);
    }

    @Override
    public void unexecute() {
        state.addChar(ch);

        producer.notifyTextChanged(PrimitiveOperation.ADD_CHAR, state);
    }

    @Override
    public UndoCommand copy() {
        return new BackspaceDeleteCommand(this);
    }
}
