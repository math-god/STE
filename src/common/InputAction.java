package common;

public enum InputAction {
    INPUT_CHAR(Group.TEXT),
    TEXT_NEW_ROW(Group.TEXT),
    DELETE_CHAR(Group.TEXT),
    CURSOR_UP(Group.CURSOR),
    CURSOR_DOWN(Group.CURSOR),
    CURSOR_LEFT(Group.CURSOR),
    CURSOR_RIGHT(Group.CURSOR),
    CURSOR_NEW_ROW(Group.CURSOR),

    ;

    private final Group group;

    InputAction(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public static InputAction getAction(Integer ch) {
        if (ch >= 32 && ch <= 126) {
            return INPUT_CHAR;
        }

        if (ch == 1001) {
            return CURSOR_LEFT;
        }

        if (ch == 1002) {
            return CURSOR_RIGHT;
        }

        if (ch == 1003) {
            return CURSOR_UP;
        }

        if (ch == 1004) {
            return CURSOR_DOWN;
        }

        throw new IllegalArgumentException("Cant figure out InputAction by input char: " + ch);
    }

    public enum Group {
        CURSOR,
        TEXT
    }
}
