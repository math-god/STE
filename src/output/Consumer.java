package output;

import context.dto.TerminalWriteModel;

public interface Consumer {
    void consume(TerminalWriteModel model);
}
