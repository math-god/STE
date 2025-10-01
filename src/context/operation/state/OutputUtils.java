package context.operation.state;

import app.Application;

import static common.escape.Escape.*;

public class OutputUtils {

    private final static String STATUS = "row: %s, column: %s";

    public static void writeText(String format, String text, int rowIndex, int columnIndex) {
        var sb = new StringBuilder();
        sb.append(text).append(getStatus(rowIndex, columnIndex));

        sb.append(SET_CURSOR_AT_START);
        var formatted = String.format(format, sb);
        System.out.print(formatted);
    }

    public static void writeText(String format, String text) {
        var formatted = String.format(format, text);
        System.out.print(formatted);
    }

    public static void writeCursor(int rowIndex, int columnIndex) {
        System.out.printf(getStatus(rowIndex, columnIndex) + SET_CURSOR_AT_ROW_COLUMN, rowIndex + 1, columnIndex + 1);
    }

    private static String getStatus(int rowIndex, int columnIndex) {
        return String.format(SET_CURSOR_AT_ROW_COLUMN, Application.height, 0) +
                INVERSE_COLOR +
                String.format(STATUS, rowIndex, columnIndex) +
                " ".repeat(Math.max(0, Application.width) - STATUS.length()) +
                RESET_COLOR;
    }
}
