package context.operation.editorcommand;

import context.operation.notification.Producer;
import context.operation.notification.Consumer;
import context.operation.command.UndoCommand;

import java.util.Collection;
import java.util.HashSet;

public class NewRowCommand extends Producer implements UndoCommand {

    private final EditorState state;

    public NewRowCommand(EditorState state) {
        super();
        this.state = state;
    }

    @Override
    public void execute() {

    }

    @Override
    public void unexecute() {

    }
}
