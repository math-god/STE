package output;

import context.ContextObserver;
import context.dto.ContextInfoModel;

public class TerminalWriter implements ContextObserver {

    private final StringBuilder content = new StringBuilder();



    public void write() {

    }

    @Override
    public void setInfo(ContextInfoModel info) {

    }
}
