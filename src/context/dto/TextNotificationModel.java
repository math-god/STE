package context.dto;

public class TextNotificationModel extends ContextNotificationModel {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
