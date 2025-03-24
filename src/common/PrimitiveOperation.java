package common;

public enum PrimitiveOperation {
    ADD_CHAR(Group.TEXT, FunctionType.UNARY),
    DELETE_CHAR(Group.TEXT, FunctionType.NUllARY),
    NEW_ROW(Group.TEXT, FunctionType.NUllARY),

    CURSOR_UP(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_DOWN(Group.CURSOR, FunctionType.NUllARY),
    CURSOR_LEFT(Group.CURSOR, FunctionType.NUllARY),
    SET_CURSOR_AT_START_OF_NEXT_ROW(Group.CURSOR, FunctionType.NUllARY),
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
