package state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class State {

    private Integer cursorRowNumber = 0;
    private Integer cursorColumnNumber = 0;

    private final LinkedList<LinkedList<Integer>> storage;
    private final Collection<Integer> changedStorageRows = new ArrayList<>();

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());
    }

    public void addChar(Integer ch) {
        var row = storage.get(cursorRowNumber);

        if (row.size() - 1 < cursorColumnNumber) {
            row.add(ch);
        } else {
            row.add(cursorColumnNumber, ch);
        }

        changedStorageRows.add(cursorRowNumber);
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

    public Integer getCursorRowNumber() {
        return cursorRowNumber;
    }

    public Integer getCursorColumnNumber() {
        return cursorColumnNumber;
    }

    public void setCursorRowNumber(Integer row) {
        if (row < 0) throw new IllegalArgumentException("Bad state: cursor row cant be less than 0");
        this.cursorRowNumber = row;
    }

    public void setCursorColumnNumber(Integer column) {
        if (column < 0) throw new IllegalArgumentException("Bad state: cursor column cant be less than 0");
        this.cursorColumnNumber = column;
    }


}
