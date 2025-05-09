import common.Action;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.ContextType;
import context.operation.command.Command;
import context.operation.command.editor.*;
import context.operation.command.fileexplorer.ItemSelectionCommand;
import context.operation.command.fileexplorer.OpenFileCommand;
import context.operation.state.EditorState;
import context.operation.state.FileExplorerState;
import context.operation.state.State;
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

        var commands = new HashMap<ContextType, HashMap<Action, Command>>();
        commands.put(ContextType.EDITOR, initEditorCommands(new EditorState()));
        commands.put(ContextType.FILE_EXPLORER, initFileExplorerCommands(new FileExplorerState()));
        inputReader = new InputReader(commands);

        System.out.print(ERASE_SCREEN);
        System.out.print(SET_CURSOR_AT_START);
    }

    private static HashMap<Action, Command> initEditorCommands(State state) {
        var editorCommands = new HashMap<Action, Command>();

        editorCommands.put(Action.INPUT_PRINTABLE_CHAR, new InputCharCommand(state, terminalWriter));
        editorCommands.put(Action.MOVE_CURSOR_RIGHT, new MoveCursorCommand(state, terminalWriter, Action.MOVE_CURSOR_RIGHT));
        editorCommands.put(Action.MOVE_CURSOR_LEFT, new MoveCursorCommand(state, terminalWriter, Action.MOVE_CURSOR_LEFT));
        editorCommands.put(Action.MOVE_CURSOR_UP, new MoveCursorCommand(state, terminalWriter, Action.MOVE_CURSOR_UP));
        editorCommands.put(Action.MOVE_CURSOR_DOWN, new MoveCursorCommand(state, terminalWriter, Action.MOVE_CURSOR_DOWN));
        editorCommands.put(Action.ENTER_NEW_ROW, new EnterNewRowCommand(state, terminalWriter));
        editorCommands.put(Action.BACKSPACE_DELETE, new DeleteCharCommand(state, terminalWriter, DeleteCharCommand.Type.BACKSPACE));
        editorCommands.put(Action.DEL_DELETE, new DeleteCharCommand(state, terminalWriter, DeleteCharCommand.Type.DEL));

        return editorCommands;
    }

    private static HashMap<Action, Command> initFileExplorerCommands(FileExplorerState state) {
        var fileExplorerCommands = new HashMap<Action, Command>();

        fileExplorerCommands.put(Action.OPEN_FILE, new OpenFileCommand(state, terminalWriter));
        fileExplorerCommands.put(Action.MOVE_CURSOR_UP, new ItemSelectionCommand(state, terminalWriter, Action.MOVE_CURSOR_UP));
        fileExplorerCommands.put(Action.MOVE_CURSOR_DOWN, new ItemSelectionCommand(state, terminalWriter, Action.MOVE_CURSOR_DOWN));

        return fileExplorerCommands;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}