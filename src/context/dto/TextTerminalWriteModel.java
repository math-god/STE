package context.dto;

import common.OperationType;
import context.ContextType;

public class TextTerminalWriteModel extends TerminalWriteModel {
    private String text;

    public TextTerminalWriteModel(String text, OperationType operationType, ContextType contextType) {
        super(operationType, contextType);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
