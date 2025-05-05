package context.operation.command;

import common.PrimitiveOperation;
import context.dto.CursorTerminalWriteModel;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import context.operation.state.State;
import output.Consumer;

public abstract class Command {

    protected final State state;
    protected final Consumer consumer;

    public Command(State state, Consumer consumer) {
        this.state = state;
        this.consumer = consumer;
    }

    public abstract void execute();

    public TerminalWriteModel getWriteModel(PrimitiveOperation operation) {
        switch (operation.getGroup()) {
            case TEXT -> {
                var textNotification = new TextTerminalWriteModel();
                var text = state.getStringRepresentation();
                textNotification.setOperation(PrimitiveOperation.ADD_CHAR);
                textNotification.setText(text);

                return textNotification;
            }
            case CURSOR -> {
                var cursorNotification = new CursorTerminalWriteModel();
                cursorNotification.setOperation(PrimitiveOperation.CURSOR_RIGHT);
                cursorNotification.setCursorColumnIndex(state.getCursorColumnIndex());
                cursorNotification.setCursorRowIndex(state.getCursorRowIndex());

                return cursorNotification;
            }
            default -> {
                return TerminalWriteModel.none();
            }
        }
    }
}
