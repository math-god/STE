package context.dto;

import common.PrimitiveOperation;

public abstract class ContextNotificationModel {
    private PrimitiveOperation operation;

    public PrimitiveOperation getOperation() {
        return operation;
    }

    public void setOperation(PrimitiveOperation operation) {
        this.operation = operation;
    }
}
