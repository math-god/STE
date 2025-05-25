package context.operation.state.fileexplorer;

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

    private String path = new File(".").getAbsolutePath().replace("\\.", "");
    private final String ARROW = "<<<";
    private final String PREVIOUS_FOLDER = "\\..";
    private final String CURRENT_FOLDER = "\\.";

    private FileExplorerHeaderBuilder.FileExplorerHeader header;
    private int minItemIndex;
    private int itemIndex;

    private final Collection<String> storage = new ArrayList<>();
    private String[] explorerItems;
    private Map<String, Long> fileSizeMap;
    private Type type;
    private String absolutePath;

    private final Consumer consumer;

    private final Logger logger = FileLogger.getFileLogger(FileExplorerState.class.getName(), "file-explorer-log.txt");

    public FileExplorerState(Consumer consumer) {
        this.consumer = consumer;
    }

    public void updateExplorer(Action action) {
        if (explorerItems == null) {
            startSession(action);

            var dir = new File(path);
            fileSizeMap = getFileSizeMap(dir);
            itemIndex = minItemIndex;
            absolutePath = dir.getAbsolutePath();
            switch (type) {
                case OPEN -> {
                    explorerItems = getAllFiles(dir);
                    var formatStrings = getFormatStrings(explorerItems, header.getSize());
                    fillStorage(formatStrings);
                }
                case SAVE -> {
                    explorerItems = getDirs(dir);
                    var formatStrings = getFormatStrings(explorerItems, header.getSize());
                    fillStorage(formatStrings);
                }
            }
        } else {
            var formatStrings = getFormatStrings(explorerItems, header.getSize());
            fillStorage(formatStrings);
        }

        consumer.consume(getData());
    }

    public List<String> readFileOrGoToDir() {
        var fileName = absolutePath + "\\" + explorerItems[itemIndex];
        var file = new File(fileName);

        if (file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                clear();
                return reader.lines().toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Objects.equals(explorerItems[itemIndex], PREVIOUS_FOLDER)) {
            var lastSlashIndex = path.lastIndexOf("\\");
            path = path.substring(0, lastSlashIndex);
            clear();
            updateExplorer(Action.OPEN_FILE_EXPLORER);
            return null;
        } else {
            path = path + "\\" + explorerItems[itemIndex];
            clear();
            updateExplorer(Action.OPEN_FILE_EXPLORER);
            return null;
        }
    }

    public boolean writeFileOrGoToDir(String str) {
        var fileName = path + "\\" + "text.txt";

        if (Objects.equals(explorerItems[itemIndex], CURRENT_FOLDER)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            clear();
            return true;
        } else if (Objects.equals(explorerItems[itemIndex], PREVIOUS_FOLDER)) {
            var lastSlashIndex = path.lastIndexOf("\\");
            path = path.substring(0, lastSlashIndex);
            clear();
            updateExplorer(Action.OPEN_DIR_EXPLORER);
            return false;
        } else {
            path = path + "\\" + explorerItems[itemIndex];
            clear();
            updateExplorer(Action.OPEN_DIR_EXPLORER);
            return false;
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

    public Type getCurrentExplorerType() {
        return type;
    }

    private void startSession(Action action) {
        if (action == Action.OPEN_FILE_EXPLORER) {
            type = Type.OPEN;
            header = FileExplorerHeaderBuilder.builder()
                    .item("Open file")
                    .item("UP - previous item")
                    .item("DOWN - next item")
                    .item("ENTER - select")
                    .line()
                    .format("Directory: %s", path)
                    .build();
            minItemIndex = header.getSize();
        } else if (action == Action.OPEN_DIR_EXPLORER) {
            type = Type.SAVE;
            header = FileExplorerHeaderBuilder.builder()
                    .item("Save file")
                    .item("UP - previous item")
                    .item("DOWN - next item")
                    .item("ENTER - select")
                    .item("Select " + "\"" + CURRENT_FOLDER + "\"" + " to save")
                    .line()
                    .format("Directory: %s", path)
                    .build();
            minItemIndex = header.getSize();
        } else
            throw new RuntimeException("Can't find suitable explorer for given action");

    }

    private void clear() {
        explorerItems = null;
        type = null;
        fileSizeMap = null;
        absolutePath = null;
    }

    private void fillStorage(String[] formatStrings) {
        storage.clear();

        for (var i = 0; i < formatStrings.length; i++) {
            var fileSize = fileSizeMap.get(explorerItems[i]);
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

        var filledArr = new String[files.length + header.getSize() + 1];
        System.arraycopy(header.getHeaderItems(), 0, filledArr, 0, header.getSize());
        filledArr[header.getSize()] = PREVIOUS_FOLDER;
        System.arraycopy(files, 0, filledArr, header.getSize() + 1, files.length);

        return filledArr;
    }

    private String[] getDirs(File dir) {
        var dirs = dir.listFiles(File::isDirectory);
        if (dirs == null)
            throw new RuntimeException("Directory doesn't exist");

        var dirsNames = Arrays.stream(dirs)
                .map(File::getName)
                .toArray(String[]::new);

        var filledArr = new String[dirsNames.length + header.getSize() + 2];
        System.arraycopy(header.getHeaderItems(), 0, filledArr, 0, header.getSize());
        filledArr[header.getSize()] = PREVIOUS_FOLDER;
        filledArr[header.getSize() + 1] = CURRENT_FOLDER;
        System.arraycopy(dirsNames, 0, filledArr, header.getSize() + 2, dirsNames.length);

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
        var dirs = dir.listFiles(File::isFile);
        if (dirs == null)
            throw new RuntimeException("Directory doesn't exist");

        return Arrays.stream(dirs)
                .collect(Collectors.toMap(File::getName, File::length));
    }

    public enum Type {
        OPEN,
        SAVE
    }
}