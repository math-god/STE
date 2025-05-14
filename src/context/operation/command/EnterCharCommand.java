package context.operation.command;

import common.CharCode;
import context.operation.command.abstraction.Command;
import context.operation.state.EditorState;
import context.operation.state.FileExplorerState;
import input.InputReader;

public class EnterCharCommand implements Command {
    private int rowIndex;
    private int columnIndex;
    private final EditorState editorState;
    private final FileExplorerState fileExplorerState;
    private boolean undoComplete;
    private boolean canUndo = true;

    public EnterCharCommand(EditorState editorState, FileExplorerState fileExplorerState) {
        this.editorState = editorState;
        this.fileExplorerState = fileExplorerState;
    }

    private EnterCharCommand(EnterCharCommand obj) {
        this.rowIndex = obj.rowIndex;
        this.columnIndex = obj.columnIndex;
        this.editorState = obj.editorState;
        this.fileExplorerState = obj.fileExplorerState;
        this.undoComplete = obj.undoComplete;
        this.canUndo = obj.canUndo;
    }

    @Override
    public void execute() {
        var context = InputReader.getCurrentContext();

        switch (context) {
            case EDITOR -> {
                rowIndex = editorState.addRow();

                editorState.addChar(CharCode.CARRIAGE_RETURN);
                rowIndex = editorState.getCursorRowIndex();
                columnIndex = editorState.getCursorColumnIndex();

                editorState.setCursorRowIndex(rowIndex + 1);
                editorState.setCursorColumnIndex(0);

                undoComplete = false;
            }
            case FILE_EXPLORER -> {
                var fileContent = fileExplorerState.openFile();
                editorState.fillStorage(fileContent);
                canUndo = false;
            }
        }
    }

    @Override
    public void unexecute() {
        if (!canUndo)
            throw new RuntimeException("Command can't be unexecuted");

        var context = InputReader.getCurrentContext();

        switch (context) {
            case EDITOR -> {
                editorState.setCursorRowIndex(rowIndex);
                editorState.setCursorColumnIndex(columnIndex);

                editorState.deleteChar(rowIndex, columnIndex);
                editorState.deleteRow(rowIndex + 1);

                undoComplete = true;
            }
        }
    }

    @Override
    public Command copy() {
        if (!canUndo)
            throw new RuntimeException("Command can't be unexecuted");
        return new EnterCharCommand(this);
    }

    @Override
    public boolean isUndoComplete() {
        if (!canUndo)
            throw new RuntimeException("Command can't be unexecuted");
        return undoComplete;
    }

    @Override
    public boolean canUndo() {
        return canUndo;
    }
}
