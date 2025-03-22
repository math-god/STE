package context.dto;

public class ContextCursorNotificationModel extends ContextNotificationModel {
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
