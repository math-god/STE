package context.operation.editorcommand;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.operation.notification.Producer;
import context.operation.notification.Consumer;
import context.dto.ContextRowNotificationModel;
import context.dto.RowContentModel;
import context.operation.command.UndoCommand;
import input.InputReader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class AddCharCommand extends Producer implements UndoCommand {

    private int rowIndex;
    private int columnIndex;
    private final EditorState editorState;

    public AddCharCommand(EditorState editorState) {
        super();
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

    private void notifyTextChanged() {
        var info = new ContextRowNotificationModel();

        var changedRowsIndexes = editorState.getChangedStorageRowIndexesWithClearing();
        var rowsContent = new ArrayList<RowContentModel>();

        for (var rowIndex : changedRowsIndexes) {
            rowsContent.add(new RowContentModel(rowIndex, storageRowToString(editorState.getStorageRow(rowIndex))));
        }

        info.setOperation(PrimitiveOperation.ADD_CHAR);
        info.setRowsContent(rowsContent);

        consumers.forEach(m -> m.setInfo(info));
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
