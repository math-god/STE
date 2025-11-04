package common.terminal;

public interface Terminal {

    void enableRawMode();

    void disableRawMode();

    WindowSize getWindowSize();

    record WindowSize(int rows, int columns) {}

    CursorPosition getCursorPosition();

    record CursorPosition(int x, int y) {}
}
