package com.mygame;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {

    public static class Choice {
        public final String text;
        public final DialogueNode nextNode;
        public final Runnable action;

        // Вибір, що веде до іншого вузла
        public Choice(String text, DialogueNode nextNode) {
            this(text, nextNode, null);
        }

        // Вибір, що виконує дію і завершує діалог
        public Choice(String text, Runnable action) {
            this(text, null, action);
        }

        // Вибір, що виконує дію І веде до іншого вузла
        public Choice(String text, DialogueNode nextNode, Runnable action) {
            this.text = text;
            this.nextNode = nextNode;
            this.action = action;
        }
    }

    private final String text;
    private final List<Choice> choices;

    public DialogueNode(String text) {
        this.text = text;
        this.choices = new ArrayList<>();
    }

    public void addChoice(String choiceText, DialogueNode nextNode) {
        this.choices.add(new Choice(choiceText, nextNode));
    }

    public void addChoice(String choiceText, Runnable action) {
        this.choices.add(new Choice(choiceText, action));
    }

    public void addChoice(String choiceText, DialogueNode nextNode, Runnable action) {
        this.choices.add(new Choice(choiceText, nextNode, action));
    }

    public String getText() {
        return text;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
