package context.operation.command.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.EditorState;

public class DeleteCharCommand implements UndoCommand {

    private int ch;
    private final EditorState state;
    private final EditorProducer producer;

    public DeleteCharCommand(EditorState state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    @Override
    public boolean execute() {
        ch = state.deleteCharAtCursor();

        producer.notifyTextChanged(PrimitiveOperation.DELETE_CHAR, state);

        return ch != AsciiConstant.NULL;
    }

    @Override
    public void unexecute() {
        state.addChar(ch);

        producer.notifyTextChanged(PrimitiveOperation.ADD_CHAR, state);
    }
}
