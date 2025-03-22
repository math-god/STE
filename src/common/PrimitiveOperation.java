package common;

public enum PrimitiveOperation {
    ADD_CHAR(Group.TEXT, Arity.UNARY),
    DELETE_CHAR(Group.TEXT, Arity.NUllARY),
    CURSOR_UP(Group.CURSOR, Arity.NUllARY),
    CURSOR_DOWN(Group.CURSOR, Arity.NUllARY),
    CURSOR_LEFT(Group.CURSOR, Arity.NUllARY),
    CURSOR_RIGHT(Group.CURSOR, Arity.NUllARY),

    NONE(Group.NONE, Arity.NONE);

    private final Group group;
    private final Arity arity;

    PrimitiveOperation(Group group, Arity arity) {
        this.group = group;
        this.arity = arity;
    }

    public Group getGroup() {
        return group;
    }

    public Arity getArity() {
        return arity;
    }

    public static PrimitiveOperation getOperation(Integer ch) {
        if (ch >= 32 && ch <= 126) {
            return ADD_CHAR;
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
        TEXT,

        NONE
    }

    public enum Arity {
        NUllARY,
        UNARY,

        NONE
    }
}
