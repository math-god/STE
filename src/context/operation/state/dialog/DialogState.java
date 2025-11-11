package context.operation.state.dialog;

import common.CharCode;
import common.terminal.Platform;
import context.operation.state.HeaderBuilder;
import context.operation.state.TerminalWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static app.Application.PLATFORM;
import static common.escape.Escape.*;

public class DialogState {

    private final String ARROW = "<<<";
    private int minItemIndex;
    private int itemIndex;
    private final Collection<String> storage = new ArrayList<>();
    private String[] dialogItems;
    private DialogType type;
    private final String OUTPUT_STRING = SET_CURSOR_AT_START + SET_CURSOR_INVISIBLE + ERASE_IN_DISPLAY + "%s";

    private final TerminalWriter terminalWriter;

    public DialogState(TerminalWriter terminalWriter) {
        this.terminalWriter = terminalWriter;
    }

    public void startDialog(DialogType type) {
        var header = HeaderBuilder.builder()
                .item(type.question)
                .line()
                .build();

        minItemIndex = header.getSize();
        itemIndex = minItemIndex;
        this.type = type;

        dialogItems = new String[header.getSize() + type.answers.length];
        System.arraycopy(header.getHeaderItems(), 0, dialogItems, 0, header.getSize());

        var answers = Arrays.stream(type.answers)
                .map(m -> m.answer)
                .toArray(String[]::new);
        System.arraycopy(answers, 0, dialogItems, header.getSize(), type.answers.length);

        fillStorage();
    }

    public void continueDialog() {
        fillStorage();
    }

    public DialogAnswer finishDialog() {
        return type.answers[itemIndex - minItemIndex];
    }

    public void previousItem() {
        if (itemIndex == minItemIndex) return;

        itemIndex--;
    }

    public void nextItem() {
        if (itemIndex == storage.size() - 1) return;

        itemIndex++;
    }

    public DialogType getType() {
        return type;
    }

    private void fillStorage() {
        storage.clear();

        for (var i = 0; i < dialogItems.length; i++) {
            if (i == itemIndex)
                storage.add(String.format(dialogItems[itemIndex] + " " + ARROW)
                        + (char) CharCode.CARRIAGE_RETURN);
            else
                storage.add(String.format(dialogItems[i]) + (char) CharCode.CARRIAGE_RETURN);
        }

        terminalWriter.writeText(OUTPUT_STRING, getData());
    }

    private String getData() {
        var stringBuilder = new StringBuilder();
        storage.forEach(stringBuilder::append);

        var result = stringBuilder.toString();
        if (PLATFORM == Platform.WINDOWS)
            result = result.replace("\r", "\r\n");

        return result;
    }

    public enum DialogAnswer {
        YES("Yes"),
        NO("No"),
        EXIT(String.format(SET_FOREGROUND, 27) + "Press Enter to exit" + RESET_COLOR)

        ;

        private final String answer;

        DialogAnswer(String answer) {
            this.answer = answer;
        }
    }

    public enum DialogType {
        SAVE_BEFORE_OPEN("Do you want to save current file before opening?", new DialogAnswer[]{DialogAnswer.YES, DialogAnswer.NO}),
        HELP_PAGE("""
                Ctrl + Q: exit
                Ctrl + Z: undo
                Ctrl + X: do
                Ctrl + O: open file
                Ctrl + S: save file
                """, new DialogAnswer[]{DialogAnswer.EXIT});

        final String question;
        final DialogAnswer[] answers;

        DialogType(String question, DialogAnswer[] answers) {
            this.question = question;
            this.answers = answers;
        }

    }
}