import common.Action;
import context.ContextType;
import context.operation.command.editorcommand.*;
import context.operation.command.transaction.Transaction;
import input.InputReader;
import output.TerminalWriter;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.operation.state.EditorState;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

        var transactions = new HashMap<ContextType, HashMap<Action, Transaction>>();
        transactions.put(ContextType.EDITOR, initEditorTransactions(state));
        inputReader = new InputReader(transactions);

        eraseScreen();
        setCursorAtStart();
    }

    private static HashMap<Action, Transaction> initEditorTransactions(EditorState editorState) {
        var editorTransactions = new HashMap<Action, Transaction>();

        var addCharCommand = new AddCharCommand(editorState, terminalWriter);
        var deleteCharCommand = new DeleteCharCommand(editorState, terminalWriter);
        var addRowCommand = new AddRowCommand(editorState, terminalWriter);
        var moveRightCommand = new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_RIGHT);
        var moveLeftCommand = new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_LEFT);
        var moveUpCommand = new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_UP);
        var moveDownCommand = new MoveCursorCommand(editorState, terminalWriter, Action.MOVE_CURSOR_DOWN);
        var moveCursorAtStartCommand = new MoveCursorAtStartCommand(editorState, terminalWriter);

        editorTransactions.put(Action.INPUT_PRINTABLE_CHAR, new Transaction(List.of(addCharCommand, moveRightCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_RIGHT, new Transaction(List.of(moveRightCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_LEFT, new Transaction(List.of(moveLeftCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_UP, new Transaction(List.of(moveUpCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_DOWN, new Transaction(List.of(moveDownCommand)));
        editorTransactions.put(Action.ENTER_NEW_ROW, new Transaction(List.of(addCharCommand, addRowCommand, moveCursorAtStartCommand)));
        editorTransactions.put(Action.BACKSPACE_DELETE, new Transaction(List.of(moveLeftCommand, deleteCharCommand)));
        editorTransactions.put(Action.DEL_DELETE, new Transaction(List.of(deleteCharCommand)));

        return editorTransactions;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}