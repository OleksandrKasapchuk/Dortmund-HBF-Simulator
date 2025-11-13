package com.mygame;


public class Dialogue {
    private final DialogueNode startNode;
    private DialogueNode currentNode;

    public Dialogue(DialogueNode startNode) {
        this.startNode = startNode;
        this.currentNode = startNode;
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    public void choose(DialogueNode.Choice choice) {
        if (choice.action != null) {choice.action.run();}
        if (choice.nextNode != null) {currentNode = choice.nextNode;}
    }
    public void reset() {
        this.currentNode = startNode;
    }
}
