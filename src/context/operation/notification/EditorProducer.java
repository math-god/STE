package context.operation.notification;

import common.AsciiConstant;
import common.PrimitiveOperation;
import context.dto.CursorNotificationModel;
import context.dto.TextNotificationModel;
import context.dto.RowContentModel;
import context.operation.state.EditorState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EditorProducer implements Producer {

    private final Collection<Consumer> consumers;

    public EditorProducer() {
        this.consumers = new HashSet<>();
    }

    public EditorProducer(Consumer consumer) {
        this.consumers = new HashSet<>();
        consumers.add(consumer);
    }

    @Override
    public void attachConsumer(Consumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void detachConsumer(Consumer consumer) {
        consumers.remove(consumer);
    }

    public void notifyTextChanged(PrimitiveOperation operation, EditorState state) {
        var info = new TextNotificationModel();

        var changedRowsIndexes = state.getChangedStorageRowIndexesWithClearing();
        var rowsContent = new ArrayList<RowContentModel>();

        for (var rowIndex : changedRowsIndexes) {
            rowsContent.add(new RowContentModel(rowIndex, storageRowToString(state.getStorageRow(rowIndex))));
        }

        info.setOperation(operation);
        info.setRowsContent(rowsContent);

        consumers.forEach(m -> m.setInfo(info));
    }

    public void notifyCursorChanged(PrimitiveOperation operation, EditorState state) {
        var info = new CursorNotificationModel();

        info.setOperation(operation);
        info.setCursorColumnIndex(state.getCursorColumnIndex());
        info.setCursorRowIndex(state.getCursorRowIndex());

        consumers.forEach(m -> m.setInfo(info));
    }

    private String storageRowToString(Collection<Integer> row) {
        StringBuilder str = new StringBuilder();
        for (int ch : row) {
            if (ch == AsciiConstant.ENTER) ch = 78;
            str.append((char) ch);
        }

        return str.toString();
    }
}
