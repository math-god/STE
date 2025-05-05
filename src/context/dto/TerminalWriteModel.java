package context.dto;

import common.PrimitiveOperation;

public class TerminalWriteModel {
    private PrimitiveOperation operation;

    public static TerminalWriteModel none() {
        var model = new TerminalWriteModel();
        model.setOperation(PrimitiveOperation.NONE);

        return model;
    }

    public PrimitiveOperation getOperation() {
        return operation;
    }

    public void setOperation(PrimitiveOperation operation) {
        this.operation = operation;
    }
}
