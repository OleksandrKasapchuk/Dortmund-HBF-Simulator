package com.mygame;

import java.util.ArrayList;
import java.util.List;

public class DialogueNode {

    public static class Choice {
        public final String text;
        public final DialogueNode nextNode;
        public final Runnable action;

        // Конструктор для вибору, що веде до іншої фрази
        public Choice(String name, DialogueNode nextNode) {
            this.text = name;
            this.nextNode = nextNode;
            this.action = null;
        }

        // Конструктор для вибору, що ОДРАЗУ виконує дію
        public Choice(String text, Runnable action) {
            this.text = text;
            this.nextNode = null;
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

    public String getText() {
        return text;
    }
    public List<Choice> getChoices() {
        return choices;
    }
}
