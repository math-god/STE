import common.CharCode;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.operation.command.*;
import context.operation.command.abstraction.Command;
import context.operation.state.EditorState;
import context.operation.state.FileExplorerState;
import input.InputReader;
import output.TerminalWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        var commands = initCommands();
        inputReader = new InputReader(commands);

        System.out.print(ERASE_SCREEN);
        System.out.print(SET_CURSOR_AT_START);
    }

    private static Map<Integer, Command> initCommands() {
        var commands = new HashMap<Integer, Command>();
        var editorState = new EditorState(terminalWriter);
        var fileExplorerState = new FileExplorerState(terminalWriter);

        var deleteCharCommand = new DeleteCharCommand(editorState);
        var enterCharCommand = new EnterCharCommand(editorState, fileExplorerState);
        var inputCharCommand = new InputCharCommand(editorState);
        var arrowKeysCommand = new ArrowKeysCommand(editorState, fileExplorerState);
        var openFileExplorerCommand = new OpenFileExplorerCommand(fileExplorerState);

        commands.put(CharCode.DEL, deleteCharCommand);
        commands.put(CharCode.BACKSPACE, deleteCharCommand);
        commands.put(CharCode.CARRIAGE_RETURN, enterCharCommand);

        // fixme do something better
        for (var i = CharCode.FIRST_PRINTABLE_CHAR; i <= CharCode.LAST_PRINTABLE_CHAR; i++) {
            commands.put(i, inputCharCommand);
        }

        commands.put(CharCode.DOWN_ARROW, arrowKeysCommand);
        commands.put(CharCode.UP_ARROW, arrowKeysCommand);
        commands.put(CharCode.LEFT_ARROW, arrowKeysCommand);
        commands.put(CharCode.RIGHT_ARROW, arrowKeysCommand);
        commands.put(CharCode.SHIFT_IN, openFileExplorerCommand);
        commands.put(CharCode.DEVICE_CONTROL_3, openFileExplorerCommand);

        return commands;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}