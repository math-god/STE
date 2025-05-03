package common.utility;

import java.io.IOException;

import static common.escape.Escape.*;

public class TerminalIOUtils {

    public static void eraseScreen() {
        System.out.print(ERASE_SCREEN);
    }

    public static void eraseLine() {
        System.out.print(ERASE_LINE);
    }

    public static void setCursorAtStart() {
        System.out.print(SET_CURSOR_AT_START);
    }

    public static void setCursor(int row, int column) {
        System.out.printf(SET_CURSOR_AT_ROW_COLUMN, row + 1, column + 1);
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
