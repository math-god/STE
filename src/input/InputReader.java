package input;

import common.Action;
import common.CharCode;
import context.ContextType;
import context.operation.command.abstraction.Command;
import log.FileLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

public class InputReader {

    private static int inputChar;
    private static ContextType currentContext;
    private static Action action;
    private final Map<Integer, Command> commands;
    private final LinkedList<Command> commandLog = new LinkedList<>();
    private int undoStep = 0;
    private final static Map<Integer, Integer> KEY_REPLACER;

    private final static int MAX_SIZE_OF_KEY = 4;

    private final Logger logger = FileLogger.getFileLogger(InputReader.class.getName(), "input-reader-log.txt");

    static {
        KEY_REPLACER = new HashMap<>();
        KEY_REPLACER.put(Arrays.hashCode("\033[D".getBytes(StandardCharsets.UTF_8)), CharCode.LEFT_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[C".getBytes(StandardCharsets.UTF_8)), CharCode.RIGHT_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[B".getBytes(StandardCharsets.UTF_8)), CharCode.DOWN_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[A".getBytes(StandardCharsets.UTF_8)), CharCode.UP_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[3~".getBytes(StandardCharsets.UTF_8)), CharCode.DEL);
        KEY_REPLACER.put(Arrays.hashCode("".getBytes(StandardCharsets.UTF_8)), CharCode.CTRL_Z);
    }

    public InputReader(Map<Integer, Command> commands) {
        this.commands = commands;

        // default value
        currentContext = ContextType.EDITOR;
    }

    public static int getInputChar() {
        return inputChar;
    }

    public static ContextType getCurrentContext() {
        return currentContext;
    }

    public static Action getAction() {
        return action;
    }

    public boolean read() throws IOException {
        var bytes = readKey();
        if (bytes.length == 1) {
            inputChar = bytes[0];
        } else {
            inputChar = KEY_REPLACER.get(Arrays.hashCode(bytes));
        }

        action = getActionByChar(inputChar);
        if (action == Action.QUIT) return false;

        var command = commands.get(inputChar);

        if (action == Action.UNDO) {
            var iterator = commandLog.listIterator(undoStep);
            if (iterator.hasNext()) {
                var undoCommand = iterator.next();
                undoCommand.unexecute();

                undoStep++;
            }
        } else if (action == Action.DO) {
            var iterator = commandLog.listIterator(undoStep);
            if (iterator.hasPrevious()) {
                var doCommand = iterator.previous();
                doCommand.execute();

                undoStep--;
            }
        } else {
            command.execute();
            if (command.canUndo()) {
                commandLog.removeIf(Command::isUndoComplete);
                commandLog.addFirst(command.copy());
                undoStep = 0;
            }
        }

        if (action == Action.OPEN_FILE_EXPLORER || action == Action.OPEN_DIR_EXPLORER) {
            currentContext = ContextType.FILE_EXPLORER;
        } else if (action == Action.OPEN_FILE) {
            currentContext = ContextType.EDITOR;
        }

        return true;
    }

    private Action getActionByChar(int ch) throws IllegalArgumentException {
        if (ch >= CharCode.FIRST_PRINTABLE_CHAR && ch <= CharCode.LAST_PRINTABLE_CHAR)
            return Action.INPUT_PRINTABLE_CHAR;
        if (ch == CharCode.BACKSPACE) return Action.BACKSPACE_DELETE;
        if (ch == CharCode.CARRIAGE_RETURN)
            return currentContext == ContextType.EDITOR ? Action.NEW_ROW : Action.OPEN_FILE;
        if (ch == CharCode.DEVICE_CONTROL_1) return Action.QUIT;
        if (ch == CharCode.DEVICE_CONTROL_3) return Action.OPEN_DIR_EXPLORER;
        if (ch == CharCode.CANCEL) return Action.DO;
        if (ch == CharCode.SHIFT_IN) return Action.OPEN_FILE_EXPLORER;
        if (ch == CharCode.DEL) return Action.DEL_DELETE;
        if (ch == CharCode.RIGHT_ARROW) return Action.MOVE_CURSOR_RIGHT;
        if (ch == CharCode.LEFT_ARROW) return Action.MOVE_CURSOR_LEFT;
        if (ch == CharCode.UP_ARROW) return Action.MOVE_CURSOR_UP;
        if (ch == CharCode.DOWN_ARROW) return Action.MOVE_CURSOR_DOWN;
        if (ch == CharCode.CTRL_Z) return Action.UNDO;

        throw new IllegalArgumentException("Unknown char: " + ch + " for " + currentContext);
    }

    private byte[] readKey() throws IOException {
        var rawArr = new byte[MAX_SIZE_OF_KEY];
        var byteCount = System.in.read(rawArr);
        if (byteCount == -1) return new byte[]{};

        var resArr = new byte[byteCount];
        System.arraycopy(rawArr, 0, resArr, 0, byteCount);

        return resArr;
    }
}