package state;

import java.util.LinkedList;

public class State {

    private Integer cursorRowNumber;
    private Integer cursorColumnNumber;

    private final LinkedList<LinkedList<Integer>> storage;

    private static State state;

    {
        storage = new LinkedList<>();
        storage.add(new LinkedList<>());
    }

    private State() {
    }

    public static State initializeOrGet() {
        if (state == null) {
            state = new State();
        }

        return state;
    }

    public Integer getCursorRowNumber() {
        return cursorRowNumber;
    }

    public Integer getCursorColumnNumber() {
        return cursorColumnNumber;
    }

    public void setCursorRowNumber(Integer row) {
        if (row < 0) throw new IllegalArgumentException("Bad state: cursor row cant be less than 0");
        this.cursorRowNumber = row;
    }

    public void setCursorColumnNumber(Integer column) {
        if (column < 0) throw new IllegalArgumentException("Bad state: cursor column cant be less than 0");
        this.cursorColumnNumber = column;
    }


}
