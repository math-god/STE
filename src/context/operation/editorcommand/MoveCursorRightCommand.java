package context.operation.editorcommand;

import common.PrimitiveOperation;
import context.Observable;
import context.Observer;
import context.dto.ContextCursorNotificationModel;
import context.operation.Command;

import java.util.Collection;
import java.util.HashSet;

public class MoveCursorRightCommand implements Command, Observable {

    private final EditorState editorState;
    private final Collection<Observer> observers;

    public MoveCursorRightCommand(EditorState editorState) {
        this.editorState = editorState;
        this.observers = new HashSet<>();
    }

    @Override
    public void execute() {
        editorState.moveCursorRight();

        notifyCursorChanged();
    }

    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyCursorChanged() {
        var info = new ContextCursorNotificationModel();

        info.setOperation(PrimitiveOperation.CURSOR_RIGHT);
        info.setCursorColumnIndex(editorState.getCursorColumnIndex());
        info.setCursorRowIndex(editorState.getCursorRowIndex());

        observers.forEach(m -> m.setInfo(info));
    }
}
