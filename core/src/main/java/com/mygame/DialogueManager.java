package com.mygame;

import com.mygame.ui.DialogueUI;

import java.util.ArrayList;

public class DialogueManager {
    private float textTimer = 0f;
    private final float textSpeed = 0.05f;
    private NPC activeNpc = null;
    private NPC recentlyFinishedForcedNpc = null;

    private final DialogueUI dialogueUI;

    public DialogueManager(DialogueUI dialogueUI) {
        this.dialogueUI = dialogueUI;
    }

    public void update(float delta, ArrayList<NPC> npcs, Player player, boolean interactPressed) {
        if (recentlyFinishedForcedNpc != null && !recentlyFinishedForcedNpc.isPlayerNear(player)) {
            recentlyFinishedForcedNpc = null;
        }

        if (activeNpc == null) {
            for (NPC npc : npcs) {
                if (npc.getName().equals("Police") && npc.isPlayerNear(player) && npc != recentlyFinishedForcedNpc) {
                    activeNpc = npc;
                    textTimer = 0f;
                    break;
                }
            }
        }

        if (interactPressed) {
            if (activeNpc != null) {
                String fullText = activeNpc.getCurrentPhrase();
                int lettersToShow = (int) (textTimer / textSpeed);

                if (lettersToShow < fullText.length()) {
                    textTimer = fullText.length() * textSpeed;
                } else {
                    activeNpc.advanceDialogue();
                    textTimer = 0f;
                }
            } else {
                for (NPC npc : npcs) {
                    if (npc.isPlayerNear(player)) {
                        activeNpc = npc;
                        textTimer = 0f;
                        break;
                    }
                }
            }
        }

        if (activeNpc != null && !activeNpc.isPlayerNear(player)) {
            activeNpc.resetDialogue();
            activeNpc = null;
        }

        if (activeNpc != null) {
            if (!activeNpc.isDialogueFinished()) {
                if (activeNpc.getName().equals("Police")) {
                    player.setMovementLocked(true);
                }
                dialogueUI.show(activeNpc.getName(), activeNpc.getCurrentPhrase().substring(0,
                    Math.min(activeNpc.getCurrentPhrase().length(), (int)(textTimer / textSpeed))));
                textTimer += delta;
            } else {
                activeNpc.runAction();
                activeNpc.resetDialogue();
                if (activeNpc.getName().equals("Police")) {
                    recentlyFinishedForcedNpc = activeNpc;
                    player.setMovementLocked(false);
                }
                activeNpc = null;
                dialogueUI.hide();
            }
        } else {
            dialogueUI.hide();
        }
    }
}
