package common;

public enum PrimitiveOperation {
    ADD_CHAR(Group.TEXT, FunctionType.UNARY),
    DELETE_CHAR(Group.TEXT, FunctionType.NUllARY),
    CURSOR_UP(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_DOWN(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_LEFT(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_RIGHT(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_AT_START_DROP_CONDITION(Group.NONE, FunctionType.NULLARY_PREDICATE),

    NONE(Group.NONE, FunctionType.NONE);

    private final Group group;
    private final FunctionType functionType;

    PrimitiveOperation(Group group, FunctionType functionType) {
        this.group = group;
        this.functionType = functionType;
    }

    public Group getGroup() {
        return group;
    }

    public FunctionType getFunctionType() {
        return functionType;
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

    public enum FunctionType {
        NUllARY,
        UNARY,
        NULLARY_PREDICATE,

        NONE
    }
}
