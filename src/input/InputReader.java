package input;

import common.Action;
import common.AsciiConstant;
import common.escape.EscapeReplaceCode;
import common.escape.EscapeSequenceBuilder;
import context.ContextType;
import context.operation.command.transaction.Transaction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static common.utility.TerminalIOUtils.readKey;

public class InputReader {

    private static int inputChar;
    private ContextType currentContext;
    private final Map<ContextType, HashMap<Action, Transaction>> transactions;
    private final EscapeSequenceBuilder escapeSequenceBuilder = new EscapeSequenceBuilder();

    public InputReader(Map<ContextType, HashMap<Action, Transaction>> transactions) {
        this.transactions = transactions;

        // editor is default value
        currentContext = ContextType.EDITOR;
    }

    public static int getInputChar() {
        return inputChar;
    }

    public Boolean read() throws IOException {
        var key = readKey();
        if (key == 'q') return false;
        if (escapeSequenceBuilder.isEscapeSequence(key)) {
            Integer replacer;
            do {
                var escapeSequenceChar = readKey();
                escapeSequenceBuilder.add(escapeSequenceChar);
                replacer = escapeSequenceBuilder.getReplaceOrNull();
            } while (Objects.equals(replacer, null));

            key = replacer;
        }
        inputChar = key;

        var action = getActionByChar(key);
        var transaction = transactions.get(currentContext).get(action);
        transaction.execute();
        return true;
    }

    private Action getActionByChar(Integer ch) throws IllegalArgumentException {
        if (ch >= AsciiConstant.FIRST_PRINTED_CHAR && ch <= AsciiConstant.LAST_PRINTED_CHAR)
            return Action.INPUT_PRINTABLE_CHAR;
        if (ch == AsciiConstant.BACKSPACE) return Action.BACKSPACE_DELETE;
        if (ch == EscapeReplaceCode.DEL) return Action.DEL_DELETE;
        if (ch == EscapeReplaceCode.RIGHT_ARROW) return Action.MOVE_CURSOR_RIGHT;
        if (ch == EscapeReplaceCode.LEFT_ARROW) return Action.MOVE_CURSOR_LEFT;
        if (ch == EscapeReplaceCode.UP_ARROW) return Action.MOVE_CURSOR_UP;
        if (ch == EscapeReplaceCode.DOWN_ARROW) return Action.MOVE_CURSOR_DOWN;
        if (ch == AsciiConstant.ENTER) return Action.ENTER_NEW_ROW;

        throw new IllegalArgumentException("Unknown char: " + ch + " for " + currentContext);
    }
}