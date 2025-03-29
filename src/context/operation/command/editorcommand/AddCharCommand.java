package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.EditorState;
import input.InputReader;

public class AddCharCommand implements UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private final EditorState editorState;
    private final EditorProducer producer;

    public AddCharCommand(EditorState editorState, Consumer consumer) {
        this.producer = new EditorProducer(consumer);
        this.editorState = editorState;
    }

    @Override
    public void execute() {
        editorState.addChar(InputReader.getInputChar());
        rowIndex = editorState.getCursorRowIndex();
        columnIndex = editorState.getCursorColumnIndex();

        producer.notifyTextChanged(PrimitiveOperation.ADD_CHAR, editorState);
    }

    @Override
    public void unexecute() {
        editorState.deleteChar(rowIndex, columnIndex);
    }
}
