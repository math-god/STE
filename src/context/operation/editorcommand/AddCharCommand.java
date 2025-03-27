package context.operation.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.Observable;
import context.Observer;
import context.dto.ContextRowNotificationModel;
import context.dto.RowContentModel;
import context.operation.UndoCommand;
import input.InputReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AddCharCommand implements UndoCommand, Observable {

    private int rowIndex;
    private int columnIndex;
    private final EditorState editorState;
    private final Collection<Observer> observers;

    public AddCharCommand(EditorState editorState) {
        observers = new HashSet<>();
        this.editorState = editorState;
    }

    @Override
    public void execute() {
        editorState.addChar(InputReader.getInputChar());
        rowIndex = editorState.getCursorRowIndex();
        columnIndex = editorState.getCursorColumnIndex();

        notifyTextChanged();
    }

    @Override
    public void unexecute() {
        editorState.deleteChar(rowIndex, columnIndex);
    }

    @Override
    public void attachObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void detachObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyTextChanged() {
        var info = new ContextRowNotificationModel();

        var changedRowsIndexes = editorState.getChangedStorageRowIndexesWithClearing();
        var rowsContent = new ArrayList<RowContentModel>();

        for (var rowIndex : changedRowsIndexes) {
            rowsContent.add(new RowContentModel(rowIndex, storageRowToString(editorState.getStorageRow(rowIndex))));
        }

        info.setOperation(PrimitiveOperation.ADD_CHAR);
        info.setRowsContent(rowsContent);

        observers.forEach(m -> m.setInfo(info));
    }

    private String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (int ch : row) {
            if (ch == AsciiConstant.ENTER) continue;
            str.append((char) ch);
        }

        return str.toString();
    }
}
