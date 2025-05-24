package context.operation.command;

import common.Action;
import common.CharCode;
import context.ContextType;
import context.operation.state.EditorState;
import context.operation.state.FileExplorerState;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandExecutor {

    private final EditorState editorState;
    private final FileExplorerState fileExplorerState;
    private final Map<Action, Command> commands;
    private final LinkedList<Undoable> commandLog = new LinkedList<>();

    private static ContextType context = ContextType.EDITOR;
    private Action action;
    private int undoStep = 0;

    public CommandExecutor(EditorState editorState,
                           FileExplorerState fileExplorerState,
                           Map<Action, Command> commands) {
        this.editorState = editorState;
        this.fileExplorerState = fileExplorerState;
        this.commands = commands;
    }

    public static ContextType getContext() {
        return context;
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
                    case PREVIOUS_ITEM -> fileExplorerState.previousItem();
                    case NEXT_ITEM -> fileExplorerState.nextItem();
                    default -> throw new IllegalArgumentException("Unknown cursor action: " + action);
                }

                fileExplorerState.updateExplorer(action);
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
                editorState.moveCursorLeft();
            }

            ch = editorState.deleteCharAtCursorAndGetChar();
            if (ch == CharCode.CARRIAGE_RETURN) {
                var firstRowIndex = editorState.getCursorRowIndex();
                var secondRowIndex = editorState.getCursorRowIndex() + 1;
                editorState.joinRows(firstRowIndex, secondRowIndex);
                editorState.deleteRow(secondRowIndex);
            }

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

            undoComplete = true;
        }

        @Override
        public Undoable copy() {
            return new EditorDeleteCharCommand(this);
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

            editorState.setCursorRowIndex(rowIndex + 1);
            editorState.setCursorColumnIndex(0);

            undoComplete = false;
            return true;
        }

        @Override
        public void undo() {
            editorState.setCursorRowIndex(rowIndex);
            editorState.setCursorColumnIndex(columnIndex);

            editorState.deleteChar(rowIndex, columnIndex);
            editorState.deleteRow(rowIndex + 1);

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
                throw new IllegalArgumentException("Arg is not present in an unary command");

            editorState.addChar(ch);

            rowIndex = editorState.getCursorRowIndex();
            columnIndex = editorState.getCursorColumnIndex();

            editorState.moveCursorRight();

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
            editorState.setCursorRowIndex(rowIndex);
            editorState.setCursorColumnIndex(columnIndex);

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


    public class ContextSwitchCommand implements Command {

        @Override
        public boolean execute() {
            switch (action) {
                case OPEN_FILE_EXPLORER, OPEN_DIR_EXPLORER -> {
                    context = ContextType.FILE_EXPLORER;
                    fileExplorerState.updateExplorer(action);
                }
                case OPEN_OR_SAVE_FILE -> {
                    var explorerType = fileExplorerState.getCurrentExplorerType();

                    if (explorerType == FileExplorerState.Type.OPEN) {
                        List<String> fileContent;
                        fileContent = fileExplorerState.readFileOrGoToDir();

                        if (fileContent != null) {
                            editorState.fillStorage(fileContent);
                            context = ContextType.EDITOR;
                        }
                    }

                    if (explorerType == FileExplorerState.Type.SAVE) {
                        var editorContent = editorState.getStringRepresentation();
                        var saved = fileExplorerState.writeFileOrGoToDir(editorContent);
                        if (saved) {
                            context = ContextType.EDITOR;
                            editorState.sendDataToTerminal();
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
                if (ch == CharCode.BACKSPACE)
                    return Action.BACKSPACE_DELETE;
                if (ch == CharCode.CARRIAGE_RETURN)
                    return Action.NEW_ROW;
                if (ch == CharCode.CANCEL)
                    return Action.DO;
                if (ch == CharCode.SHIFT_IN)
                    return Action.OPEN_FILE_EXPLORER;
                if (ch == CharCode.DEVICE_CONTROL_3)
                    return Action.OPEN_DIR_EXPLORER;
                if (ch == CharCode.DEL)
                    return Action.DEL_DELETE;
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
            }
            case FILE_EXPLORER -> {
                if (ch == CharCode.CARRIAGE_RETURN)
                    return Action.OPEN_OR_SAVE_FILE;
                if (ch == CharCode.UP_ARROW)
                    return Action.PREVIOUS_ITEM;
                if (ch == CharCode.DOWN_ARROW)
                    return Action.NEXT_ITEM;
            }
            default -> {

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
