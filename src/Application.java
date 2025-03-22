import context.Context;
import context.ContextType;
import context.concrete.editor.Editor;
import input.TerminalReader;
import output.TerminalWriter;
import common.infrastructure.terminal.Terminal;
import common.infrastructure.terminal.WindowsTerminal;
import state.State;

import java.io.IOException;
import java.util.HashMap;

import static common.utility.TerminalIOUtils.eraseScreen;
import static common.utility.TerminalIOUtils.setCursorAtStart;

public class Application {
    private static TerminalReader terminalReader;
    private static TerminalWriter terminalWriter;
    private static Terminal terminal;

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        init();

        try {
            while (true) {
                var res = terminalReader.read();
                if (!res) break;
                terminalWriter.write();
            }
        } catch (IllegalArgumentException | IOException ex) {
            System.out.println(ex.getMessage());
        }

        exit();
    }

    private static void init() {
        terminal = new WindowsTerminal();
        terminal.enableRawMode();

        var state = new State();
        var contextMap = new HashMap<ContextType, Context>();
        var editor = new Editor(state);
        contextMap.put(ContextType.EDITOR, editor);

        terminalReader = new TerminalReader(contextMap);
        terminalWriter = new TerminalWriter();

        editor.attachObserver(terminalWriter);

        eraseScreen();
        setCursorAtStart();
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}