package output;

import context.operation.notification.Consumer;
import context.dto.CursorNotificationModel;
import context.dto.ContextNotificationModel;
import context.dto.TextNotificationModel;
import log.FileLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static common.utility.TerminalIOUtils.*;

public class TerminalWriter implements Consumer {
    private final Collection<ContextNotificationModel> notifications = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        notifications.forEach(info -> {
            switch (info.getOperation().getGroup()) {
                case TEXT -> {
                    var actualContextInfo = (TextNotificationModel) info;
                    logger.info(actualContextInfo.getRowsContent().toString());
                    var rowsInfo = actualContextInfo.getRowsContent();
                    for (var rowInfo : rowsInfo) {
                        printAll(rowInfo.getContent());
                    }
                }
                case CURSOR -> {
                    var actualContextInfo = (CursorNotificationModel) info;
                    logger.info("row: " + actualContextInfo.getCursorRowIndex() + " column " + actualContextInfo.getCursorColumnIndex());
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
}
