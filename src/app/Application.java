package app;

import common.Action;
import common.terminal.Platform;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.operation.command.CommandExecutor;
import context.operation.state.TerminalWriter;
import context.operation.state.dialog.DialogState;
import context.operation.state.editor.EditorState;
import context.operation.state.fileexplorer.FileExplorerState;

import java.io.IOException;
import java.util.HashMap;

import static common.escape.Escape.ERASE_SCREEN;
import static common.escape.Escape.SET_CURSOR_AT_START;

public class Application {
    private static InputReader inputReader;
    private static Terminal terminal;

    public final static Platform PLATFORM = getPlatform();
    public static int width;
    public static int height;

    public static int positionX;
    public static int positionY;

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        try {
            init();

            while (true) {
                width = terminal.getWindowSize().columns();
                height = terminal.getWindowSize().rows();
                positionX = terminal.getCursorPosition().x();
                positionY = terminal.getCursorPosition().y();

                var res = inputReader.read();
                if (!res) break;
            }
        } catch (IllegalArgumentException | IOException ex) {
            System.out.print(ERASE_SCREEN);
            System.out.print(SET_CURSOR_AT_START);
            System.out.print(ex.getMessage());
        } finally {
            exit();
        }
    }

    private static void init() {
        System.out.print(ERASE_SCREEN);
        System.out.print(SET_CURSOR_AT_START);

        terminal = new WindowsTerminal();
        terminal.enableRawMode();

        width = terminal.getWindowSize().columns();
        height = terminal.getWindowSize().rows();

        var commands = initExecutor();
        inputReader = new InputReader(commands);
    }

    private static CommandExecutor initExecutor() {
        var writer = new TerminalWriter();

        var commands = new HashMap<Action, CommandExecutor.Command>();
        var editorState = new EditorState(writer);
        var fileExplorerState = new FileExplorerState(writer);
        var dialogState = new DialogState(writer);
        var executor = new CommandExecutor(editorState, fileExplorerState, dialogState, commands);

        var editorPrintableInputCommand = executor.new EditorPrintableInputCommand();
        var editorDeleteCharCommand = executor.new EditorDeleteCharCommand();
        var arrowKeysCommand = executor.new ArrowKeysCommand();
        var editorNewRowCommand = executor.new EditorNewRowCommand();
        var doCommand = executor.new DoCommand();
        var undoCommand = executor.new UndoCommand();
        var quitCommand = executor.new QuitCommand();
        var contextSwitchCommand = executor.new ContextSwitchCommand();
        var inputFileName = executor.new SaveFileExplorerInputFileNameCommand();
        var deleteFileName = executor.new FileNameDeleteCharCommand();

        commands.put(Action.INPUT_PRINTABLE_CHAR, editorPrintableInputCommand);
        commands.put(Action.INPUT_FILE_NAME, inputFileName);

        commands.put(Action.BACKSPACE_DELETE, editorDeleteCharCommand);
        commands.put(Action.DEL_DELETE, editorDeleteCharCommand);
        commands.put(Action.BACKSPACE_DELETE_FILE_NAME, deleteFileName);
        commands.put(Action.DEL_DELETE_FILE_NAME, deleteFileName);

        commands.put(Action.MOVE_CURSOR_UP, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_DOWN, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_RIGHT, arrowKeysCommand);
        commands.put(Action.MOVE_CURSOR_LEFT, arrowKeysCommand);
        commands.put(Action.MOVE_LEFT_FILE_NAME, arrowKeysCommand);
        commands.put(Action.MOVE_RIGHT_FILE_NAME, arrowKeysCommand);

        commands.put(Action.NEW_ROW, editorNewRowCommand);

        commands.put(Action.DO, doCommand);
        commands.put(Action.UNDO, undoCommand);
        commands.put(Action.QUIT, quitCommand);

        commands.put(Action.OPEN_FILE_EXPLORER, contextSwitchCommand);
        commands.put(Action.OPEN_DIR_EXPLORER, contextSwitchCommand);
        commands.put(Action.OPEN_OR_SAVE_FILE, contextSwitchCommand);
        commands.put(Action.DIALOG_ACTIONS, contextSwitchCommand);
        commands.put(Action.OPEN_HELP_PAGE, contextSwitchCommand);
        commands.put(Action.NEXT_ITEM, arrowKeysCommand);
        commands.put(Action.PREVIOUS_ITEM, arrowKeysCommand);

        return executor;
    }

    private static void exit() {
        terminal.disableRawMode();
    }

    private static Platform getPlatform() {
        var windows = "windows";
        var mac = "mac";
        var linux = "linux";

        var systemName = System.getProperty("os.name");

        if (systemName.toLowerCase().contains(windows))
            return Platform.WINDOWS;

        if (systemName.toLowerCase().contains(mac))
            return Platform.MAC;

        if (systemName.toLowerCase().contains(linux))
            return Platform.LINUX;

        throw new RuntimeException("Can't determine OS");
    }
}