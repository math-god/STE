import common.Action;
import context.ContextType;
import context.operation.editorcommand.AddCharCommand;
import context.operation.editorcommand.MoveCursorLeftCommand;
import context.operation.editorcommand.MoveCursorRightCommand;
import context.operation.transaction.Transaction;
import input.InputReader;
import output.TerminalWriter;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.operation.editorcommand.EditorState;

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

        var addCharCommand = new AddCharCommand(editorState);
        addCharCommand.attachObserver(terminalWriter);

        var moveRightCommand = new MoveCursorRightCommand(editorState);
        moveRightCommand.attachObserver(terminalWriter);

        var moveLeftCommand = new MoveCursorLeftCommand(editorState);
        moveLeftCommand.attachObserver(terminalWriter);

        editorTransactions.put(Action.INPUT_PRINTABLE_CHAR, new Transaction(List.of(addCharCommand, moveRightCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_RIGHT, new Transaction(List.of(moveRightCommand)));
        editorTransactions.put(Action.MOVE_CURSOR_LEFT, new Transaction(List.of(moveLeftCommand)));

        return editorTransactions;
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}