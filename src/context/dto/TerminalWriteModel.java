package context.dto;

import common.OperationType;
import context.ContextType;

public class TerminalWriteModel {
    private OperationType operationType;
    private ContextType contextType;

    public TerminalWriteModel(OperationType operationType, ContextType contextType) {
        this.operationType = operationType;
        this.contextType = contextType;
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
