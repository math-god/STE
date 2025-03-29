package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.EditorState;

public class AddRowCommand implements UndoCommand {

    private int rowIndex;
    private final EditorState state;
    private final EditorProducer producer;

    public AddRowCommand(EditorState state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    @Override
    public void execute() {
        rowIndex = state.addRow();

        producer.notifyTextChanged(PrimitiveOperation.ADD_ROW, state);
    }

    @Override
    public void unexecute() {
        state.deleteRow(rowIndex);
    }
}
