package context.operation.command;

import common.Action;
import common.CharCode;
import context.ContextType;
import context.operation.state.dialog.DialogState;
import context.operation.state.editor.EditorState;
import context.operation.state.fileexplorer.FileExplorerState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CommandExecutor {

    private final EditorState editorState;
    private final FileExplorerState fileExplorerState;
    private final DialogState dialogState;

    private final Map<Action, Command> commands;
    private final LinkedList<Undoable> commandLog = new LinkedList<>();

    private static ContextType context = ContextType.EDITOR;
    private Action action;
    private int undoStep = 0;

    public CommandExecutor(EditorState editorState,
                           FileExplorerState fileExplorerState,
                           DialogState dialogState,
                           Map<Action, Command> commands) {
        this.editorState = editorState;
        this.fileExplorerState = fileExplorerState;
        this.dialogState = dialogState;
        this.commands = commands;
    }

    public boolean execute(int ch) {
        boolean res;
        action = getActionByChar(ch);
        var command = commands.get(action);
        if (command == null)
            return true;

        if (command instanceof ArgumentCommand argCommand) {
            argCommand.setArg(ch);
            res = argCommand.execute();
        } else {
            res = command.execute();
        }

        if (command instanceof Undoable undoable) {
            commandLog.removeIf(Undoable::isUndoComplete);
            commandLog.addFirst(undoable.copy());
            undoStep = 0;
        }

        return res;
    }

    public class ArrowKeysCommand implements Command {

        @Override
        public boolean execute() {
            if (context == ContextType.EDITOR) {
                boolean changed;
                switch (action) {
                    case MOVE_CURSOR_UP -> changed = editorState.moveCursorUp();
                    case MOVE_CURSOR_DOWN -> changed = editorState.moveCursorDown();
                    case MOVE_CURSOR_LEFT -> changed = editorState.moveCursorLeft();
                    case MOVE_CURSOR_RIGHT -> changed = editorState.moveCursorRight();
                    default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
                }

                if (changed)
                    editorState.writeInTerminal();
            }

            if (context == ContextType.FILE_EXPLORER) {
                switch (action) {
                    case PREVIOUS_ITEM -> fileExplorerState.previousItem();
                    case NEXT_ITEM -> fileExplorerState.nextItem();
                    case MOVE_LEFT_FILE_NAME -> fileExplorerState.moveCursorLeft();
                    case MOVE_RIGHT_FILE_NAME -> fileExplorerState.moveCursorRight();
                    default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
                }
            }

            if (context == ContextType.DIALOG) {
                switch (action) {
                    case PREVIOUS_ITEM -> dialogState.previousItem();
                    case NEXT_ITEM -> dialogState.nextItem();
                    default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
                }

                dialogState.continueDialog();
            }

            return true;
        }
    }

    public class EditorDeleteCharCommand implements Command, Undoable {
        private int ch;
        private boolean undoComplete;

        public EditorDeleteCharCommand() {
        }

        private EditorDeleteCharCommand(EditorDeleteCharCommand obj) {
            this.ch = obj.ch;
            this.undoComplete = obj.undoComplete;
        }

        @Override
        public boolean execute() {
            if (action == Action.BACKSPACE_DELETE) {
                var changed = editorState.moveCursorLeft();
                if (!changed)
                    return true;
            }

            ch = editorState.deleteCharAtCursorAndGetChar();
            if (ch == CharCode.CARRIAGE_RETURN) {
                var firstRowIndex = editorState.getCursorRowIndex();
                var secondRowIndex = editorState.getCursorRowIndex() + 1;
                editorState.joinRows(firstRowIndex, secondRowIndex);
                editorState.deleteRow(secondRowIndex);
            }

            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = false;

            return true;
        }

        @Override
        public boolean isUndoComplete() {
            return undoComplete;
        }

        @Override
        public void undo() {
            editorState.addChar(ch);

            if (action == Action.BACKSPACE_DELETE) {
                editorState.moveCursorRight();
            }
            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = true;
        }

        @Override
        public Undoable copy() {
            return new EditorDeleteCharCommand(this);
        }
    }

    public class FileNameDeleteCharCommand implements Command {

        @Override
        public boolean execute() {
            if (action == Action.BACKSPACE_DELETE_FILE_NAME)
                fileExplorerState.moveCursorLeft();
            fileExplorerState.deleteFileNameCharOnCursor();

            return true;
        }
    }

    public class EditorNewRowCommand implements Command, Undoable {
        private int rowIndex;
        private int columnIndex;
        private boolean undoComplete;

        public EditorNewRowCommand() {
        }

        private EditorNewRowCommand(EditorNewRowCommand obj) {
            this.rowIndex = obj.rowIndex;
            this.columnIndex = obj.columnIndex;
            this.undoComplete = obj.undoComplete;
        }

        @Override
        public boolean execute() {
            rowIndex = editorState.addRow();

            editorState.addChar(CharCode.CARRIAGE_RETURN);
            rowIndex = editorState.getCursorRowIndex();
            columnIndex = editorState.getCursorColumnIndex();

            editorState.setCursorPosition(rowIndex + 1, 0);

            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = false;
            return true;
        }

        @Override
        public void undo() {
            editorState.setCursorPosition(rowIndex, columnIndex);

            editorState.deleteChar(rowIndex, columnIndex);
            editorState.deleteRow(rowIndex + 1);

            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = true;
        }

        @Override
        public boolean isUndoComplete() {
            return undoComplete;
        }

        @Override
        public Undoable copy() {
            return new EditorNewRowCommand(this);
        }
    }

    public class EditorPrintableInputCommand implements ArgumentCommand, Undoable {

        private int rowIndex;
        private int columnIndex;
        private int ch;
        private boolean undoComplete;
        private boolean isArgPresent;

        public EditorPrintableInputCommand() {
        }

        private EditorPrintableInputCommand(EditorPrintableInputCommand obj) {
            this.rowIndex = obj.rowIndex;
            this.columnIndex = obj.columnIndex;
            this.ch = obj.ch;
            this.undoComplete = obj.undoComplete;
            this.isArgPresent = obj.isArgPresent;
        }

        @Override
        public void setArg(int arg) {
            ch = arg;
            isArgPresent = true;
        }

        @Override
        public boolean execute() {
            if (!isArgPresent)
                throw new IllegalArgumentException("Arg is not present in an argument command");

            editorState.addChar(ch);

            rowIndex = editorState.getCursorRowIndex();
            columnIndex = editorState.getCursorColumnIndex();

            editorState.moveCursorRight();
            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = false;
            return true;
        }

        @Override
        public boolean isUndoComplete() {
            return undoComplete;
        }

        @Override
        public void undo() {
            editorState.deleteChar(rowIndex, columnIndex);
            editorState.setCursorPosition(rowIndex, columnIndex);

            editorState.writeInTerminal();

            fileExplorerState.setUnsaved();
            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());

            undoComplete = true;
        }

        @Override
        public Undoable copy() {
            return new EditorPrintableInputCommand(this);
        }

        @Override
        public String toString() {
            return ch + " " + rowIndex + " " + columnIndex;
        }
    }

    public class SaveFileExplorerInputFileNameCommand implements ArgumentCommand {

        private int ch;
        private boolean isArgPresent;

        @Override
        public boolean execute() {
            if (!isArgPresent)
                throw new IllegalArgumentException("Arg is not present in an argument command");

            fileExplorerState.inputFileName(ch);


            return true;
        }

        @Override
        public void setArg(int arg) {
            ch = arg;
            isArgPresent = true;
        }
    }


    public class ContextSwitchCommand implements Command {

        @Override
        public boolean execute() {
            switch (action) {
                case OPEN_FILE_EXPLORER -> {
                    if (!fileExplorerState.isSaved()) {
                        context = ContextType.DIALOG;
                        dialogState.startDialog(DialogState.DialogType.SAVE_BEFORE_OPEN);
                        break;
                    }

                    context = ContextType.FILE_EXPLORER;
                    fileExplorerState.startExplorer(action);
                }
                case OPEN_DIR_EXPLORER -> {
                    if (fileExplorerState.isSaved())
                        break;

                    if (fileExplorerState.getOpenedFilePath() != null) {
                        var editorContent = editorState.getStringRepresentation();
                        fileExplorerState.writeFile(editorContent);
                        editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());
                        break;
                    }

                    context = ContextType.FILE_EXPLORER;
                    fileExplorerState.startExplorer(action);
                }
                case OPEN_OR_SAVE_FILE -> {
                    var explorerType = fileExplorerState.getCurrentExplorerType();

                    if (explorerType == FileExplorerState.Type.OPEN) {
                        List<String> fileContent;
                        fileContent = fileExplorerState.readFileOrGoToDir();

                        if (fileContent != null) {
                            editorState.fillStorage(fileContent, fileExplorerState.getFileName(), fileExplorerState.isSaved());
                            editorState.writeInTerminal();
                            context = ContextType.EDITOR;
                        }
                    }

                    if (explorerType == FileExplorerState.Type.SAVE) {
                        var editorContent = editorState.getStringRepresentation();
                        var saved = fileExplorerState.writeFileOrGoToDir(editorContent);
                        if (saved) {
                            context = ContextType.EDITOR;
                            editorState.updateHeader(fileExplorerState.getFileName(), fileExplorerState.isSaved());
                            editorState.writeInTerminal();
                        }
                    }
                }
                case OPEN_HELP_PAGE -> {
                    context = ContextType.DIALOG;
                    dialogState.startDialog(DialogState.DialogType.HELP_PAGE);
                }
                case DIALOG_ACTIONS -> {
                    var dialogType = dialogState.getType();
                    switch (dialogType) {
                        case SAVE_BEFORE_OPEN -> {
                            var answer = dialogState.finishDialog();
                            if (answer == DialogState.DialogAnswer.YES) {
                                if (fileExplorerState.getOpenedFilePath() != null) {
                                    var editorContent = editorState.getStringRepresentation();
                                    fileExplorerState.writeFile(editorContent);

                                    context = ContextType.FILE_EXPLORER;
                                    fileExplorerState.startExplorer(Action.OPEN_FILE_EXPLORER);
                                    break;
                                }

                                context = ContextType.FILE_EXPLORER;
                                fileExplorerState.startExplorer(Action.OPEN_DIR_EXPLORER);
                            }

                            if (answer == DialogState.DialogAnswer.NO) {
                                context = ContextType.FILE_EXPLORER;
                                fileExplorerState.startExplorer(Action.OPEN_FILE_EXPLORER);
                            }
                        }
                        case HELP_PAGE -> {
                            dialogState.finishDialog();
                            context = ContextType.EDITOR;
                            editorState.writeInTerminal();
                        }
                    }
                }
            }

            return true;
        }
    }

    public class DoCommand implements Command {

        @Override
        public boolean execute() {
            var iterator = commandLog.listIterator(undoStep);
            if (iterator.hasPrevious()) {
                var doCommand = (Command) iterator.previous();
                doCommand.execute();

                undoStep--;
            }

            return true;
        }
    }

    public class UndoCommand implements Command {

        @Override
        public boolean execute() {
            var iterator = commandLog.listIterator(undoStep);
            if (iterator.hasNext()) {
                var undoCommand = iterator.next();
                undoCommand.undo();

                undoStep++;
            }

            return true;
        }
    }

    public class QuitCommand implements Command {

        @Override
        public boolean execute() {
            return false;
        }
    }

    private Action getActionByChar(int ch) throws IllegalArgumentException {
        if (ch == CharCode.DEVICE_CONTROL_1)
            return Action.QUIT;

        switch (context) {
            case EDITOR -> {
                if (ch >= CharCode.FIRST_PRINTABLE_CHAR && ch <= CharCode.LAST_PRINTABLE_CHAR)
                    return Action.INPUT_PRINTABLE_CHAR;
                if (ch == CharCode.DEL_WITH_LEFT_OFFSET)
                    return Action.BACKSPACE_DELETE;
                if (ch == CharCode.DEL)
                    return Action.DEL_DELETE;
                if (ch == CharCode.CARRIAGE_RETURN)
                    return Action.NEW_ROW;
                if (ch == CharCode.CANCEL)
                    return Action.DO;
                if (ch == CharCode.SHIFT_IN)
                    return Action.OPEN_FILE_EXPLORER;
                if (ch == CharCode.DEVICE_CONTROL_3)
                    return Action.OPEN_DIR_EXPLORER;
                if (ch == CharCode.RIGHT_ARROW)
                    return Action.MOVE_CURSOR_RIGHT;
                if (ch == CharCode.LEFT_ARROW)
                    return Action.MOVE_CURSOR_LEFT;
                if (ch == CharCode.UP_ARROW)
                    return Action.MOVE_CURSOR_UP;
                if (ch == CharCode.DOWN_ARROW)
                    return Action.MOVE_CURSOR_DOWN;
                if (ch == CharCode.CTRL_Z)
                    return Action.UNDO;
                if (ch == CharCode.BACKSPACE)
                    return Action.OPEN_HELP_PAGE;
            }
            case FILE_EXPLORER -> {
                if (ch >= CharCode.FIRST_PRINTABLE_CHAR && ch <= CharCode.LAST_PRINTABLE_CHAR)
                    return Action.INPUT_FILE_NAME;
                if (ch == CharCode.DEL_WITH_LEFT_OFFSET)
                    return Action.BACKSPACE_DELETE_FILE_NAME;
                if (ch == CharCode.DEL)
                    return Action.DEL_DELETE_FILE_NAME;
                if (ch == CharCode.RIGHT_ARROW)
                    return Action.MOVE_RIGHT_FILE_NAME;
                if (ch == CharCode.LEFT_ARROW)
                    return Action.MOVE_LEFT_FILE_NAME;
                if (ch == CharCode.CARRIAGE_RETURN)
                    return Action.OPEN_OR_SAVE_FILE;
                if (ch == CharCode.UP_ARROW)
                    return Action.PREVIOUS_ITEM;
                if (ch == CharCode.DOWN_ARROW)
                    return Action.NEXT_ITEM;
            }
            case DIALOG -> {
                if (ch == CharCode.CARRIAGE_RETURN)
                    return Action.DIALOG_ACTIONS;
                if (ch == CharCode.UP_ARROW)
                    return Action.PREVIOUS_ITEM;
                if (ch == CharCode.DOWN_ARROW)
                    return Action.NEXT_ITEM;
            }
        }

        return Action.NONE;
    }

    public interface Command {
        boolean execute();
    }

    public interface ArgumentCommand extends Command {
        void setArg(int arg);
    }

    public interface Undoable {
        boolean isUndoComplete();

        void undo();

        Undoable copy();
    }
}
