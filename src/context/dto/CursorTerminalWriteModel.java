package context.dto;

import common.OperationType;
import context.ContextType;

public class CursorTerminalWriteModel extends TerminalWriteModel {
    private int cursorRowIndex;
    private int cursorColumnIndex;

    public CursorTerminalWriteModel(int cursorRowIndex, int cursorColumnIndex,
                                    OperationType operationType, ContextType contextType) {
        super(operationType, contextType);
        this.cursorRowIndex = cursorRowIndex;
        this.cursorColumnIndex = cursorColumnIndex;
    }

    public int getCursorRowIndex() {
        return cursorRowIndex;
    }

    public void setCursorRowIndex(int cursorRowIndex) {
        this.cursorRowIndex = cursorRowIndex;
    }

    public int getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public void setCursorColumnIndex(int cursorColumnIndex) {
        this.cursorColumnIndex = cursorColumnIndex;
    }
}
