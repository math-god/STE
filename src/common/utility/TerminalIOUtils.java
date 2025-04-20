package common.utility;

import java.io.IOException;

public class TerminalIOUtils {

    private final static String ERASE_SCREEN = "\033[2J";
    private final static String SAVE_CURSOR_POSITION = "\0337";
    private final static String RESTORE_CURSOR_POSITION = "\0338";
    private final static String SET_CURSOR_INVISIBLE = "\033[?25l";
    private final static String SET_CURSOR_VISIBLE = "\033[?25h";
    private final static String SET_CURSOR_AT_START = "\033[H";

    public static void eraseScreen() {
        System.out.print(ERASE_SCREEN);
    }

    public static void eraseLine() {
        System.out.print("\033[2K");
    }

    public static void setCursorAtStart() {
        System.out.print("\033[H");
    }

    public static void eraseFromRowStartToCursor() {
        System.out.print("\033[1K");
    }

    public static void setCursor(int row, int column) {
        System.out.printf("\033[%d;%dH", row + 1, column + 1);
    }

    public static void printRow(String rowText, int rowNumber) {
        System.out.print("\0337"); // save cursor position
        System.out.print("\033[?25l"); // set cursor invisible
        System.out.printf("\033[%d;%dH", rowNumber + 1, 1); // set cursor at start of row
        System.out.print("\033[2K"); // erase line
        System.out.print(rowText);
        System.out.print("\0338"); // restore cursor position
        System.out.print("\033[?25h"); // set cursor visible
    }

    public static void printAll(String text) {
        System.out.print(SAVE_CURSOR_POSITION + SET_CURSOR_INVISIBLE + SET_CURSOR_AT_START + ERASE_SCREEN + text +
                RESTORE_CURSOR_POSITION + SET_CURSOR_VISIBLE
        );
    }

    public static int readKey() throws IOException {
        return System.in.read();
    }
}
