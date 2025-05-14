package context.operation.command;

import context.operation.command.abstraction.Command;
import context.operation.state.FileExplorerState;

public class OpenFileExplorerCommand implements Command {

    private final FileExplorerState state;

    public OpenFileExplorerCommand(FileExplorerState state) {
        this.state = state;
    }

    @Override
    public void execute() {
        state.updateList();
    }

    @Override
    public boolean canUndo() {
        return false;
    }

    @Override
    public boolean isUndoComplete() {
        throw new RuntimeException("Command can't be unexecuted");
    }

    @Override
    public void unexecute() {
        throw new RuntimeException("Command can't be unexecuted");
    }

    @Override
    public Command copy() {
        throw new RuntimeException("Command can't be unexecuted");
    }
}
