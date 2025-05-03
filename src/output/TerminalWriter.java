package output;

import common.AsciiConstant;
import context.dto.ContextNotificationModel;
import context.dto.CursorNotificationModel;
import context.dto.TextNotificationModel;
import context.operation.notification.Consumer;
import log.FileLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import static common.utility.TerminalIOUtils.printAll;
import static common.utility.TerminalIOUtils.setCursor;

public class TerminalWriter implements Consumer {
    private final Collection<ContextNotificationModel> notifications = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        notifications.forEach(info -> {
            switch (info.getOperation().getGroup()) {
                case TEXT -> {
                    var actualContextInfo = (TextNotificationModel) info;
                    var text = actualContextInfo.getText();

                    var normalizedText = normalizeText(text);
                    logger.info(normalizedText.replace('\n', '*'));
                    printAll(normalizedText);
                }
                case CURSOR -> {
                    var actualContextInfo = (CursorNotificationModel) info;
                    setCursor(actualContextInfo.getCursorRowIndex(), actualContextInfo.getCursorColumnIndex());
                }
            }
        });

        notifications.clear();
    }

    @Override
    public void setInfo(ContextNotificationModel info) {
        notifications.add(info);
    }

    private String normalizeText(String text) {
        return text.replace((char) AsciiConstant.CARRIAGE_RETURN, (char) AsciiConstant.NEW_LINE);
    }
}
