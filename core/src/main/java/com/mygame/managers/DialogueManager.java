package com.mygame.managers;

import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.ui.DialogueUI;

import java.util.ArrayList;

public class DialogueManager {
    private NPC activeNpc = null;
    private Dialogue activeDialogue = null;
    private final DialogueUI dialogueUI;
    private boolean dialogueVisible = false;

    public DialogueManager(DialogueUI dialogueUI) {
        this.dialogueUI = dialogueUI;
    }


    public void update(ArrayList<NPC> npcs, Player player, boolean interactPressed) {
        if (interactPressed) {
            if (dialogueVisible) {
                dialogueUI.hide();
                activeNpc = null;
                activeDialogue = null;
                dialogueVisible = false;
                return;
            }

            for (NPC npc : npcs) {
                if (npc.isPlayerNear(player)) {
                    activeNpc = npc;
                    activeDialogue = npc.getDialogue();

                    if (activeDialogue != null) {
                        DialogueNode node = activeDialogue.getCurrentNode();
                        showNode(npc, node);
                        dialogueVisible = true;
                    }
                    break;
                }
            }
        }
    }

    private void showNode(NPC npc, DialogueNode node) {
        dialogueUI.show(npc.getName(), node, choice -> {
            activeDialogue.choose(choice);
            DialogueNode next = activeDialogue.getCurrentNode();

            if (next.getChoices().isEmpty()) {
                dialogueUI.hide();
                dialogueVisible = false;
                activeNpc = null;
                activeDialogue.reset();
            } else {
                showNode(npc, next);
            }
        });
    }
}
