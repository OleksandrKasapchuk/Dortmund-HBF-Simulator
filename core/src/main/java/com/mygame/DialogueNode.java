package com.mygame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a single node in a dialogue sequence.
 * Each node contains:
 *  - Texts to display to the player
 *  - Optional action to execute when this node is reached
 *  - List of choices that the player can select to progress the dialogue
 */
public class DialogueNode {

    /**
     * Represents a choice that a player can select from a dialogue node.
     * Each choice can:
     *  - Lead to another DialogueNode (nextNode)
     *  - Trigger an action (Runnable)
     *  - Have display text
     */
    public static class Choice {
        public final String text;           // Text displayed for this choice
        public final DialogueNode nextNode; // Node to move to if this choice is selected
        public final Runnable action;       // Action to execute when this choice is selected

        // Choice that leads to another node
        public Choice(String text, DialogueNode nextNode) {
            this(text, nextNode, null);
        }

        // Choice that executes an action without moving to a new node
        public Choice(String text, Runnable action) {
            this(text, null, action);
        }

        // Choice with both a next node and an action
        public Choice(String text, DialogueNode nextNode, Runnable action) {
            this.text = text;
            this.nextNode = nextNode;
            this.action = action;
        }
    }

    private final List<String> texts;   // Dialogue text lines for this node
    private final List<Choice> choices; // List of choices available at this node
    private final Runnable action;      // Optional action executed when entering this node

    // Private constructor used internally
    private DialogueNode(Runnable action, List<String> texts) {
        this.texts = (texts == null || texts.isEmpty()) ? List.of("") : texts;
        this.choices = new ArrayList<>();
        this.action = action;
    }

    // Constructor with only texts
    public DialogueNode(String... texts) {
        this(null, Arrays.asList(texts));
    }

    // Constructor with action and texts
    public DialogueNode(Runnable action, String... texts) {
        this(action, Arrays.asList(texts));
    }

    /**
     * Adds a choice leading to another dialogue node.
     */
    public void addChoice(String choiceText, DialogueNode nextNode) {
        this.choices.add(new Choice(choiceText, nextNode));
    }

    /**
     * Adds a choice that executes an action when selected.
     */
    public void addChoice(String choiceText, Runnable action) {
        this.choices.add(new Choice(choiceText, action));
    }

    // Getters
    public List<String> getTexts() { return texts; }
    public List<Choice> getChoices() { return choices; }
    public Runnable getAction() { return action; }
}
