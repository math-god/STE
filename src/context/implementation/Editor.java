package context.implementation;

import common.Action;
import common.PrimitiveOperation;
import common.escape.EscapeReplaceCode;
import common.AsciiConstant;
import common.functional.NullaryModifier;
import common.functional.UnaryModifier;
import context.Context;
import context.ContextObserver;
import context.dto.ContextCursorNotificationModel;
import context.dto.ContextRowNotificationModel;
import context.dto.RowContentModel;
import log.FileLogger;

import java.util.*;
import java.util.logging.Logger;

public class Editor implements Context {

    private final State state;

    private final Collection<ContextObserver> OBSERVERS;
    private final Map<PrimitiveOperation, NullaryModifier> NULLARY_STATE_MODIFIER_MAP;
    private final Map<PrimitiveOperation, UnaryModifier<Integer>> UNARY_STATE_MODIFIER_MAP;
    private final Logger logger = FileLogger.getFileLogger(Editor.class.getName(), "editor-log.txt");

    public Editor(State state) {
        this.state = state;
        OBSERVERS = new HashSet<>();
        NULLARY_STATE_MODIFIER_MAP = initNullaryStateModifiers();
        UNARY_STATE_MODIFIER_MAP = initUnaryStateModifiers();
    }

    @Override
    public void input(Integer ch) {
        var action = getActionByChar(ch);
        var operations = getOperationsByAction(action);
        operations.forEach(operation -> {
            if (operation.getArity() == PrimitiveOperation.Arity.NUllARY) {
                var modifier = NULLARY_STATE_MODIFIER_MAP.get(operation);
                if (modifier == null) {
                    throw new IllegalArgumentException("Editor cant find nullary modifier for: " + ch);
                }

                modifier.modify();
            }
            if (operation.getArity() == PrimitiveOperation.Arity.UNARY) {
                var modifier = UNARY_STATE_MODIFIER_MAP.get(operation);
                if (modifier == null) {
                    throw new IllegalArgumentException("Editor cant find unary modifier for: " + ch);
                }

                modifier.modify(ch);
            }
            if (operation.getArity() == PrimitiveOperation.Arity.NONE) {
                throw new IllegalArgumentException("Editor cant find any modifier for: " + ch);
            }

            if (operation.getGroup() == PrimitiveOperation.Group.TEXT) {
                notifyTextChanged(operation);
            }
            if (operation.getGroup() == PrimitiveOperation.Group.CURSOR) {
                notifyCursorChanged(operation);
            }
        });
    }

    @Override
    public void attachObserver(ContextObserver observer) {
        OBSERVERS.add(observer);
    }

    @Override
    public void detachObserver(ContextObserver observer) {
        OBSERVERS.remove(observer);
    }

    // private

    private Map<PrimitiveOperation, NullaryModifier> initNullaryStateModifiers() {
        var initMap = new HashMap<PrimitiveOperation, NullaryModifier>();

        initMap.put(PrimitiveOperation.CURSOR_RIGHT, state::moveCursorRight);
        initMap.put(PrimitiveOperation.CURSOR_LEFT, state::moveCursorLeft);
        initMap.put(PrimitiveOperation.DELETE_CHAR, state::deleteCharAtCursor);

        return initMap;
    }

    private Map<PrimitiveOperation, UnaryModifier<Integer>> initUnaryStateModifiers() {
        var initMap = new HashMap<PrimitiveOperation, UnaryModifier<Integer>>();

        initMap.put(PrimitiveOperation.ADD_CHAR, state::addChar);

        return initMap;
    }

    private void notifyTextChanged(PrimitiveOperation action) {
        var info = new ContextRowNotificationModel();

        var changedRowsIndexes = state.getChangedStorageRowIndexesWithClearing();
        var rowsContent = new ArrayList<RowContentModel>();

        for (var rowIndex : changedRowsIndexes) {
            rowsContent.add(new RowContentModel(rowIndex, storageRowToString(state.getStorageRow(rowIndex))));
        }

        info.setOperation(action);
        info.setRowsContent(rowsContent);

        OBSERVERS.forEach(m -> m.setInfo(info));
    }

    private void notifyCursorChanged(PrimitiveOperation action) {
        var info = new ContextCursorNotificationModel();

        info.setOperation(action);
        info.setCursorColumnIndex(state.getCursorColumnIndex());
        info.setCursorRowIndex(state.getCursorRowIndex());

        OBSERVERS.forEach(m -> m.setInfo(info));
    }

    private String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (int ch : row) {
            str.append((char) ch);
        }

        return str.toString();
    }

    private Action getActionByChar(Integer ch) {
        if (ch >= AsciiConstant.FIRST_PRINTED_CHAR && ch <= AsciiConstant.LAST_PRINTED_CHAR)
            return Action.INPUT_PRINTABLE_CHAR;
        if (ch == AsciiConstant.BACKSPACE) return Action.BACKSPACE_DELETE;
        if (ch == EscapeReplaceCode.DEL) return Action.DEL_DELETE;
        if (ch == EscapeReplaceCode.RIGHT_ARROW) return Action.MOVE_CURSOR_RIGHT;
        if (ch == EscapeReplaceCode.LEFT_ARROW) return Action.MOVE_CURSOR_LEFT;

        return Action.NONE;
    }

    private Collection<PrimitiveOperation> getOperationsByAction(Action action) {
        switch (action) {
            case INPUT_PRINTABLE_CHAR -> {
                return List.of(PrimitiveOperation.ADD_CHAR, PrimitiveOperation.CURSOR_RIGHT);
            }
            case BACKSPACE_DELETE -> {
                return List.of(PrimitiveOperation.CURSOR_LEFT, PrimitiveOperation.DELETE_CHAR);
            }
            case MOVE_CURSOR_RIGHT -> {
                return List.of(PrimitiveOperation.CURSOR_RIGHT);
            }
            case MOVE_CURSOR_LEFT -> {
                return List.of(PrimitiveOperation.CURSOR_LEFT);
            }
        }

        return List.of(PrimitiveOperation.NONE);
    }
}
