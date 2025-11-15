package com.mygame.dialogue;

/**
 * Represents a dialogue sequence for NPCs or game events.
 * Consists of nodes (DialogueNode) connected via choices.

 * Each node can have multiple choices, and each choice may trigger an action
 * and/or lead to another dialogue node.

 * Usage:
 * - Create a Dialogue with a starting node.
 * - Call choose() to select a choice and progress through the dialogue.
 * - Call reset() to go back to the starting node.
 */
public class Dialogue {

    // The initial node of the dialogue
    private final DialogueNode startNode;

    // The current node the player is interacting with
    private DialogueNode currentNode;

    /**
     * Constructs a dialogue starting from the given node.
     *
     * @param startNode The first node of the dialogue
     */
    public Dialogue(DialogueNode startNode) {
        this.startNode = startNode;
        this.currentNode = startNode;
    }

    /**
     * Returns the current dialogue node.
     *
     * @return Current node
     */
    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Chooses an option in the current dialogue node.
     * Executes the choice's action (if any) and moves to the next node (if any).
     *
     * @param choice The choice selected by the player
     */
    public void choose(DialogueNode.Choice choice) {
        if (choice.action != null) {
            choice.action.run(); // Execute any action associated with the choice
        }
        if (choice.nextNode != null) {
            currentNode = choice.nextNode; // Move to the next node
        }
    }

    /**
     * Resets the dialogue to the starting node.
     */
    public void reset() {
        this.currentNode = startNode;
    }
}
