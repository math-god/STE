package context.operation.command.fileexplorer;

import common.Action;
import common.Operation;
import context.operation.command.Command;
import context.operation.state.FileExplorerState;
import context.operation.state.State;
import output.Consumer;

public class ItemSelectionCommand extends Command {

    private final Action action;

    public ItemSelectionCommand(State state, Consumer consumer, Action action) {
        super(state, consumer);
        this.action = action;
    }

    @Override
    public void execute() {
        var state = getState();

        switch (action) {
            case MOVE_CURSOR_UP -> state.previousItem();
            case MOVE_CURSOR_DOWN -> state.nextItem();
            default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
        }

        state.updateList();

        consumer.consume(getWriteModel(Operation.TEXT));
    }

    @Override
    protected FileExplorerState getState() {
        return (FileExplorerState) super.getState();
    }
}
