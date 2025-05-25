package context.operation.state.fileexplorer;

import java.util.ArrayList;
import java.util.Collection;

public class FileExplorerHeaderBuilder {

    private static FileExplorerHeaderBuilder instance;

    private final Collection<String> temp;

    private FileExplorerHeaderBuilder(Collection<String> temp) {
        this.temp = temp;
    }

    public static FileExplorerHeaderBuilder builder() {
        instance = new FileExplorerHeaderBuilder(new ArrayList<>());

        return instance;
    }

    public FileExplorerHeaderBuilder item(String str) {
        temp.add(str);

        return instance;
    }

    public FileExplorerHeaderBuilder line() {
        temp.add("-------------------------");

        return instance;
    }

    public FileExplorerHeaderBuilder format(String formatStr, Object... objects) {
        temp.add(formatStr.formatted(objects));

        return instance;
    }

    public FileExplorerHeader build() {
        var headerItems = temp.toArray(String[]::new);

        return new FileExplorerHeader(headerItems);
    }

    public static class FileExplorerHeader {
        private final String[] headerItems;
        private final int size;

        private FileExplorerHeader(String[] headerItems) {
            this.headerItems = headerItems;
            this.size = headerItems.length;
        }

        public String[] getHeaderItems() {
            return headerItems;
        }

        public int getSize() {
            return size;
        }
    }
}

