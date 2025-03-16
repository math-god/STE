package context.dto;

public class ContextCursorInfoModel extends ContextInfoModel {
    private Integer cursorRowIndex;
    private Integer cursorColumnIndex;

    public Integer getCursorRowIndex() {
        return cursorRowIndex;
    }

    public void setCursorRowIndex(Integer cursorRowIndex) {
        this.cursorRowIndex = cursorRowIndex;
    }

    public Integer getCursorColumnIndex() {
        return cursorColumnIndex;
    }

    public void setCursorColumnIndex(Integer cursorColumnIndex) {
        this.cursorColumnIndex = cursorColumnIndex;
    }
}
