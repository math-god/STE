import common.Action;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.operation.command.CommandExecutor;
import context.operation.state.dialog.DialogState;
import context.operation.state.editor.EditorState;
import context.operation.state.fileexplorer.FileExplorerState;
import input.InputReader;
import output.TerminalWriter;

import java.io.IOException;
import java.util.HashMap;

import static common.escape.Escape.ERASE_SCREEN;
import static common.escape.Escape.SET_CURSOR_AT_START;

public class Application {
    private static InputReader inputReader;
    private static TerminalWriter terminalWriter;
    private static Terminal terminal;

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        init();

        try {
            while (true) {
                var res = inputReader.read();
                if (!res) break;
                terminalWriter.write();
            }
        } catch (IllegalArgumentException | IOException ex) {
            System.out.print(ERASE_SCREEN);
            System.out.print(SET_CURSOR_AT_START);
            System.out.print(ex.getMessage());
        }

        exit();
    }

    private static void init() {
        terminal = new WindowsTerminal();
        terminal.enableRawMode();

        terminalWriter = new TerminalWriter();

        var commands = initExecutor();
        inputReader = new InputReader(commands);

        System.out.print(ERASE_SCREEN);
        System.out.print(SET_CURSOR_AT_START);
    }

    private static CommandExecutor initExecutor() {
        var commands = new HashMap<Action, CommandExecutor.Command>();
        var editorState = new EditorState(terminalWriter);
        var fileExplorerState = new FileExplorerState(terminalWriter);
        var dialogState = new DialogState(terminalWriter);
        var executor = new CommandExecutor(editorState, fileExplorerState, dialogState, commands);

        var editorPrintableInputCommand = executor.new EditorPrintableInputCommand();
        var editorDeleteCharCommand = executor.new EditorDeleteCharCommand();
        var arrowKeysCommand = executor.new ArrowKeysCommand();
        var editorNewRowCommand = executor.new EditorNewRowCommand();
        var doCommand = executor.new DoCommand();
        var undoCommand = executor.new UndoCommand();
        var quitCommand = executor.new QuitCommand();
        var contextSwitchCommand = executor.new ContextSwitchCommand();

        commands.put(Action.INPUT_PRINTABLE_CHAR, editorPrintableInputCommand);

        commands.put(Action.BACKSPACE_DELETE, editorDeleteCharCommand);
        commands.put(Action.DEL_DELETE, editorDeleteCharCommand);

        commands.put(Action.MOVE_CURSOR_UP, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_DOWN, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_RIGHT, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_LEFT, arrowKeysCommand);

        commands.put(Action.NEW_ROW, editorNewRowCommand);

        commands.put(Action.DO, doCommand);
        commands.put(Action.UNDO, undoCommand);
        commands.put(Action.QUIT, quitCommand);

        commands.put(Action.OPEN_FILE_EXPLORER, contextSwitchCommand);
        commands.put(Action.OPEN_DIR_EXPLORER, contextSwitchCommand);
        commands.put(Action.OPEN_OR_SAVE_FILE, contextSwitchCommand);
        commands.put(Action.DIALOG_ACTIONS, contextSwitchCommand);
        commands.put(Action.NEXT_ITEM, arrowKeysCommand);
        commands.put(Action.PREVIOUS_ITEM, arrowKeysCommand);

        return executor;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}