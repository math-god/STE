package context.operation.command;

import context.ContextType;
import context.operation.command.abstraction.Command;
import context.operation.state.EditorState;
import context.operation.state.FileExplorerState;
import input.InputReader;

public class ArrowKeysCommand implements Command {

    private final EditorState editorState;
    private final FileExplorerState fileExplorerState;

    public ArrowKeysCommand(EditorState editorState, FileExplorerState fileExplorerState) {
        this.editorState = editorState;
        this.fileExplorerState = fileExplorerState;
    }

    @Override
    public void execute() {
        var action = InputReader.getAction();
        var context = InputReader.getCurrentContext();

        if (context == ContextType.EDITOR) {
            switch (action) {
                case MOVE_CURSOR_UP -> editorState.moveCursorUp();
                case MOVE_CURSOR_DOWN -> editorState.moveCursorDown();
                case MOVE_CURSOR_LEFT -> editorState.moveCursorLeft();
                case MOVE_CURSOR_RIGHT -> editorState.moveCursorRight();
                default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
            }
        }

        if (context == ContextType.FILE_EXPLORER) {
            switch (action) {
                case MOVE_CURSOR_UP -> fileExplorerState.previousItem();
                case MOVE_CURSOR_DOWN -> fileExplorerState.nextItem();
                default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
            }

            fileExplorerState.updateExplorer(action);
        }
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
