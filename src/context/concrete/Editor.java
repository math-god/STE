package context.concrete;

import common.InputAction;
import context.Context;
import context.ContextObserver;
import context.dto.ContextRowInfoModel;
import context.dto.RowContentModel;
import output.TerminalWriter;
import state.State;

import java.util.*;
import java.util.function.Consumer;

public class Editor implements Context {

    private final State state;

    private final Collection<ContextObserver> OBSERVERS;
    private final Map<InputAction, Consumer<Integer>> STATE_MODIFIER_MAP;

    public Editor(State state) {
        this.state = state;
        OBSERVERS = initDefaultObserver();
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

        return initMap;
    }

    private Collection<ContextObserver> initDefaultObserver() {
        return new ArrayList<>(List.of(new TerminalWriter()));
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

    }

    private String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (var ch : row) {
            str.append(ch);
        }

        return str.toString();
    }
}
