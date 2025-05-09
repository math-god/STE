package context.dto;

import common.Operation;

public class TerminalWriteModel {
    private Operation operation;

    public static TerminalWriteModel none() {
        var model = new TerminalWriteModel();
        model.setOperation(Operation.NONE);

        return model;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }
}
