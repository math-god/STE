package common.escape;

public enum EscapeReplaceCode {
    DEL(1000),
    LEFT_ARROW(1001),
    RIGHT_ARROW(1002),
    UP_ARROW(1003),
    DOWN_ARROW(1004),

    NONE(0),

    ;

    private final int replace;

    EscapeReplaceCode(int replace) {
        this.replace = replace;
    }

    public int getReplace() {
        return replace;
    }

    public static EscapeReplaceCode get(int code) {
        for (var item : EscapeReplaceCode.values()) {
            if (item.replace == code) return item;
        }

        throw new IllegalArgumentException("Cant get EscapeReplaceCode element by given code");
    }
}
