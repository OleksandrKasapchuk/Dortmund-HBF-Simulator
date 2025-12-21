package com.mygame.dialogue;

import com.mygame.entity.npc.NPC;


public class DialogueState {

    public NPC activeNpc;
    public DialogueNode activeNode;

    public boolean forced;
    public boolean textCompleted;

    public int textIndex;

    public void resetNode() {
        textIndex = 0;
        textCompleted = false;
    }

    public boolean isActive() {
        return activeNode != null;
    }

    public boolean isLastPhrase() {
        return activeNode != null &&
            textIndex >= activeNode.getTexts().size() - 1;
    }
}
