package com.mygame;


import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    private final List<DialogueNode> nodes = new ArrayList<>();
    private DialogueNode currentNode;
    private final DialogueNode startNode;

    public Dialogue(DialogueNode startNode) {
        this.startNode = startNode;
        this.currentNode = startNode;
        addNode(startNode);
    }

    public void addNode(DialogueNode node) {
        if (!nodes.contains(node))
            nodes.add(node);
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(DialogueNode node) {
        this.currentNode = node;
    }

    public void reset() {
        this.currentNode = startNode;
    }

    public void choose(DialogueNode.Choice choice) {
        if (choice.action != null) {
            choice.action.run();
        }
        if (choice.nextNode != null) {
            currentNode = choice.nextNode;
        }
    }

    public List<DialogueNode> getNodes() {
        return nodes;
    }
}
