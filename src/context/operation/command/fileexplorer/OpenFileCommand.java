package context.operation.command.fileexplorer;

import common.Operation;
import context.operation.command.Command;
import context.operation.state.FileExplorerState;
import context.operation.state.State;
import output.Consumer;

public class OpenFileCommand extends Command {

    public OpenFileCommand(State state, Consumer consumer) {
        super(state, consumer);
    }

    @Override
    public void execute() {
        var state = getState();

        state.initFileList();
        consumer.consume(getWriteModel(Operation.TEXT));
    }

    @Override
    protected FileExplorerState getState() {
        return (FileExplorerState) super.getState();
    }
}
