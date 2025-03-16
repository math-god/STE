package context.dto;

import common.InputAction;

public abstract class ContextInfoModel {
    private InputAction action;

    public InputAction getAction() {
        return action;
    }

    public void setAction(InputAction action) {
        this.action = action;
    }
}
