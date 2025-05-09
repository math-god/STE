package context.operation.command;

import common.Operation;
import context.dto.CursorTerminalWriteModel;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import context.operation.state.State;
import output.Consumer;

public abstract class Command {

    private final State state;
    protected final Consumer consumer;

    public Command(State state, Consumer consumer) {
        this.state = state;
        this.consumer = consumer;
    }

    public abstract void execute();

    public TerminalWriteModel getWriteModel(Operation operation) {
        switch (operation) {
            case TEXT -> {
                var textNotification = new TextTerminalWriteModel();
                var text = state.getData().getText();
                textNotification.setOperation(operation);
                textNotification.setText(text);

                return textNotification;
            }
            case CURSOR -> {
                var cursorNotification = new CursorTerminalWriteModel();
                cursorNotification.setOperation(operation);
                cursorNotification.setCursorColumnIndex(state.getData().getColumnIndex());
                cursorNotification.setCursorRowIndex(state.getData().getRowIndex());

                return cursorNotification;
            }
            default -> {
                return TerminalWriteModel.none();
            }
        }
    }

    protected State getState() {
        return state;
    }
}
