package output;

import common.AsciiConstant;
import context.ContextType;
import context.dto.CursorTerminalWriteModel;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import input.InputReader;
import log.FileLogger;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import static common.escape.Escape.*;
import static common.escape.Escape.SET_CURSOR_VISIBLE;

public class TerminalWriter implements Consumer {
    private final Collection<TerminalWriteModel> models = new LinkedList<>();
    private final Logger logger = FileLogger.getFileLogger(TerminalWriter.class.getName(), "terminal-writer-log.txt");

    public void write() {
        models.forEach(info -> {
            switch (info.getOperation()) {
                case TEXT -> {
                    var contextInfo = (TextTerminalWriteModel) info;
                    var text = contextInfo.getText();

                    var normalizedText = normalizeText(text);
                    logger.info(normalizedText.replace('\n', '*'));
                    printAll(normalizedText, InputReader.getCurrentContext());
                }
                case CURSOR -> {
                    var contextInfo = (CursorTerminalWriteModel) info;
                    System.out.printf(SET_CURSOR_AT_ROW_COLUMN,
                            contextInfo.getCursorRowIndex() + 1, contextInfo.getCursorColumnIndex() + 1);
                }
            }
        });

        models.clear();
    }

    @Override
    public void consume(TerminalWriteModel info) {
        models.add(info);
    }

    private String normalizeText(String text) {
        return text.replace((char) AsciiConstant.CARRIAGE_RETURN, (char) AsciiConstant.NEW_LINE);
    }

    private void printAll(String text, ContextType context) {
        switch (context) {
            case EDITOR ->
                    System.out.print(SAVE_CURSOR_POSITION + SET_CURSOR_INVISIBLE + SET_CURSOR_AT_START + ERASE_SCREEN + text +
                            RESTORE_CURSOR_POSITION + SET_CURSOR_VISIBLE
                    );
            case FILE_EXPLORER -> System.out.print(SET_CURSOR_AT_START + SET_CURSOR_INVISIBLE + ERASE_SCREEN + text);
        }

    }
}
