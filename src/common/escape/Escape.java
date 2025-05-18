package common.escape;

public class Escape {
    public final static String ERASE_SCREEN = "\033[2J";
    public final static String ERASE_IN_DISPLAY = "\033[J";
    public final static String ERASE_LINE = "\033[2K";
    public final static String SAVE_CURSOR_POSITION = "\0337";
    public final static String RESTORE_CURSOR_POSITION = "\0338";
    public final static String SET_CURSOR_INVISIBLE = "\033[?25l";
    public final static String SET_CURSOR_VISIBLE = "\033[?25h";
    public final static String SET_CURSOR_AT_START = "\033[H";
    public final static String SET_CURSOR_AT_ROW_COLUMN = "\033[%d;%dH";
}
