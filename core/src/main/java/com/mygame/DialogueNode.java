package com.mygame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogueNode {

    public static class Choice {
        public final String text;
        public final DialogueNode nextNode;
        public final Runnable action;

        public Choice(String text, DialogueNode nextNode) {
            this(text, nextNode, null);
        }

        public Choice(String text, Runnable action) {
            this(text, null, action);
        }

        public Choice(String text, DialogueNode nextNode, Runnable action) {
            this.text = text;
            this.nextNode = nextNode;
            this.action = action;
        }
    }

    private final List<String> texts;
    private final List<Choice> choices;
    private final Runnable action; // Action for nodes without choices


    private DialogueNode(Runnable action, List<String> texts) {
        this.texts = (texts == null || texts.isEmpty()) ? List.of("") : texts;
        this.choices = new ArrayList<>();
        this.action = action;
    }

    public DialogueNode(String... texts) {
        this(null, Arrays.asList(texts));
    }

    // For nodes with multiple texts and a final action
    public DialogueNode(Runnable action, String... texts) {
        this(action, Arrays.asList(texts));
    }

    public void addChoice(String choiceText, DialogueNode nextNode) {
        this.choices.add(new Choice(choiceText, nextNode));
    }

    public void addChoice(String choiceText, Runnable action) {
        this.choices.add(new Choice(choiceText, action));
    }

//    public void addChoice(String choiceText, DialogueNode nextNode, Runnable action) {
//        this.choices.add(new Choice(choiceText, nextNode, action));
//    }

    public List<String> getTexts() {
        return texts;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public Runnable getAction() {
        return action;
    }
}
