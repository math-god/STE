package output;

import context.Observer;
import context.dto.ContextCursorNotificationModel;
import context.dto.ContextNotificationModel;
import context.dto.ContextRowNotificationModel;
import log.FileLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import static common.utility.TerminalIOUtils.printRow;
import static common.utility.TerminalIOUtils.setCursor;

public class TerminalWriter implements Observer {
    private final Collection<ContextNotificationModel> notifications = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        notifications.forEach(info -> {
            switch (info.getOperation().getGroup()) {
                case TEXT -> {
                    var actualContextInfo = (ContextRowNotificationModel) info;
                    logger.info(actualContextInfo.getRowsContent().toString());
                    var rowsInfo = actualContextInfo.getRowsContent();
                    for (var rowInfo : rowsInfo) {
                        printRow(rowInfo.getContent(), rowInfo.getRowNumber());
                    }
                }
                case CURSOR -> {
                    var actualContextInfo = (ContextCursorNotificationModel) info;
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
