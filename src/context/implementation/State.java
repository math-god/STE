package context.implementation;

import common.utility.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class State {

    private Integer cursorRowIndex = 0;
    private Integer cursorColumnIndex = 0;

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

    void deleteCharAtCursor() {
        var row = storage.get(cursorRowIndex);
        if (CommonUtils.getElementOrNull(row, cursorColumnIndex) == null) return;

        row.remove((int) cursorColumnIndex);
        changedStorageRowIndexes.add(cursorRowIndex);
    }

    void moveCursorRight() {
        if (cursorColumnIndex == storage.get(cursorRowIndex).size()) return;

        cursorColumnIndex++;
    }

    void moveCursorLeft() {
        if (cursorColumnIndex == 0) return;

        cursorColumnIndex--;
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
