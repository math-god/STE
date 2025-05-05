package common;

public enum PrimitiveOperation {
    ADD_CHAR(Group.TEXT),
    DELETE_CHAR(Group.TEXT),
    ADD_ROW(Group.TEXT),
    DELETE_ROW(Group.TEXT),

    CURSOR_UP(Group.CURSOR),
    CURSOR_DOWN(Group.CURSOR),
    CURSOR_LEFT(Group.CURSOR),
    SET_CURSOR(Group.CURSOR),
    CURSOR_RIGHT(Group.CURSOR),

    CURSOR_AT_START_DROP_CONDITION(Group.NONE),

    NONE(Group.NONE);

    private final Group group;

    PrimitiveOperation(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }


    public enum Group {
        CURSOR,
        TEXT,

        NONE
    }
}
