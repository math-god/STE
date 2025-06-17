package context.operation.state.editor;

import common.CharCode;
import common.terminal.Platform;
import context.operation.state.OutputUtils;

import java.util.LinkedList;
import java.util.List;

import static app.Application.PLATFORM;
import static common.escape.Escape.*;
import static common.escape.Escape.SET_CURSOR_VISIBLE;

public class EditorState {
    private int cursorRowIndex = 0;
    private int cursorColumnIndex = 0;
    private int cursorColumnEdgeIndex = 0;

    private LinkedList<StringBuilder> storage;

    private final String OUTPUT_STRING = SAVE_CURSOR_POSITION + SET_CURSOR_INVISIBLE + SET_CURSOR_AT_START +
            ERASE_IN_DISPLAY + "%s" + RESTORE_CURSOR_POSITION + SET_CURSOR_VISIBLE;

    private int size;

    {
        storage = new LinkedList<>();
        storage.add(new StringBuilder());
    }

    public void addChar(int ch) {
        var row = storage.get(cursorRowIndex);
        var maxColumnIndex = row.length();

        if (maxColumnIndex <= cursorColumnIndex) {
            row.append((char) ch);
        } else {
            row.insert(cursorColumnIndex, (char) ch);
        }
        size++;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.deleteCharAt(columnIndex);
        size--;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (cursorColumnIndex > row.length() - 1) return CharCode.NULL;

        var ch = row.charAt(cursorColumnIndex);
        row.deleteCharAt(cursorColumnIndex);
        size--;

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

    public void fillStorage(List<String> lines) {
        if (!lines.isEmpty()) {
            var list = new LinkedList<StringBuilder>();
            for (var i = 0; i < lines.size() - 1; i++) {
                list.add(new StringBuilder(lines.get(i) + (char) CharCode.CARRIAGE_RETURN));
            }
            list.add(new StringBuilder(lines.get(lines.size() - 1)));
            storage = list;
        }
        cursorRowIndex = 0;
        cursorColumnIndex = 0;
        cursorColumnEdgeIndex = 0;

        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void sendDataToTerminal() {
        OutputUtils.writeText(OUTPUT_STRING, getTextData());
        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public String getStringRepresentation() {
        var result = new StringBuilder();
        storage.forEach(result::append);

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
        if (cursorColumnIndex == 0 && cursorRowIndex == 0) return;

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
        if (cursorRowIndex == 0) return;
        var previousRowSize = storage.get(cursorRowIndex - 1).length();

        cursorColumnIndex = Math.min(previousRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex--;

        OutputUtils.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1) return;
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
        return size;
    }

    public int getRowsCount() {
        return storage.size();
    }

    private String getTextData() {
        var stringBuilder = new StringBuilder();
        storage.forEach(stringBuilder::append);

        var result = stringBuilder.toString();
        if (PLATFORM == Platform.WINDOWS)
            result = result.replace("\r", "\r\n");

        return result;
    }
}
