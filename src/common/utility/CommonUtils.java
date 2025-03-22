package common.utility;

import java.util.List;

public class CommonUtils {
    public static <T> T getElementOrNull(List<T> list, int index) {
        if (index < list.size() - 1 || index > list.size() - 1) return null;

        return list.get(index);
    }
}
