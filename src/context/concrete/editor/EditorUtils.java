package context.concrete.editor;

import common.Action;
import common.PrimitiveOperation;

import java.util.Collection;
import java.util.List;

import static common.infrastructure.AsciiConstant.FIRST_PRINTED_CHAR;
import static common.infrastructure.AsciiConstant.LAST_PRINTED_CHAR;

public class EditorUtils {
    static String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (int ch : row) {
            str.append((char) ch);
        }

        return str.toString();
    }

    static Action getActionByChar(Integer ch) {
        if (isTextInput(ch)) {
            return Action.INPUT_PRINTABLE_CHAR;
        }

        return Action.NONE;
    }

    static Collection<PrimitiveOperation> getOperationsByAction(Action action) {
        switch (action) {
            case INPUT_PRINTABLE_CHAR -> {
                return List.of(PrimitiveOperation.ADD_CHAR, PrimitiveOperation.CURSOR_RIGHT);
            }
        }

        return List.of(PrimitiveOperation.NONE);
    }

    private static Boolean isTextInput(Integer ch) {
        return ch >= FIRST_PRINTED_CHAR && ch <= LAST_PRINTED_CHAR;
    }
}
