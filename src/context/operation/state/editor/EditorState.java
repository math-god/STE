package context.operation.state.editor;

import app.Application;
import common.CharCode;
import common.terminal.Platform;
import context.operation.state.TerminalWriter;
import log.FileLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static app.Application.PLATFORM;
import static common.escape.Escape.*;

public class EditorState {
    private int cursorRowIndex;
    private int cursorColumnIndex;
    private int cursorColumnEdgeIndex;

    private int offsetFromTop;

    private LinkedList<StringBuilder> storage;

    private final String OUTPUT_STRING = SAVE_CURSOR_POSITION + SET_CURSOR_INVISIBLE + SET_CURSOR_AT_START +
            ERASE_SCREEN + "%s" + RESTORE_CURSOR_POSITION + SET_CURSOR_VISIBLE;

    private final Logger logger = FileLogger.getFileLogger(EditorState.class.getName(), "editor-state.txt");

    private int textSize;

    private final TerminalWriter terminalWriter;

    public EditorState(TerminalWriter terminalWriter) {
        this.terminalWriter = terminalWriter;

        storage = new LinkedList<>();
        storage.add(new StringBuilder());

        terminalWriter.writeEditorText(OUTPUT_STRING, getTextData());
        terminalWriter.writeCursor(cursorRowIndex, cursorColumnIndex);
    }

    public void writeInTerminal() {
        logger.info(getTextData());
        terminalWriter.writeEditorText(OUTPUT_STRING, getTextData());
        terminalWriter.writeCursor(cursorRowIndex, offsetFromTop, cursorColumnIndex);
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
    }

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.deleteCharAt(columnIndex);
        textSize--;
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (cursorColumnIndex > row.length() - 1) return CharCode.NULL;

        var ch = row.charAt(cursorColumnIndex);
        row.deleteCharAt(cursorColumnIndex);
        textSize--;

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

        return storage.indexOf(toRow);
    }

    public void joinRows(int firstRowIndex, int secondRowIndex) {
        var firstRow = storage.get(firstRowIndex);
        var secondRow = storage.get(secondRowIndex);

        firstRow.append(secondRow);
    }

    public void deleteRow(int rowIndex) {
        storage.remove(rowIndex);
    }

    public void fillStorage(List<String> lines, String fileName, boolean savingState) {
        if (!lines.isEmpty()) {
            var list = new LinkedList<StringBuilder>();
            for (var i = 0; i < lines.size() - 1; i++) {
                list.add(new StringBuilder(lines.get(i) + (char) CharCode.CARRIAGE_RETURN));
            }
            list.add(new StringBuilder(lines.getLast()));
            storage = list;
        }
        cursorRowIndex = 0;
        cursorColumnIndex = 0;
        cursorColumnEdgeIndex = 0;
        offsetFromTop = 0;

        terminalWriter.saveFileStatus(fileName, savingState);
    }

    public void updateHeader(String fileName, boolean savingState) {
        terminalWriter.saveFileStatus(fileName, savingState);
    }

    public String getStringRepresentation() {
        return storage.toString();
    }

    public boolean moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        var currentRowMaxColumnIndex = currentRow.length();
        var maxRowIndex = storage.size() - 1;

        if (cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex == maxRowIndex)
            return false;

        if (currentRow.charAt(cursorColumnIndex) == CharCode.CARRIAGE_RETURN ||
                cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex < maxRowIndex) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }

        if (cursorRowIndex > getWorkSpaceHeight()) {
            offsetFromTop++;
        }

        return true;
    }

    public boolean moveCursorLeft() {
        if (cursorColumnIndex == 0 && cursorRowIndex == 0)
            return false;

        if (cursorColumnIndex == 0 && cursorRowIndex > 0) {
            cursorRowIndex--;
            cursorColumnIndex = storage.get(cursorRowIndex).length() - 1;
            cursorColumnEdgeIndex = storage.get(cursorRowIndex).length() - 1;
        } else {
            cursorColumnIndex--;
            cursorColumnEdgeIndex--;
        }

        if (cursorRowIndex < offsetFromTop) {
            offsetFromTop--;
        }

        return true;
    }

    public boolean moveCursorUp() {
        if (cursorRowIndex == 0)
            return false;

        var previousRowSize = storage.get(cursorRowIndex - 1).length();

        cursorColumnIndex = Math.min(previousRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex--;

        if (cursorRowIndex < offsetFromTop) {
            offsetFromTop--;
        }

        return true;
    }

    public boolean moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1)
            return false;

        var nextRowSize = storage.get(cursorRowIndex + 1).length();

        cursorColumnIndex = Math.min(nextRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex++;

        if (cursorRowIndex > getWorkSpaceHeight()) {
            offsetFromTop++;
        }

        return true;
    }

    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public void setCursorPosition(int row, int column) {
        if (row < 0 || row > storage.size() - 1) throw new IllegalArgumentException();
        if (column < 0) throw new IllegalArgumentException();

        this.cursorRowIndex = row;
        this.cursorColumnIndex = column;
        this.cursorColumnEdgeIndex = column;

        if (cursorRowIndex < offsetFromTop) {
            offsetFromTop--;
        }
        if (cursorRowIndex > getWorkSpaceHeight()) {
            offsetFromTop++;
        }
    }

    private String getTextData() {
        var stringBuilder = new StringBuilder();
        storage.subList(offsetFromTop, Math.min(getWorkSpaceHeight(), storage.size()) + offsetFromTop).forEach(stringBuilder::append);

        var result = stringBuilder.toString();
        if (PLATFORM == Platform.WINDOWS)
            result = result.replace("\r", PLATFORM.NEXT_ROW_CHAR);

        return result;
    }

    private void log() {
        logger.info("cursorRowIndex: " + cursorRowIndex +
                ", cursorColumnIndex: " + cursorColumnIndex +
                ", offsetFromTop: " + offsetFromTop +
                ", height:" + Application.height +
                ", storage:" + storage.size());
    }

    private int getWorkSpaceHeight() {
        return Application.height
                - 3 // top status
                - 1 // bottom status
                - 1; // alignment with index
    }
}
