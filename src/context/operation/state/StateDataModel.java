package context.operation.state;

import common.OperationType;
import context.ContextType;

public class StateDataModel {
    private String text;
    private int rowIndex;
    private int columnIndex;
    private OperationType operationType;
    private ContextType contextType;

    public StateDataModel(String text, int rowIndex, int columnIndex, ContextType contextType) {
        this.text = text;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.contextType = contextType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public OperationType getOperation() {
        return operationType;
    }

    public void setOperation(OperationType operationType) {
        this.operationType = operationType;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }
}
