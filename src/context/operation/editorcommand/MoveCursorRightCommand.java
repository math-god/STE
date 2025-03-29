package context.operation.editorcommand;

import common.PrimitiveOperation;
import context.operation.notification.Producer;
import context.operation.notification.Consumer;
import context.dto.ContextCursorNotificationModel;
import context.operation.command.Command;

import java.util.Collection;
import java.util.HashSet;

public class MoveCursorRightCommand extends Producer implements Command {

    private final EditorState editorState;

    public MoveCursorRightCommand(EditorState editorState) {
        super();
        this.editorState = editorState;
    }

    @Override
    public void execute() {
        editorState.moveCursorRight();

        notifyCursorChanged();
    }

    private void notifyCursorChanged() {
        var info = new ContextCursorNotificationModel();

        info.setOperation(PrimitiveOperation.CURSOR_RIGHT);
        info.setCursorColumnIndex(editorState.getCursorColumnIndex());
        info.setCursorRowIndex(editorState.getCursorRowIndex());

        consumers.forEach(m -> m.setInfo(info));
    }
}
