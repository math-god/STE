package context.operation.state;

public class StateDataModel {
    private String text;
    private int rowIndex;
    private int columnIndex;

    public StateDataModel(String text, int rowIndex, int columnIndex) {
        this.text = text;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
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
}
