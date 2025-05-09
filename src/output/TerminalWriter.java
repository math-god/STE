package output;

import common.AsciiConstant;
import context.dto.CursorTerminalWriteModel;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import log.FileLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import static common.utility.TerminalIOUtils.printAll;
import static common.utility.TerminalIOUtils.setCursor;

public class TerminalWriter implements Consumer {
    private final Collection<TerminalWriteModel> models = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        models.forEach(info -> {
            switch (info.getOperation()) {
                case TEXT -> {
                    var actualContextInfo = (TextTerminalWriteModel) info;
                    var text = actualContextInfo.getText();

                    var normalizedText = normalizeText(text);
                    logger.info(normalizedText.replace('\n', '*'));
                    printAll(normalizedText);
                }
                case CURSOR -> {
                    var actualContextInfo = (CursorTerminalWriteModel) info;
                    setCursor(actualContextInfo.getCursorRowIndex(), actualContextInfo.getCursorColumnIndex());
                }
            }
        });

        models.clear();
    }

    @Override
    public void consume(TerminalWriteModel info) {
        models.add(info);
    }

    private String normalizeText(String text) {
        return text.replace((char) AsciiConstant.CARRIAGE_RETURN, (char) AsciiConstant.NEW_LINE);
    }
}
