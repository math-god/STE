package context.operation.editorcommand;

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
    private final Collection<Integer> changedStorageRowIndexes = new ArrayList<>();

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());
    }

    void addChar(Integer ch) {
        var row = storage.get(cursorRowIndex);

        if (row.size() - 1 < cursorColumnIndex) {
            row.add(ch);
        } else {
            row.add(cursorColumnIndex, ch);
        }

        changedStorageRowIndexes.add(cursorRowIndex);
    }

    void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.remove(columnIndex);
    }

    void deleteCharAtCursor() {
        var row = storage.get(cursorRowIndex);
        if (CommonUtils.getElementOrNull(row, cursorColumnIndex) == null) return;

        row.remove(cursorColumnIndex);
        changedStorageRowIndexes.add(cursorRowIndex);
    }

    void addNewRow() {
        var firstRow = storage.get(cursorRowIndex);

        var modifiedFirstRow = firstRow.subList(0, cursorColumnIndex);
        var secondRow = firstRow.subList(cursorColumnIndex, firstRow.size());

        storage.set(cursorRowIndex, (new LinkedList<>(modifiedFirstRow)));
        if (cursorRowIndex == storage.size() - 1) {
            storage.add(new LinkedList<>(secondRow));
        } else {
            storage.add(cursorRowIndex + 1, new LinkedList<>(secondRow));
        }

        for (var i = cursorRowIndex; i < storage.size(); i++) {
            changedStorageRowIndexes.add(i);
        }
    }

    void moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        currentRow.removeIf(m -> m == AsciiConstant.ENTER);
        if (cursorColumnIndex == currentRow.size() && cursorRowIndex == storage.size() - 1) return;

        if (cursorColumnIndex == storage.get(cursorRowIndex).size() && cursorRowIndex < storage.size() - 1) {
            cursorRowIndex++;
            cursorColumnIndex = 0;
            cursorColumnEdgeIndex = 0;
        } else {
            cursorColumnIndex++;
            cursorColumnEdgeIndex++;
        }
    }

    void moveCursorLeft() {
        if (cursorColumnIndex == 0 && cursorRowIndex == 0) return;

        if (cursorColumnIndex == 0 && cursorRowIndex > 0) {
            cursorRowIndex--;
            cursorColumnIndex = storage.get(cursorRowIndex).size();
            cursorColumnEdgeIndex = storage.get(cursorRowIndex).size();
        } else {
            cursorColumnIndex--;
            cursorColumnEdgeIndex--;
        }
    }

    void moveCursorUp() {
        if (cursorRowIndex == 0) return;

        var previousRowSize = storage.get(cursorRowIndex - 1).size();
        cursorColumnIndex = Math.min(previousRowSize, cursorColumnEdgeIndex);

        cursorRowIndex--;
    }

    void moveCursorDown() {
        if (cursorRowIndex == storage.size() - 1) return;

        var nextRowSize = storage.get(cursorRowIndex + 1).size();
        cursorColumnIndex = Math.min(nextRowSize, cursorColumnEdgeIndex);

        cursorRowIndex++;
    }

    void setCursorAtStartOfNextRow() {
        cursorRowIndex++;
        cursorColumnIndex = 0;
        cursorColumnEdgeIndex = 0;
    }

    boolean isCursorNotAtStart() {
        return cursorColumnIndex > 0;
    }

    Collection<Integer> getStorageRow(Integer rowIndex) {
        return storage.get(rowIndex);
    }

    Collection<Collection<Integer>> getStorageRows(Collection<Integer> rowIndexes) {
        var rows = new ArrayList<Collection<Integer>>();
        rowIndexes.forEach(m -> rows.add(storage.get(m)));

        return rows;
    }

    Collection<Integer> getChangedStorageRowIndexesWithClearing() {
        var changedRows = new ArrayList<>(changedStorageRowIndexes);
        changedStorageRowIndexes.clear();

        return changedRows;
    }

    Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    void setCursorRowIndex(Integer row) {
        if (row < 0) throw new IllegalArgumentException("Bad state: cursor row cant be less than 0");
        this.cursorRowIndex = row;
    }

    void setCursorColumnIndex(Integer column) {
        if (column < 0) throw new IllegalArgumentException("Bad state: cursor column cant be less than 0");
        this.cursorColumnIndex = column;
    }

}
