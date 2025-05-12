package context.operation.command;

import context.operation.command.abstraction.Command;
import context.operation.state.FileExplorerState;

public class OpenFileCommand implements Command {

    private final FileExplorerState state;

    public OpenFileCommand(FileExplorerState state) {
        this.state = state;
    }

    @Override
    public void execute() {
        state.updateList();
    }
}
