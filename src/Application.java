import input.TerminalReader;
import output.TerminalWriter;
import common.infrastructure.terminal.Terminal;
import common.infrastructure.terminal.WindowsTerminal;

import java.io.IOException;

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


    }

    private static void exit() {
        terminal.disableRawMode();
    }
}