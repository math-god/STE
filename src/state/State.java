package state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import common.escape.EscapeReplaceCode;

public class State {

    private Integer cursorRowIndex = 0;
    private Integer cursorColumnIndex = 0;

    private final LinkedList<LinkedList<Integer>> storage;
    private final Collection<Integer> changedStorageRows = new ArrayList<>();

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());
    }

    public void addChar(Integer ch) {
        var row = storage.get(cursorRowIndex);

        if (row.size() - 1 < cursorColumnIndex) {
            row.add(ch);
        } else {
            row.add(cursorColumnIndex, ch);
        }

        changedStorageRows.add(cursorRowIndex);
    }

    public void moveCursor(Integer ch) {
        var escapeReplaceElement = EscapeReplaceCode.get(ch);

        switch (escapeReplaceElement) {
            case RIGHT_ARROW -> cursorColumnIndex++;
        }
    }

    public Collection<Integer> getStorageRow(Integer rowIndex) {
        return storage.get(rowIndex);
    }

    public Collection<Collection<Integer>> getStorageRows(Collection<Integer> rowIndexes) {
        var rows = new ArrayList<Collection<Integer>>();
        rowIndexes.forEach(m -> rows.add(storage.get(m)));

        return rows;
    }

    public Collection<Integer> getChangedStorageRowsWithClearing() {
        var changedRows = new ArrayList<>(changedStorageRows);
        changedStorageRows.clear();

        return changedRows;
    }

    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public void setCursorRowIndex(Integer row) {
        if (row < 0) throw new IllegalArgumentException("Bad state: cursor row cant be less than 0");
        this.cursorRowIndex = row;
    }

    public void setCursorColumnIndex(Integer column) {
        if (column < 0) throw new IllegalArgumentException("Bad state: cursor column cant be less than 0");
        this.cursorColumnIndex = column;
    }


}
