package common;

public enum Action {
    INPUT_PRINTABLE_CHAR,
    BACKSPACE_DELETE,
    DEL_DELETE,
    MOVE_CURSOR_RIGHT,
    MOVE_CURSOR_LEFT,
    MOVE_CURSOR_UP,
    MOVE_CURSOR_DOWN,
    MOVE_CURSOR,
    NEW_ROW,
    INPUT_TAB,

    UNDO,
    DO,
    QUIT,
    INPUT_FILE_NAME,
    BACKSPACE_DELETE_FILE_NAME,
    DEL_DELETE_FILE_NAME,
    MOVE_LEFT_FILE_NAME,
    MOVE_RIGHT_FILE_NAME,
    OPEN_FILE_EXPLORER,
    OPEN_DIR_EXPLORER,
    OPEN_OR_SAVE_FILE,
    NEXT_ITEM,
    PREVIOUS_ITEM,
    DIALOG_ACTIONS,

    NONE
}
