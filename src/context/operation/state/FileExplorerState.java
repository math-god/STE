package context.operation.state;

import common.AsciiConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class FileExplorerState implements State {

    private final String DEFAULT_DIR = ".";
    private final String ARROW = "<<<";

    private final Collection<String> storage = new ArrayList<>();

    public void initFileList() {
        var file = new File(DEFAULT_DIR);

        var list = file.list();
        if (list == null) throw new RuntimeException("Directory doesn't exist");
        fillStorage(list);
    }

    private void fillStorage(String[] list) {
        for (var item : list) {
            storage.add(item + "  " + ARROW + (char) AsciiConstant.CARRIAGE_RETURN);
        }
    }

    @Override
    public StateDataModel getData() {
        StringBuilder res = new StringBuilder();
        for (var str : storage) {
            res.append(str);
        }

        return new StateDataModel(res.toString(), 0, 0);
    }
}
