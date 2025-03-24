package common.utility;

import java.io.IOException;

public class TerminalIOUtils {
    public static void eraseScreen() {
        System.out.print("\033[2J");
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
        System.out.printf("\033[%d;%dH", rowNumber + 1, 1); // set cursor at start of row
        System.out.print("\033[2K"); // erase line
        System.out.print(rowText);
        System.out.print("\0338"); // restore cursor position
    }

    public static int readKey() throws IOException {
        return System.in.read();
    }
}
