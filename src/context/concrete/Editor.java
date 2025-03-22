package context.concrete;

import common.InputAction;
import context.Context;
import context.ContextObserver;
import context.dto.ContextCursorInfoModel;
import context.dto.ContextRowInfoModel;
import context.dto.RowContentModel;
import log.FileLogger;
import state.State;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Editor implements Context {

    private final State state;

    private final Collection<ContextObserver> OBSERVERS;
    private final Map<InputAction, Consumer<Integer>> STATE_MODIFIER_MAP;
    private final Logger logger = FileLogger.getFileLogger(Editor.class.getName(), "editor-log.txt");

    public Editor(State state) {
        this.state = state;
        OBSERVERS = new HashSet<>();
        STATE_MODIFIER_MAP = initStateModifiers();
    }

    @Override
    public void input(Integer ch) {
        var inputAction = InputAction.getAction(ch);
        var modifier = STATE_MODIFIER_MAP.get(inputAction);
        if (modifier == null) {
            throw new IllegalArgumentException("Editor cant process InputAction taken from char: " + ch);
        }
        modifier.accept(ch);

        if (inputAction.getGroup() == InputAction.Group.TEXT) {
            notifyTextChanged(inputAction);
        }
        if (inputAction.getGroup() == InputAction.Group.CURSOR) {
            notifyCursorChanged(inputAction);
        }
    }

    @Override
    public void attachObserver(ContextObserver observer) {
        OBSERVERS.add(observer);
    }

    @Override
    public void detachObserver(ContextObserver observer) {
        OBSERVERS.remove(observer);
    }

    private Map<InputAction, Consumer<Integer>> initStateModifiers() {
        var initMap = new HashMap<InputAction, Consumer<Integer>>();

        initMap.put(InputAction.INPUT_CHAR, state::addChar);
        initMap.put(InputAction.CURSOR_RIGHT, state::moveCursor);

        return initMap;
    }

    private void notifyTextChanged(InputAction action) {
        var info = new ContextRowInfoModel();

        var changedRowsIndexes = state.getChangedStorageRowsWithClearing();
        var rowsContent = new ArrayList<RowContentModel>();

        for (var rowIndex : changedRowsIndexes) {
            rowsContent.add(new RowContentModel(rowIndex, storageRowToString(state.getStorageRow(rowIndex))));
        }

        info.setAction(action);
        info.setRowsContent(rowsContent);

        OBSERVERS.forEach(m -> m.setInfo(info));
    }

    private void notifyCursorChanged(InputAction action) {
        var info = new ContextCursorInfoModel();

        info.setAction(action);
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
}
