package common.terminal;

public enum Platform {
    WINDOWS("\r\n"),
    MAC("\n"),
    LINUX("\n")

    ;

    public final String NEXT_ROW_CHAR;

    Platform(String nextRowChar) {
        NEXT_ROW_CHAR = nextRowChar;
    }
}
