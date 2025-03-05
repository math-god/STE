import input.TerminalReader;
import output.TerminalWriter;
import terminal.Terminal;
import terminal.WindowsTerminal;

public class Application {
    private static TerminalReader terminalReader;
    private static TerminalWriter terminalWriter;
    private static Terminal terminal;

    public static void main(String[] args) {
        run();
    }

    private static void run() {

    }

    private static void init() {
        terminal = new WindowsTerminal();
        terminal.enableRawMode();


    }

    private static void exit() {
        terminal.disableRawMode();
    }
}