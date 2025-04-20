package context.operation.state;

import common.AsciiConstant;
import common.utility.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class EditorState {

    private int cursorRowIndex = 0;
    private int cursorColumnIndex = 0;
    private int cursorColumnEdgeIndex = 0;

    private final LinkedList<LinkedList<Integer>> storage;
    private final LinkedList<Integer> testStor;
    private final Collection<Integer> changedStorageRowIndexes = new ArrayList<>();
    private int size;

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());

        testStor = new LinkedList<>();
    }

    public int getTextSize() {
        return size;
    }

    public void addChar(Integer ch) {
        var row = storage.get(cursorRowIndex);

        if (row.size() - 1 < cursorColumnIndex) {
            row.add(ch);
        } else {
            row.add(cursorColumnIndex, ch);
        }

        size++;
        changedStorageRowIndexes.add(cursorRowIndex);
    }

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.remove(columnIndex);
        size--;
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (CommonUtils.getElementOrNull(row, cursorColumnIndex) == null) return AsciiConstant.NULL;

        var ch = row.remove(cursorColumnIndex);
        changedStorageRowIndexes.add(cursorRowIndex);

        return ch;
    }

    public int addRow() {
        var fromRow = storage.get(cursorRowIndex);

        var modifiedFromRow = fromRow.subList(0, cursorColumnIndex);
        var toRow = new LinkedList<>(fromRow.subList(cursorColumnIndex, fromRow.size()));

        storage.set(cursorRowIndex, (new LinkedList<>(modifiedFromRow)));
        if (cursorRowIndex == storage.size() - 1) {
            storage.add(toRow);
        } else {
            storage.add(cursorRowIndex + 1, toRow);
        }

        for (var i = cursorRowIndex; i < storage.size(); i++) {
            changedStorageRowIndexes.add(i);
        }

        return storage.indexOf(toRow);
    }

    public void deleteRow(int rowIndex) {
        storage.remove(rowIndex);

        for (var i = rowIndex; i < storage.size(); i++) {
            changedStorageRowIndexes.add(i);
        }
    }

    public boolean moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        currentRow.removeIf(m -> m == AsciiConstant.ENTER);
        if (cursorColumnIndex == currentRow.size() && cursorRowIndex == storage.size() - 1) return false;

        if (cursorColumnIndex == storage.get(cursorRowIndex).size() && cursorRowIndex < storage.size() - 1) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }

        return true;
    }

    public boolean moveCursorLeft() {
        if (cursorColumnIndex == 0 && cursorRowIndex == 0) return false;

        if (cursorColumnIndex == 0 && cursorRowIndex > 0) {
            cursorRowIndex--;
            cursorColumnIndex = storage.get(cursorRowIndex).size();
            cursorColumnEdgeIndex = storage.get(cursorRowIndex).size();
        } else {
            cursorColumnIndex--;
            cursorColumnEdgeIndex--;
        }

        return true;
    }

    public boolean moveCursorUp() {
        if (cursorRowIndex == 0) return false;

        var previousRowSize = storage.get(cursorRowIndex - 1).size();
        cursorColumnIndex = Math.min(previousRowSize, cursorColumnEdgeIndex);

        cursorRowIndex--;

        return true;
    }

    public boolean moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1) return false;

        var nextRowSize = storage.get(cursorRowIndex + 1).size();
        cursorColumnIndex = Math.min(nextRowSize, cursorColumnEdgeIndex);

        cursorRowIndex++;

        return true;
    }

    public boolean isCursorNotAtStart() {
        return cursorColumnIndex > 0;
    }

    public Collection<Integer> getStorageRow(Integer rowIndex) {
        return storage.get(rowIndex);
    }

    public Collection<Collection<Integer>> getStorageRows(Collection<Integer> rowIndexes) {
        var rows = new ArrayList<Collection<Integer>>();
        rowIndexes.forEach(m -> rows.add(storage.get(m)));

        return rows;
    }

    public Collection<Integer> getChangedStorageRowIndexesWithClearing() {
        var changedRows = new ArrayList<>(changedStorageRowIndexes);
        changedStorageRowIndexes.clear();

        return changedRows;
    }

    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public boolean setCursorRowIndex(Integer row) {
        if (row < 0) return false;
        this.cursorRowIndex = row;

        return true;
    }

    public boolean setCursorColumnIndex(Integer column) {
        if (column < 0) return false;

        this.cursorColumnIndex = column;
        this.cursorColumnEdgeIndex = column;

        return true;
    }
}
