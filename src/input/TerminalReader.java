package input;

import common.escape.EscapeReplaceCode;
import common.escape.EscapeSequenceBuilder;
import context.Context;
import context.ContextType;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static common.infrastructure.AsciiConstant.*;
import static common.utility.TerminalIOUtils.readKey;

public class TerminalReader {

    private Context currentContext;
    private final Map<ContextType, Context> contextMap;
    private final EscapeSequenceBuilder escapeSequenceBuilder = new EscapeSequenceBuilder();

    public TerminalReader(Map<ContextType, Context> contextMap) {
        this.contextMap = contextMap;

        // editor is default value
        currentContext = contextMap.get(ContextType.EDITOR);
    }

    public Boolean read() throws IOException {
        var key = readKey();
        if (key == 'q') return false;
        if (escapeSequenceBuilder.isEscapeSequence(key)) {
            var replacer = EscapeReplaceCode.NONE;
            do {
                var escapeSequenceChar = readKey();
                escapeSequenceBuilder.add(escapeSequenceChar);
                replacer = escapeSequenceBuilder.getReplace();
            } while (Objects.equals(replacer, EscapeReplaceCode.NONE));

            key = replacer.getReplace();
        }

        currentContext.input(key);
        return true;
    }
}