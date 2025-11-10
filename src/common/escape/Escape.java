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
    public final static String MOVE_CURSOR_UP = "\033[%dA";
    public final static String RESTORE_SCREEN = "\033[?47l";
    public final static String SAVE_SCREEN = "\033[?47h";
    public final static String INVERSE_COLOR = "\033[7m";
    public final static String RESET_COLOR = "\033[0m";
    public final static String SET_FOREGROUND = "\033[38;5;%dm";
}
