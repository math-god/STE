import context.Context;
import context.ContextType;
import context.implementation.Editor;
import input.InputReader;
import output.TerminalWriter;
import common.terminal.Terminal;
import common.terminal.WindowsTerminal;
import context.implementation.State;

import java.io.IOException;
import java.util.HashMap;

import static common.utility.TerminalIOUtils.eraseScreen;
import static common.utility.TerminalIOUtils.setCursorAtStart;

public class Application {
    private static InputReader inputReader;
    private static TerminalWriter terminalWriter;
    private static Terminal terminal;

    public static void main(String[] args) {
        run();
    }

    private static void run() {
        init();

        try {
            while (true) {
                var res = inputReader.read();
                if (!res) break;
                terminalWriter.write();
            }
        } catch (IllegalArgumentException | IOException ex) {
            eraseScreen();
            setCursorAtStart();
            System.out.print(ex.getMessage());
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

        inputReader = new InputReader(contextMap);
        terminalWriter = new TerminalWriter();

        editor.attachObserver(terminalWriter);

        eraseScreen();
        setCursorAtStart();
    }

    private static void exit() {
        terminal.disableRawMode();
    }
}