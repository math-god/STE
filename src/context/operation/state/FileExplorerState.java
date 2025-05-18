package context.operation.state;

import common.Action;
import common.CharCode;
import common.OperationType;
import context.ContextType;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import log.FileLogger;
import output.Consumer;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FileExplorerState {

    private final String DEFAULT_DIR = ".";
    private final String ARROW = "<<<";
    private final String[] DIRS_HEADER =
            new String[]{"Save file", "UP - previous item", "DOWN - next item", "ENTER - select", "-------------------------"};
    private final String[] FILES_HEADER =
            new String[]{"Open file", "UP - previous item", "DOWN - next item", "ENTER - select", "-------------------------"};

    private int minItemIndex;
    private int itemIndex;

    private final Collection<String> storage = new ArrayList<>();
    private String[] fileList;
    private Map<String, Long> fileSizeMap;
    private Type type;

    private final Consumer consumer;

    private final Logger logger = FileLogger.getFileLogger(FileExplorerState.class.getName(), "file-explorer-log.txt");

    public FileExplorerState(Consumer consumer) {
        this.consumer = consumer;
    }

    public void updateExplorer(Action action) {
        if (fileList == null) {
            if (action == Action.OPEN_FILE_EXPLORER) {
                type = Type.OPEN;
                minItemIndex = FILES_HEADER.length;
            } else if (action == Action.OPEN_DIR_EXPLORER) {
                type = Type.SAVE;
                minItemIndex = DIRS_HEADER.length;
            } else
                throw new RuntimeException("Can't find suitable explorer for given action");

            var dir = new File(DEFAULT_DIR);
            fileSizeMap = getFileSizeMap(dir);
            itemIndex = minItemIndex;
            switch (type) {
                case OPEN -> {
                    fileList = getAllFiles(dir);
                    var formatStrings = getFormatStrings(fileList, FILES_HEADER.length);
                    fillStorage(formatStrings);
                }
                case SAVE -> {
                    fileList = getDirs(dir);
                    var formatStrings = getFormatStrings(fileList, DIRS_HEADER.length);
                    fillStorage(formatStrings);
                }
            }
        } else {
            switch (type) {
                case SAVE -> {
                    var formatStrings = getFormatStrings(fileList, FILES_HEADER.length);
                    fillStorage(formatStrings);
                }
                case OPEN -> {
                    var formatStrings = getFormatStrings(fileList, DIRS_HEADER.length);
                    fillStorage(formatStrings);
                }
            }
        }

        consumer.consume(getData());
    }

    public List<String> readFile() {
        var fileName = fileList[itemIndex];

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFile(String str) {
        var fileName = fileList[itemIndex];

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void previousItem() {
        if (itemIndex == minItemIndex) return;

        itemIndex--;
    }

    public void nextItem() {
        if (itemIndex == storage.size() - 1) return;

        itemIndex++;
    }

    public void clear() {
        fileList = null;
        type = null;
        fileSizeMap = null;
    }

    private void fillStorage(String[] formatStrings) {
        storage.clear();

        for (var i = 0; i < formatStrings.length; i++) {
            var fileSize = fileSizeMap.get(fileList[i]);
            var fileSizeStr = fileSize != null ? fileSize + " bytes" : "";

            if (i == itemIndex)
                storage.add(String.format(formatStrings[itemIndex], ARROW, fileSizeStr)
                        + (char) CharCode.CARRIAGE_RETURN);
            else
                storage.add(String.format(formatStrings[i], "   ", fileSizeStr)
                        + (char) CharCode.CARRIAGE_RETURN);
        }
    }

    private TerminalWriteModel getData() {
        var result = new StringBuilder();
        storage.forEach(result::append);

        return new TextTerminalWriteModel(result.toString(), OperationType.TEXT, ContextType.FILE_EXPLORER);
    }

    private String[] getAllFiles(File dir) {
        var files = dir.list();
        if (files == null)
            throw new RuntimeException("Directory doesn't exist");

        var filledArr = new String[files.length + FILES_HEADER.length + 1];
        System.arraycopy(FILES_HEADER, 0, filledArr, 0, FILES_HEADER.length);
        filledArr[FILES_HEADER.length] = "\\..";
        System.arraycopy(files, 0, filledArr, FILES_HEADER.length + 1, files.length);

        return filledArr;
    }

    private String[] getDirs(File dir) {
        var dirs = dir.listFiles(File::isDirectory);
        if (dirs == null)
            throw new RuntimeException("Directory doesn't exist");

        var dirsNames = Arrays.stream(dirs)
                .map(File::getName)
                .toArray(String[]::new);

        var filledArr = new String[dirsNames.length + DIRS_HEADER.length + 2];
        System.arraycopy(DIRS_HEADER, 0, filledArr, 0, DIRS_HEADER.length);
        filledArr[DIRS_HEADER.length] = "\\..";
        filledArr[DIRS_HEADER.length + 1] = "\\.";
        System.arraycopy(dirsNames, 0, filledArr, DIRS_HEADER.length + 2, dirsNames.length);

        return filledArr;
    }

    private String[] getFormatStrings(String[] arr, int from) {
        var biggestSizeArr = new String[arr.length - from];
        System.arraycopy(arr, from, biggestSizeArr, 0, arr.length - from);
        var biggestStrSize = Arrays.stream(biggestSizeArr)
                .max(Comparator.comparingInt(String::length))
                .orElseThrow()
                .length();

        var resArr = new String[arr.length];
        System.arraycopy(arr, 0, resArr, 0, arr.length);
        for (var i = from; i < arr.length; i++) {
            var offset = 10 + biggestStrSize - arr[i].length();
            var sizeFormat = "%" + offset + "s";
            resArr[i] = arr[i] + " %s " + sizeFormat;
        }

        return resArr;
    }

    private Map<String, Long> getFileSizeMap(File dir) {
        var dirs = dir.listFiles();
        if (dirs == null)
            throw new RuntimeException("Directory doesn't exist");

        return Arrays.stream(dirs)
                .collect(Collectors.toMap(File::getName, File::length));
    }

    private enum Type {
        OPEN,
        SAVE
    }
}