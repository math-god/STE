package context.operation.state;

import java.util.ArrayList;
import java.util.Collection;

public class HeaderBuilder {

    private static HeaderBuilder instance;

    private final Collection<String> temp;

    private HeaderBuilder(Collection<String> temp) {
        this.temp = temp;
    }

    public static HeaderBuilder builder() {
        instance = new HeaderBuilder(new ArrayList<>());

        return instance;
    }

    public HeaderBuilder item(String str) {
        temp.add(str);

        return instance;
    }

    public HeaderBuilder line() {
        temp.add("-------------------------\n");

        return instance;
    }

    public HeaderBuilder format(String formatStr, Object... objects) {
        temp.add(formatStr.formatted(objects));

        return instance;
    }

    public Header build() {
        var headerItems = temp.toArray(String[]::new);

        return new Header(headerItems);
    }

    public static class Header {
        private final String[] headerItems;
        private final int size;

        private Header(String[] headerItems) {
            this.headerItems = headerItems;
            this.size = headerItems.length;
        }

        public String[] getHeaderItems() {
            return headerItems;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return String.join("\n", headerItems);
        }
    }
}

