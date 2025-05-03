package context.operation.state;

public interface State {

    void addChar(int ch);

    void deleteChar(int rowIndex, int columnIndex);

    int deleteCharAtCursorAndGetChar();

    int addRow();

    void joinRows(int firstRowIndex, int secondRowIndex);

    void deleteRow(int rowIndex);

    boolean moveCursorRight();

    boolean moveCursorLeft();

    boolean moveCursorUp();

    boolean moveCursorDown();

    String getStringRepresentation();

    Integer getCursorRowIndex();

    Integer getCursorColumnIndex();

    boolean setCursorRowIndex(Integer row);

    boolean setCursorColumnIndex(Integer column);
}
