package common.escape;

public class EscapeSequenceBuilder {

    private final StringBuilder sequence;

    public EscapeSequenceBuilder() {
        sequence = new StringBuilder();
    }

    public void add(int ch) {
        sequence.append((char) ch);
    }

    public Integer getReplaceOrNull() {
        switch (sequence.toString()) {
            case "[D" -> {
                erase();
                return EscapeReplaceCode.LEFT_ARROW;
            }
            case "[C" -> {
                erase();
                return EscapeReplaceCode.RIGHT_ARROW;
            }
            case "[B" -> {
                erase();
                return EscapeReplaceCode.DOWN_ARROW;
            }
            case "[A" -> {
                erase();
                return EscapeReplaceCode.UP_ARROW;
            }
            case "[3~" -> {
                erase();
                return EscapeReplaceCode.DEL;
            }
            default -> {
                return null;
            }
        }
    }

    public boolean isEscapeSequence(int ch) {
        return ch == 27;
    }

    private void erase() {
        sequence.delete(0, sequence.length());
    }
}
