package context.operation.command.editorcommand;

import common.PrimitiveOperation;
import context.operation.command.UndoCommand;
import context.operation.notification.Consumer;
import context.operation.notification.EditorProducer;
import context.operation.state.State;

public class AddRowCommand implements UndoCommand {

    private int rowIndex;
    private final State state;
    private final EditorProducer producer;

    public AddRowCommand(State state, Consumer consumer) {
        this.state = state;
        this.producer = new EditorProducer(consumer);
    }

    @Override
    public boolean execute() {
        rowIndex = state.addRow();

        producer.notifyTextChanged(PrimitiveOperation.ADD_ROW, state);

        return true;
    }

    @Override
    public void unexecute() {
        state.deleteRow(rowIndex);
    }
}
