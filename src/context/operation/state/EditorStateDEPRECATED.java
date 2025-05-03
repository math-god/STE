package context.operation.state;

import common.AsciiConstant;
import common.utility.CommonUtils;
import log.FileLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

@Deprecated
public class EditorStateDEPRECATED implements State {

    private int cursorRowIndex = 0;
    private int cursorColumnIndex = 0;
    private int cursorColumnEdgeIndex = 0;

    private final LinkedList<LinkedList<Integer>> storage;
    private final StringBuilder stringBuilder;
    private final LinkedList<Integer> offsets;
    private int size;
    private int rowsCount = 1;

    private final Logger logger = FileLogger.getFileLogger(EditorStateDEPRECATED.class.getName(), "editor-state.txt");

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());

        stringBuilder = new StringBuilder();

        offsets = new LinkedList<>();
        offsets.add(0);
        offsets.add(0);
    }

    public int getTextSize() {
        return size;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public void addChar(int ch) {
        var row = storage.get(cursorRowIndex);

        var maxColumnIndex = row.size();
        if (maxColumnIndex <= cursorColumnIndex) {
            row.add(ch);
        } else {
            row.add(cursorColumnIndex, ch);
        }

        stringBuilder.insert(offsets.get(cursorRowIndex) + cursorColumnIndex, (char) ch);
        size++;
        offsets.set(cursorRowIndex + 1, offsets.get(cursorRowIndex + 1) + 1);
    }

    public void deleteChar(int rowIndex, int columnIndex) {
        var row = storage.get(rowIndex);

        row.remove(columnIndex);
        size--;

        offsets.set(rowIndex + 1, offsets.get(rowIndex) - 1);
        var offset = offsets.get(rowIndex) + columnIndex;
        stringBuilder.delete(offset, offset + 1);

        logger.info(stringBuilder.toString());
    }

    public int deleteCharAtCursorAndGetChar() {
        var row = storage.get(cursorRowIndex);
        if (CommonUtils.getElementOrNull(row, cursorColumnIndex) == null) return AsciiConstant.NULL;

        var ch = row.remove(cursorColumnIndex);

        offsets.set(cursorRowIndex + 1, offsets.get(cursorRowIndex) - 1);
        var offset = offsets.get(cursorRowIndex) + cursorColumnIndex;
        stringBuilder.delete(offset, offset + 1);

        return ch;
    }

    public int addRow() {
        var fromRow = storage.get(cursorRowIndex);
        var modifiedFromRow = fromRow.subList(0, cursorColumnIndex);
        var toRow = new LinkedList<>(fromRow.subList(cursorColumnIndex, fromRow.size()));

        storage.set(cursorRowIndex, (new LinkedList<>(modifiedFromRow)));
        var maxRowIndex = storage.size() - 1;
        if (cursorRowIndex == maxRowIndex) {
            storage.add(toRow);
            offsets.add(CommonUtils.getSum(offsets, 0, rowsCount));
        } else {
            storage.add(cursorRowIndex + 1, toRow);
            offsets.add(cursorRowIndex + 1, CommonUtils.getSum(offsets, 0, cursorRowIndex + 1));
        }

        rowsCount++;
        return storage.indexOf(toRow);
    }

    @Override
    public void joinRows(int firstRowIndex, int secondRowIndex) {

    }

    public void deleteRow(int rowIndex) {
        storage.remove(rowIndex);
    }

    public boolean moveCursorRight() {
        var currentRow = storage.get(cursorRowIndex);
        currentRow.removeIf(m -> m == AsciiConstant.CARRIAGE_RETURN);
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

    public String getStringRepresentation() {
        return stringBuilder.toString();
    }

    public Collection<Integer> getStorageRow(Integer rowIndex) {
        return storage.get(rowIndex);
    }

    public Collection<Collection<Integer>> getStorageRows(Collection<Integer> rowIndexes) {
        var rows = new ArrayList<Collection<Integer>>();
        rowIndexes.forEach(m -> rows.add(storage.get(m)));

        return rows;
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
