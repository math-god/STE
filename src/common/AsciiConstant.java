package common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AsciiConstant {
    public final static int FIRST_PRINTABLE_CHAR = 32;
    public final static int LAST_PRINTABLE_CHAR = 126;
    public final static int BACKSPACE = 127;
    public final static int CARRIAGE_RETURN = 13;
    public final static int NEW_LINE = 10;
    public final static int TAB = 9;
    public final static int SPACE = 32;
    public final static int NULL = 0;

    public final static Collection<Integer> LIST;

    static {
        var tempList = new ArrayList<Integer>();
        for (var i = FIRST_PRINTABLE_CHAR; i <= LAST_PRINTABLE_CHAR; i++) {
            tempList.add(i);
        }
        tempList.add(BACKSPACE);
        tempList.add(CARRIAGE_RETURN);
        tempList.add(NEW_LINE);
        tempList.add(TAB);
        tempList.add(NULL);

        LIST = tempList.stream().toList();
    }
}
