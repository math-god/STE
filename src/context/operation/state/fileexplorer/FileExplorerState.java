package context.operation.state.fileexplorer;

import common.Action;
import common.CharCode;
import common.terminal.Platform;
import context.operation.state.HeaderBuilder;
import context.operation.state.TerminalWriter;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static app.Application.PLATFORM;
import static common.escape.Escape.*;

public class FileExplorerState {

    private String path = new File(".").getAbsolutePath().replace("\\.", "");

    private String openedFilePath;
    private boolean saved;
    private final String PREVIOUS_FOLDER = "\\..";
    private final String CURRENT_FOLDER = "\\.";

    private HeaderBuilder.Header header;
    private int minItemIndex;
    private int itemIndex;

    private int inputColumnIndex;
    private int minInputColumnIndex;
    private StringBuilder fileName;
    private static final int MAX_FILE_NAME_SIZE = 256;

    private final Collection<String> storage = new ArrayList<>();
    private String[] explorerItems;
    private Type type;

    private String outputString;

    private final TerminalWriter terminalWriter;

    public FileExplorerState(TerminalWriter terminalWriter) {
        this.terminalWriter = terminalWriter;
    }

    public void startExplorer(Action action) {
        initFields(action);

        var formatStrings = getFormatStrings();
        fillStorage(formatStrings);

        writeTerminal();
    }

    public List<String> readFileOrGoToDir() {
        var fileName = path + "\\" + explorerItems[itemIndex];
        var file = new File(fileName);

        if (file.isFile()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                openedFilePath = fileName;
                saved = true;
                return reader.lines().toList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Objects.equals(explorerItems[itemIndex], PREVIOUS_FOLDER)) {
            var lastSlashIndex = path.lastIndexOf("\\");
            path = path.substring(0, lastSlashIndex);
        } else {
            path = path + "\\" + explorerItems[itemIndex];
        }

        resetItemIndex();
        continueExplorer();
        return null;
    }

    public boolean writeFileOrGoToDir(String str) {
        var filePath = path + "\\" + fileName;

        if (Objects.equals(explorerItems[itemIndex], CURRENT_FOLDER)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            openedFilePath = filePath;
            saved = true;
            return true;
        } else if (Objects.equals(explorerItems[itemIndex], PREVIOUS_FOLDER)) {
            var lastSlashIndex = path.lastIndexOf("\\");
            path = path.substring(0, lastSlashIndex);
        } else {
            path = path + "\\" + explorerItems[itemIndex];
        }

        resetItemIndex();
        continueExplorer();
        return false;
    }

    public void writeFile(String str) {
        var fileName = openedFilePath;
        if (fileName == null)
            throw new NullPointerException("File name is null");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(str);
            openedFilePath = fileName;
            saved = true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void inputFileName(int ch) {
        if (type == Type.OPEN)
            return;

        if (inputColumnIndex == MAX_FILE_NAME_SIZE)
            return;

        fileName.insert(inputColumnIndex - minInputColumnIndex, (char) ch);
        inputColumnIndex++;
        continueExplorer();
    }

    public void deleteFileNameCharOnCursor() {
        if (inputColumnIndex - minInputColumnIndex > fileName.length() - 1)
            return;

        fileName.deleteCharAt(inputColumnIndex - minInputColumnIndex);
        continueExplorer();
    }

    public void previousItem() {
        if (itemIndex == minItemIndex) return;

        itemIndex--;
        continueExplorer();
    }

    public void nextItem() {
        if (itemIndex == storage.size() - 1) return;

        itemIndex++;
        continueExplorer();
    }

    public void moveCursorLeft() {
        if (inputColumnIndex == minInputColumnIndex)
            return;

        inputColumnIndex--;
        continueExplorer();
    }

    public void moveCursorRight() {
        if (inputColumnIndex - minInputColumnIndex == fileName.length())
            return;

        inputColumnIndex++;
        continueExplorer();
    }

    public String getFileName() {
        if (explorerItems != null) {
            return explorerItems[itemIndex];
        }

        return null;
    }

    public Type getCurrentExplorerType() {
        return type;
    }

    public String getOpenedFilePath() {
        return openedFilePath;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setUnsaved() {
        saved = false;
    }

    private void resetItemIndex() {
        itemIndex = minItemIndex;
    }

    private void initFields(Action action) {
        if (action == Action.OPEN_FILE_EXPLORER) {
            type = Type.OPEN;

            header = HeaderBuilder.builder()
                    .item("Open file")
                    .item("UP - previous item")
                    .item("DOWN - next item")
                    .item("ENTER - select")
                    .line()
                    .format("Directory: %s", path)
                    .build();
            minItemIndex = header.getSize();
            outputString = SET_CURSOR_AT_START + SET_CURSOR_INVISIBLE + ERASE_IN_DISPLAY + "%s";

            explorerItems = getAllFiles();
        } else if (action == Action.OPEN_DIR_EXPLORER) {
            type = Type.SAVE;

            var fileNameItem = "File name: %s";
            fileName = new StringBuilder();
            header = HeaderBuilder.builder()
                    .item("Save file")
                    .item("UP - previous item")
                    .item("DOWN - next item")
                    .item("ENTER - select")
                    .item("Select " + "\"" + CURRENT_FOLDER + "\"" + " to save")
                    .line()
                    .format("Directory: %s", path)
                    .format(fileNameItem, fileName)
                    .build();
            minItemIndex = header.getSize();
            outputString = SET_CURSOR_AT_START + SET_CURSOR_INVISIBLE + ERASE_IN_DISPLAY + "%s" + SET_CURSOR_VISIBLE;
            inputColumnIndex = 11;
            minInputColumnIndex = 11;

            explorerItems = getDirs();
        } else
            throw new RuntimeException("Can't find suitable explorer for given action");

        resetItemIndex();
    }

    private void updateSaveExplorerHeader() {
        var fileNameItem = "File name: %s";

        header = HeaderBuilder.builder()
                .item("Save file")
                .item("UP - previous item")
                .item("DOWN - next item")
                .item("ENTER - select")
                .item("Select " + "\"" + CURRENT_FOLDER + "\"" + " to save")
                .line()
                .format("Directory: %s", path)
                .format(fileNameItem, fileName)
                .build();
    }

    private void continueExplorer() {
        switch (type) {
            case OPEN -> explorerItems = getAllFiles();
            case SAVE -> {
                updateSaveExplorerHeader();
                explorerItems = getDirs();
            }
        }

        var formatStrings = getFormatStrings();
        fillStorage(formatStrings);

        writeTerminal();
    }

    private void writeTerminal() {
        if (type == Type.OPEN) {
            terminalWriter.writeText(outputString, getTextData());
        } else if (type == Type.SAVE) {
            terminalWriter.writeText(outputString, getTextData());

            var fileNameItemIndex = header.getSize() - 1;
            terminalWriter.writeCursor(fileNameItemIndex, inputColumnIndex);
        }
    }

    private void fillStorage(String[] formatStrings) {
        storage.clear();

        var fileSizeMap = getFileSizeMap();
        for (var i = 0; i < formatStrings.length; i++) {
            var fileSize = fileSizeMap.get(explorerItems[i]);
            var fileSizeStr = fileSize != null ? fileSize + " bytes" : "";

            String ARROW = "<<<";
            if (i == itemIndex)
                storage.add(String.format(formatStrings[itemIndex], ARROW, fileSizeStr)
                        + (char) CharCode.CARRIAGE_RETURN);
            else
                storage.add(String.format(formatStrings[i], "   ", fileSizeStr)
                        + (char) CharCode.CARRIAGE_RETURN);
        }
    }

    private String getTextData() {
        var stringBuilder = new StringBuilder();
        storage.forEach(stringBuilder::append);

        var result = stringBuilder.toString();
        if (PLATFORM == Platform.WINDOWS)
            result = result.replace("\r", "\r\n");

        return result;
    }

    private String[] getAllFiles() {
        var files = new File(path).list();
        if (files == null)
            throw new RuntimeException("Directory doesn't exist");

        var filledArr = new String[files.length + header.getSize() + 1];
        System.arraycopy(header.getHeaderItems(), 0, filledArr, 0, header.getSize());
        filledArr[header.getSize()] = PREVIOUS_FOLDER;
        System.arraycopy(files, 0, filledArr, header.getSize() + 1, files.length);

        return filledArr;
    }

    private String[] getDirs() {
        var dirs = new File(path).listFiles(File::isDirectory);
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

    private String[] getFormatStrings() {
        var from = header.getSize();

        var biggestSizeArr = new String[explorerItems.length - from];
        System.arraycopy(explorerItems, from, biggestSizeArr, 0, explorerItems.length - from);
        var biggestStrSize = Arrays.stream(biggestSizeArr)
                .max(Comparator.comparingInt(String::length))
                .orElseThrow()
                .length();

        var resArr = new String[explorerItems.length];
        System.arraycopy(explorerItems, 0, resArr, 0, explorerItems.length);
        for (var i = from; i < explorerItems.length; i++) {
            var offset = 10 + biggestStrSize - explorerItems[i].length();
            var sizeFormat = "%" + offset + "s";
            resArr[i] = explorerItems[i] + " %s " + sizeFormat;
        }

        return resArr;
    }

    private Map<String, Long> getFileSizeMap() {
        var dirs = new File(path).listFiles(File::isFile);
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