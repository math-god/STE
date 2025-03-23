package input;

import common.escape.EscapeReplaceCode;
import common.escape.EscapeSequenceBuilder;
import context.Context;
import context.ContextType;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static common.utility.TerminalIOUtils.readKey;

public class InputReader {

    private Context currentContext;
    private final Map<ContextType, Context> contextMap;
    private final EscapeSequenceBuilder escapeSequenceBuilder = new EscapeSequenceBuilder();

    public InputReader(Map<ContextType, Context> contextMap) {
        this.contextMap = contextMap;

        // editor is default value
        currentContext = contextMap.get(ContextType.EDITOR);
    }

    public Boolean read() throws IOException {
        var key = readKey();
        if (key == 'q') return false;
        if (escapeSequenceBuilder.isEscapeSequence(key)) {
            Integer replacer;
            do {
                var escapeSequenceChar = readKey();
                escapeSequenceBuilder.add(escapeSequenceChar);
                replacer = escapeSequenceBuilder.getReplaceOrNull();
            } while (Objects.equals(replacer, null));

            key = replacer;
        }

        currentContext.input(key);
        return true;
    }
}