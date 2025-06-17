package app;

import common.CharCode;
import context.operation.command.CommandExecutor;
import log.FileLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InputReader {

    private final CommandExecutor executor;

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

    public InputReader(CommandExecutor executor) {
        this.executor = executor;
    }

    public boolean read() throws IOException {
        var bytes = readKey();
        int inputChar;
        if (bytes.length == 1) {
            inputChar = bytes[0];
        } else {
            inputChar = KEY_REPLACER.get(Arrays.hashCode(bytes));
        }

        return executor.execute(inputChar);
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