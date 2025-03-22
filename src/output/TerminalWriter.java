package output;

import context.ContextObserver;
import context.dto.ContextCursorInfoModel;
import context.dto.ContextInfoModel;
import context.dto.ContextRowInfoModel;
import log.FileLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import static common.utility.TerminalIOUtils.printRow;
import static common.utility.TerminalIOUtils.setCursor;

public class TerminalWriter implements ContextObserver {
    private final Collection<ContextInfoModel> contextInfoList = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        contextInfoList.forEach(info -> {
            switch (info.getAction().getGroup()) {
                case TEXT -> {
                    var actualContextInfo = (ContextRowInfoModel) info;
                    var rowsInfo = actualContextInfo.getRowsContent();
                    for (var rowInfo : rowsInfo) {
                        printRow(rowInfo.getContent(), rowInfo.getRowNumber());
                    }
                }
                case CURSOR -> {
                    var actualContextInfo = (ContextCursorInfoModel) info;
                    setCursor(actualContextInfo.getCursorRowIndex(), actualContextInfo.getCursorColumnIndex());
                }
            }
        });

        contextInfoList.clear();
    }

    @Override
    public void setInfo(ContextInfoModel info) {
        contextInfoList.add(info);
    }
}
