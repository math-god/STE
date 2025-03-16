package context.dto;

import java.util.Collection;

public class ContextRowInfoModel extends ContextInfoModel {
    private Collection<RowContentModel> rowsContent;

    public Collection<RowContentModel> getRowsContent() {
        return rowsContent;
    }

    public void setRowsContent(Collection<RowContentModel> rowsContent) {
        this.rowsContent = rowsContent;
    }
}
