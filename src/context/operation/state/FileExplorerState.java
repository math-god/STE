package context.operation.state;

import common.AsciiConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileExplorerState implements State {

    private final String DEFAULT_DIR = ".";
    private final String ARROW = "<<<";

    private int itemIndex;

    private final Collection<String> storage = new ArrayList<>();
    private String[] fileList;

    @Override
    public StateDataModel getData() {
        StringBuilder res = new StringBuilder();
        for (var str : storage) {
            res.append(str);
        }

        fileList = null;
        return new StateDataModel(res.toString(), 0, 0);
    }

    public void updateList() {
        if (fileList == null) {
            var file = new File(DEFAULT_DIR);

            var uncompletedFileList = file.list();
            if (uncompletedFileList == null) throw new RuntimeException("Directory doesn't exist");

            fileList = new String[uncompletedFileList.length + 1];
            fileList[0] = "\\..";
            System.arraycopy(uncompletedFileList, 0, fileList, 1, uncompletedFileList.length);

            fillStorage(fileList);
        } else {
            fillStorage(fileList);
        }
    }

    public void nextItem() {
        if (itemIndex == storage.size() - 1) return;

        itemIndex++;
    }

    public void previousItem() {
        if (itemIndex == 0) return;

        itemIndex--;
    }

    private void fillStorage(String[] list) {
        storage.clear();

        for (var i = 0; i < list.length; i++) {
            if (i == itemIndex)
                storage.add(list[itemIndex] + "  " + ARROW + (char) AsciiConstant.CARRIAGE_RETURN);
            else
                storage.add(list[i] + "  " + (char) AsciiConstant.CARRIAGE_RETURN);

        }
    }
}
