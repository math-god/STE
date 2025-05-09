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

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.deleteCharAt(columnIndex);
        size--;
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (cursorColumnIndex > row.length() - 1) return AsciiConstant.NULL;

        var ch = row.charAt(cursorColumnIndex);
        row.deleteCharAt(cursorColumnIndex);
        size--;
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

    public void moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        var currentRowMaxColumnIndex = currentRow.length();
        var maxRowIndex = storage.size() - 1;

        if (cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex == maxRowIndex) return;

        if (currentRow.charAt(cursorColumnIndex) == AsciiConstant.CARRIAGE_RETURN ||
                cursorColumnIndex == currentRowMaxColumnIndex && cursorRowIndex < maxRowIndex) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }
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
    }

    public void moveCursorUp() {
        if (cursorRowIndex == 0) return;
        var previousRowSize = storage.get(cursorRowIndex - 1).length();

        cursorColumnIndex = Math.min(previousRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex--;
    }

    public void moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1) return;
        var nextRowSize = storage.get(cursorRowIndex + 1).length();

        cursorColumnIndex = Math.min(nextRowSize - 1, cursorColumnEdgeIndex);
        cursorRowIndex++;
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
    }

    public void setCursorColumnIndex(Integer column) {
        if (column < 0) return;
        this.cursorColumnIndex = column;
        this.cursorColumnEdgeIndex = column;
    }

    public int getTextSize() {
        return size;
    }

    public int getRowsCount() {
        return storage.size();
    }

    @Override
    public StateDataModel getData() {
        var result = new StringBuilder();
        storage.forEach(result::append);

        return new StateDataModel(result.toString(), cursorRowIndex, cursorColumnIndex);
    }
}
