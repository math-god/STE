package context.operation.state;

import common.AsciiConstant;

import java.util.LinkedList;

public class EditorState implements State {
    private int cursorRowIndex = 0;
    private int cursorColumnIndex = 0;
    private int cursorColumnEdgeIndex = 0;

    private final LinkedList<StringBuilder> storage;

    private int size;

    {
        storage = new LinkedList<>();
        storage.add(new StringBuilder());
    }

    @Override
    public void addChar(int ch) {
        var row = storage.get(cursorRowIndex);
        var maxColumnIndex = row.length();

        if (maxColumnIndex <= cursorColumnIndex) {
            row.append((char) ch);
        } else {
            row.insert(cursorColumnIndex, (char) ch);
        }
        size++;
    }

    @Override
    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.deleteCharAt(columnIndex);
        size--;
    }

    @Override
    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        var ch = row.charAt(cursorColumnIndex);

        row.deleteCharAt(cursorColumnIndex);
        size--;
        return ch;
    }

    @Override
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

    @Override
    public void joinRows(int firstRowIndex, int secondRowIndex) {
        var firstRow = storage.get(firstRowIndex);
        var secondRow = storage.get(secondRowIndex);

        firstRow.append(secondRow);
    }

    @Override
    public void deleteRow(int rowIndex) {
        storage.remove(rowIndex);
    }

    @Override
    public boolean moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        var currentRowMaxColumnIndex = currentRow.length();
        var maxRowIndex = storage.size() - 1;

        if (cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex == maxRowIndex) return false;

        if (currentRow.charAt(cursorColumnIndex) == AsciiConstant.CARRIAGE_RETURN ||
                cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex < maxRowIndex) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }

        return true;
    }

    @Override
    public boolean moveCursorLeft() {
        if (cursorColumnIndex == 0 && cursorRowIndex == 0) return false;

        if (cursorColumnIndex == 0 && cursorRowIndex > 0) {
            cursorRowIndex--;
            cursorColumnIndex = storage.get(cursorRowIndex).length() - 1;
            cursorColumnEdgeIndex = storage.get(cursorRowIndex).length() - 1;
        } else {
            cursorColumnIndex--;
            cursorColumnEdgeIndex--;
        }

        return true;
    }

    @Override
    public boolean moveCursorUp() {
        if (cursorRowIndex == 0) return false;
        var previousRowSize = storage.get(cursorRowIndex - 1).length();

        cursorColumnIndex = Math.min(previousRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex--;
        return true;
    }

    @Override
    public boolean moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1) return false;
        var nextRowSize = storage.get(cursorRowIndex + 1).length();

        cursorColumnIndex = Math.min(nextRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex++;
        return true;
    }

    @Override
    public String getStringRepresentation() {
        var result = new StringBuilder();
        storage.forEach(result::append);

        return result.toString();
    }

    @Override
    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    @Override
    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    @Override
    public boolean setCursorRowIndex(Integer row) {
        if (row < 0) return false;
        this.cursorRowIndex = row;

        return true;
    }

    @Override
    public boolean setCursorColumnIndex(Integer column) {
        if (column < 0) return false;
        this.cursorColumnIndex = column;
        this.cursorColumnEdgeIndex = column;

        return true;
    }

    public int getTextSize() {
        return size;
    }

    public int getRowsCount() {
        return storage.size();
    }
}
