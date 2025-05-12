package context.operation.state;

import common.CharCode;
import common.OperationType;
import context.ContextType;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import log.FileLogger;
import output.Consumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

public class FileExplorerState {

    private final String DEFAULT_DIR = ".";
    private final String ARROW = "<<<";

    private final int MIN_ITEM_INDEX = 4;

    private int itemIndex = MIN_ITEM_INDEX;

    private final Collection<String> storage = new ArrayList<>();
    private String[] fileList;

    private final Consumer consumer;

    private final Logger logger = FileLogger.getFileLogger(FileExplorerState.class.getName(), "file-explorer-log.txt");

    public FileExplorerState(Consumer consumer) {
        this.consumer = consumer;
    }

    public void updateList() {
        if (fileList == null) {
            var file = new File(DEFAULT_DIR);

            var uncompletedFileList = file.list();
            if (uncompletedFileList == null) throw new RuntimeException("Directory doesn't exist");

            fileList = new String[uncompletedFileList.length + 5];
            fileList[0] = "UP - previous item";
            fileList[1] = "DOWN - next item";
            fileList[2] = "ENTER - select";
            fileList[3] = "-------------------------";
            fileList[4] = "\\..";
            System.arraycopy(uncompletedFileList, 0, fileList, 5, uncompletedFileList.length);

            fillStorage(fileList);
        } else {
            fillStorage(fileList);
        }

        consumer.consume(getData());
    }

    public List<String> openFile() {
        var fileName = fileList[itemIndex];

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            fileList = null;
            return reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void previousItem() {
        if (itemIndex == MIN_ITEM_INDEX) return;

        itemIndex--;

        consumer.consume(getData());
    }

    public void nextItem() {
        if (itemIndex == storage.size() - 1) return;

        itemIndex++;

        consumer.consume(getData());
    }

    private void fillStorage(String[] list) {
        storage.clear();

        for (var i = 0; i < list.length; i++) {
            if (i == itemIndex)
                storage.add(list[itemIndex] + "  " + ARROW + (char) CharCode.CARRIAGE_RETURN);
            else
                storage.add(list[i] + "  " + (char) CharCode.CARRIAGE_RETURN);
        }
    }

    private TerminalWriteModel getData() {
        var result = new StringBuilder();
        storage.forEach(result::append);

        return new TextTerminalWriteModel(result.toString(), OperationType.TEXT, ContextType.FILE_EXPLORER);
    }
}
