package context.dto;

public class RowContentModel {
    private Integer rowNumber;
    private String content;

    public RowContentModel(Integer rowNumber, String content) {
        this.rowNumber = rowNumber;
        this.content = content;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", rowNumber, content);
    }
}
