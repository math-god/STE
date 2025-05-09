package input;

import common.Action;
import common.AsciiConstant;
import common.ReplaceCode;
import context.ContextType;
import context.operation.command.Command;
import context.operation.command.UndoCommand;
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
    private final Map<ContextType, HashMap<Action, Command>> commands;
    private final LinkedList<UndoCommand> commandLog = new LinkedList<>();
    private int undoStep = 0;
    private final static Map<Integer, Integer> KEY_REPLACER;

    private final static int MAX_SIZE_OF_KEY = 4;

    private final Logger logger = FileLogger.getFileLogger(InputReader.class.getName(), "input-reader-log.txt");

    static {
        KEY_REPLACER = new HashMap<>();
        KEY_REPLACER.put(Arrays.hashCode("\033[D".getBytes(StandardCharsets.UTF_8)), ReplaceCode.LEFT_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[C".getBytes(StandardCharsets.UTF_8)), ReplaceCode.RIGHT_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[B".getBytes(StandardCharsets.UTF_8)), ReplaceCode.DOWN_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[A".getBytes(StandardCharsets.UTF_8)), ReplaceCode.UP_ARROW);
        KEY_REPLACER.put(Arrays.hashCode("\033[3~".getBytes(StandardCharsets.UTF_8)), ReplaceCode.DEL);
        KEY_REPLACER.put(Arrays.hashCode("".getBytes(StandardCharsets.UTF_8)), ReplaceCode.CTRL_Z);
    }

    public InputReader(Map<ContextType, HashMap<Action, Command>> commands) {
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

    public boolean read() throws IOException {
        var bytes = readKey();
        if (bytes.length == 1) {
            inputChar = bytes[0];
        } else {
            inputChar = KEY_REPLACER.get(Arrays.hashCode(bytes));
        }

        var action = getActionByChar(inputChar);
        if (action == Action.QUIT) return false;
        if (action == Action.OPEN_FILE) {
            currentContext = ContextType.FILE_EXPLORER;
        }

        var command = commands.get(currentContext).get(action);

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
            if (command instanceof UndoCommand) {
                commandLog.removeIf(UndoCommand::isUndoComplete);
                commandLog.addFirst(((UndoCommand) command).copy());
                undoStep = 0;
            }
        }
        return true;
    }

    private Action getActionByChar(int ch) throws IllegalArgumentException {
        if (ch >= AsciiConstant.FIRST_PRINTABLE_CHAR && ch <= AsciiConstant.LAST_PRINTABLE_CHAR)
            return Action.INPUT_PRINTABLE_CHAR;
        if (ch == AsciiConstant.BACKSPACE) return Action.BACKSPACE_DELETE;
        if (ch == AsciiConstant.CARRIAGE_RETURN) return Action.ENTER_NEW_ROW;
        if (ch == AsciiConstant.DEVICE_CONTROL_1) return Action.QUIT;
        if (ch == AsciiConstant.CANCEL) return Action.DO;
        if (ch == AsciiConstant.SHIFT_IN) return Action.OPEN_FILE;

        if (ch == ReplaceCode.DEL) return Action.DEL_DELETE;
        if (ch == ReplaceCode.RIGHT_ARROW) return Action.MOVE_CURSOR_RIGHT;
        if (ch == ReplaceCode.LEFT_ARROW) return Action.MOVE_CURSOR_LEFT;
        if (ch == ReplaceCode.UP_ARROW) return Action.MOVE_CURSOR_UP;
        if (ch == ReplaceCode.DOWN_ARROW) return Action.MOVE_CURSOR_DOWN;
        if (ch == ReplaceCode.CTRL_Z) return Action.UNDO;

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