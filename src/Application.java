import common.Action;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.ContextType;
import context.operation.command.Command;
import context.operation.command.editorcommand.*;
import context.operation.state.EditorState;
import context.operation.state.State;
import input.InputReader;
import output.TerminalWriter;

import java.io.IOException;
import java.util.HashMap;

import static common.utility.TerminalIOUtils.eraseScreen;
import static common.utility.TerminalIOUtils.setCursorAtStart;

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
            eraseScreen();
            setCursorAtStart();
            System.out.print(ex.getMessage());
        }

        exit();
    }

    private static void init() {
        terminal = new WindowsTerminal();
        terminal.enableRawMode();

        terminalWriter = new TerminalWriter();

        var state = new EditorState();

        var commands = new HashMap<ContextType, HashMap<Action, Command>>();
        commands.put(ContextType.EDITOR, initEditorTransactions(state));
        inputReader = new InputReader(commands);

        eraseScreen();
        setCursorAtStart();
    }

    private static HashMap<Action, Command> initEditorTransactions(State editorState) {
        var editorCommands = new HashMap<Action, Command>();

        editorCommands.put(Action.INPUT_PRINTABLE_CHAR, new InputPrintableCharCommand(editorState, terminalWriter));
        editorCommands.put(Action.MOVE_CURSOR_RIGHT, new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_RIGHT));
        editorCommands.put(Action.MOVE_CURSOR_LEFT, new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_LEFT));
        editorCommands.put(Action.MOVE_CURSOR_UP, new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_UP));
        editorCommands.put(Action.MOVE_CURSOR_DOWN, new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_DOWN));
        editorCommands.put(Action.ENTER_NEW_ROW, new EnterNewRowCommand(editorState, terminalWriter));
        editorCommands.put(Action.BACKSPACE_DELETE, new BackspaceDeleteCommand(editorState, terminalWriter));
        editorCommands.put(Action.DEL_DELETE, new DelDeleteCommand(editorState, terminalWriter));

        return editorCommands;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}