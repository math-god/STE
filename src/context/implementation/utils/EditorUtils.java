package context.implementation.utils;

import common.Action;
import common.PrimitiveOperation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static common.infrastructure.AsciiConstant.*;

public class EditorUtils {
    public static String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (int ch : row) {
            str.append((char) ch);
        }

        return str.toString();
    }

   public static Action getActionByChar(Integer ch) {
        if (isTextInput(ch)) return Action.INPUT_PRINTABLE_CHAR;
        if (isBackspace(ch)) return Action.BACKSPACE_DELETE;

        return Action.NONE;
    }

    public static Collection<PrimitiveOperation> getOperationsByAction(Action action) {
        switch (action) {
            case INPUT_PRINTABLE_CHAR -> {
                return List.of(PrimitiveOperation.ADD_CHAR, PrimitiveOperation.CURSOR_RIGHT);
            }
            case BACKSPACE_DELETE -> {
                return List.of(PrimitiveOperation.CURSOR_LEFT, PrimitiveOperation.DELETE_CHAR);
            }
        }

        return List.of(PrimitiveOperation.NONE);
    }

    private static Boolean isTextInput(Integer ch) {
        return ch >= FIRST_PRINTED_CHAR && ch <= LAST_PRINTED_CHAR;
    }

    private static Boolean isBackspace(Integer ch) {
        return Objects.equals(ch, BACKSPACE);
    }
}
