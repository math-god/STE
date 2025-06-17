package context.operation.state;

import static common.escape.Escape.SET_CURSOR_AT_ROW_COLUMN;

public class OutputUtils {

    public static void writeText(String format, String text) {
        var formatted = String.format(format, text);
        System.out.print(formatted);
    }

    public static void writeCursor(int rowIndex, int columnIndex) {
        System.out.printf(SET_CURSOR_AT_ROW_COLUMN, rowIndex + 1, columnIndex + 1);
    }
}
