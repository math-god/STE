package context.operation.state;

import app.Application;
import common.utility.CommonUtils;

import static common.escape.Escape.*;

public class TerminalWriter {

    private String fileName;
    private boolean isSaved;

    private final String UNNAMED_STR = "<unnamed>";
    private final String SAVED_STR = "saved";
    private final String NOT_SAVED_STR = "not saved";

    private final String BOTTOM_STATUS = "row: %s, column: %s";

    public void writeText(String format,
                          String text,
                          int rowIndex,
                          int columnIndex) {
        var windowContent = new StringBuilder();
        windowContent
                .append(getTopStatus())
                .append(text)
                .append(getBottomStatus(rowIndex, columnIndex));

        var formatted = String.format(format, windowContent);
        System.out.print(formatted);
    }

    public void writeText(String format, String text) {
        var formatted = String.format(format, text);
        System.out.print(formatted);
    }

    public void writeCursor(int rowIndex, int columnIndex) {
        System.out.printf(getBottomStatus(rowIndex, columnIndex) + SET_CURSOR_AT_ROW_COLUMN, rowIndex + 3 + 1, columnIndex + 1);
    }

    public void saveFileStatus(String fileName, boolean isSaved) {
        this.fileName = fileName;
        this.isSaved = isSaved;
    }

    private String getBottomStatus(int rowIndex, int columnIndex) {
        return String.format(SET_CURSOR_AT_ROW_COLUMN, Application.height, 0) +
                INVERSE_COLOR +
                String.format(BOTTOM_STATUS, rowIndex, columnIndex) +
                " ".repeat(Math.max(0, Application.width) - BOTTOM_STATUS.length()) +
                RESET_COLOR;
    }

    private String getTopStatus() {
        return HeaderBuilder.builder()
                .item(getFileNameSafely())
                .item(getStausOfFile())
                .line()
                .build()
                .toString();
    }

    private String getFileNameSafely() {
        if (CommonUtils.isEmpty(fileName)) {
            return UNNAMED_STR;
        }

        return fileName;
    }

    private String getStausOfFile() {
        if (isSaved) {
            return SAVED_STR;
        }

        return NOT_SAVED_STR;
    }
}
