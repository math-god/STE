package context.operation.state.editor;

import common.CharCode;
import common.terminal.Platform;
import common.utility.CommonUtils;
import context.operation.state.HeaderBuilder;
import context.operation.state.OutputUtils;
import log.FileLogger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static app.Application.PLATFORM;
import static common.escape.Escape.*;

public class EditorState {
    private int cursorRowIndex;
    private int cursorColumnIndex;
    private int cursorColumnEdgeIndex;

    private final int minimalRowIndex;

    private LinkedList<StringBuilder> storage;
    private HeaderBuilder.Header header;

    private final String UNNAMED_STR = "<unnamed>";
    private final String SAVED_STR = "saved";
    private final String NOT_SAVED_STR = "not saved";

    private final String OUTPUT_STRING = SAVE_CURSOR_POSITION + SET_CURSOR_INVISIBLE + SET_CURSOR_AT_START +
            ERASE_IN_DISPLAY + "%s" + RESTORE_CURSOR_POSITION + SET_CURSOR_VISIBLE;

    private final Logger logger = FileLogger.getFileLogger(EditorState.class.getName(), "editor-state.txt");

    private int textSize;

    {
        header = HeaderBuilder.builder()
                .item("<unnamed>")
                .item(NOT_SAVED_STR)
                .line()
                .build();

        cursorRowIndex = header.getSize();
        minimalRowIndex = cursorRowIndex;

        storage = new LinkedList<>();
        Arrays.stream(header.getHeaderItems())
                .forEach(item -> storage.add(new StringBuilder(item + (char) CharCode.CARRIAGE_RETURN)));
        storage.add(new StringBuilder());

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void addChar(int ch) {
        var row = storage.get(cursorRowIndex);
        var maxColumnIndex = row.length();

        if (maxColumnIndex <= cursorColumnIndex) {
            row.append((char) ch);
        } else {
            row.insert(cursorColumnIndex, (char) ch);
        }
        textSize++;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.deleteCharAt(columnIndex);
        textSize--;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (cursorColumnIndex > row.length() - 1) return CharCode.NULL;

        var ch = row.charAt(cursorColumnIndex);
        row.deleteCharAt(cursorColumnIndex);
        textSize--;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        return ch;
    }

    public int addRow() {
        var fromRow = storage.get(cursorRowIndex);
        var modifiedFromRow = new StringBuilder(fromRow.substring(0, cursorColumnIndex));
        var toRow = new StringBuilder(fromRow.substring(cursorColumnIndex, fromRow.length()));

        storage.set(cursorRowIndex, modifiedFromRow);
        var maxRowIndex = storage.size() - 1;
        if (cursorRowIndex == maxRowIndex) {
            storage.add(toRow);
        } else {
            storage.add(cursorRowIndex + 1, toRow);
        }

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        return storage.indexOf(toRow);
    }

    public void joinRows(int firstRowIndex, int secondRowIndex) {
        var firstRow = storage.get(firstRowIndex);
        var secondRow = storage.get(secondRowIndex);

        firstRow.append(secondRow);
        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public void deleteRow(int rowIndex) {
        storage.remove(rowIndex);
        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public void fillStorage(List<String> lines, String fileName, boolean savingState) {
        if (!lines.isEmpty()) {
            var list = new LinkedList<StringBuilder>();
            for (var i = 0; i < lines.size() - 1; i++) {
                list.add(new StringBuilder(lines.get(i) + (char) CharCode.CARRIAGE_RETURN));
            }
            list.add(new StringBuilder(lines.get(lines.size() - 1)));
            storage = list;
        }
        cursorRowIndex = minimalRowIndex;
        cursorColumnIndex = 0;
        cursorColumnEdgeIndex = 0;

        header = HeaderBuilder.builder()
                .item(fileName)
                .item(savingState ? SAVED_STR : NOT_SAVED_STR)
                .line()
                .build();

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void updateHeader(String fileName, boolean savingState) {
        fileName = CommonUtils.isEmpty(fileName) ? UNNAMED_STR : fileName;

        header = HeaderBuilder.builder()
                .item(fileName)
                .item(savingState ? SAVED_STR : NOT_SAVED_STR)
                .line()
                .build();

        for (var i = 0; i < header.getSize(); i++) {
            storage.set(i, new StringBuilder(header.getHeaderItems()[i] + (char) CharCode.CARRIAGE_RETURN));
        }

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public void sendDataToTerminal() {
        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public String getStringRepresentation() {
        var result = new StringBuilder();
        logger.info(String.valueOf(header.getSize()));

        for (var i = header.getSize(); i < storage.size(); i++) {
            result.append(storage.get(i));
        }

        return result.toString();
    }

    public void moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        var currentRowMaxColumnIndex = currentRow.length();
        var maxRowIndex = storage.size() - 1;

        if (cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex == maxRowIndex) return;

        if (currentRow.charAt(cursorColumnIndex) == CharCode.CARRIAGE_RETURN ||
                cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex < maxRowIndex) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void moveCursorLeft() {
        if (cursorColumnIndex == 0 && cursorRowIndex == minimalRowIndex)
            return;

        if (cursorColumnIndex == 0 && cursorRowIndex > 0) {
            cursorRowIndex--;
            cursorColumnIndex = storage.get(cursorRowIndex).length() - 1;
            cursorColumnEdgeIndex = storage.get(cursorRowIndex).length() - 1;
        } else {
            cursorColumnIndex--;
            cursorColumnEdgeIndex--;
        }

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void moveCursorUp() {
        if (cursorRowIndex == minimalRowIndex)
            return;

        var previousRowSize = storage.get(cursorRowIndex - 1).length();

        cursorColumnIndex = Math.min(previousRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex--;

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1)
            return;

        var nextRowSize = storage.get(cursorRowIndex + 1).length();

        cursorColumnIndex = Math.min(nextRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex++;

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public void setCursorRowIndex(Integer row) {
        if (row < 0) return;
        this.cursorRowIndex = row;

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void setCursorColumnIndex(Integer column) {
        if (column < 0) return;
        this.cursorColumnIndex = column;
        this.cursorColumnEdgeIndex = column;

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public int getTextSize() {
        return textSize;
    }

    public int getRowsCount() {
        return storage.size();
    }

    private String getTextData() {
        var stringBuilder = new StringBuilder();
        storage.forEach(stringBuilder::append);

        var result = stringBuilder.toString();
        if (PLATFORM == Platform.WINDOWS)
            result = result.replace("\r", PLATFORM.NEXT_ROW_CHAR);

        return result;
    }
}
