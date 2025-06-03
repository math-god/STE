package context.operation.state.dialog;

import common.CharCode;
import common.OperationType;
import context.ContextType;
import context.dto.TerminalWriteModel;
import context.dto.TextTerminalWriteModel;
import context.operation.state.HeaderBuilder;
import output.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DialogState {

    private final String ARROW = "<<<";
    private int minItemIndex;
    private int itemIndex;
    private final Collection<String> storage = new ArrayList<>();
    private String[] dialogItems;
    private DialogType type;
    private final Consumer consumer;

    public DialogState(Consumer consumer) {
        this.consumer = consumer;
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

        consumer.consume(getData());
    }

    private TerminalWriteModel getData() {
        var result = new StringBuilder();
        storage.forEach(result::append);

        return new TextTerminalWriteModel(result.toString(), OperationType.TEXT, ContextType.DIALOG);
    }

    public enum DialogAnswer {
        YES("Yes"),
        NO("No"),

        ;

        private final String answer;

        DialogAnswer(String answer) {
            this.answer = answer;
        }

        static DialogAnswer[] getAnswers() {
            return Arrays.stream(DialogAnswer.values())
                    .toArray(DialogAnswer[]::new);
        }
    }

    public enum DialogType {
        SAVE_BEFORE_OPEN("Do you want to save current file before opening?", DialogAnswer.getAnswers()),

        ;

        final String question;
        final DialogAnswer[] answers;

        DialogType(String question, DialogAnswer[] answers) {
            this.question = question;
            this.answers = answers;
        }

    }
}