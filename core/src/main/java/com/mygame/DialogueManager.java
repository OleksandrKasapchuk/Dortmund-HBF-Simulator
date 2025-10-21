package com.mygame;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class DialogueManager {
    private float textTimer = 0f;
    private final Label dialogueLabel;
    private final float textSpeed = 0.05f;
    private NPC activeNpc = null;
    private final Table dialogueTable;
    private final Label nameLabel;
    private NPC recentlyFinishedForcedNpc = null;

    public DialogueManager(Table dialogueTable, Label nameLabel, Label dialogueLabel) {
        this.dialogueTable = dialogueTable;
        this.nameLabel = nameLabel;
        this.dialogueLabel = dialogueLabel;
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
                dialogueTable.setVisible(true);
                nameLabel.setText(activeNpc.getName());
                String fullText = activeNpc.getCurrentPhrase();

                textTimer += delta;
                int lettersToShow = (int) (textTimer / textSpeed);

                if (lettersToShow > fullText.length()) {
                    lettersToShow = fullText.length();
                }
                String currentText = fullText.substring(0, lettersToShow);
                dialogueLabel.setText(currentText);
            } else {
                activeNpc.runAction();
                activeNpc.resetDialogue();
                if (activeNpc.getName().equals("Police")) {
                    recentlyFinishedForcedNpc = activeNpc;
                    player.setMovementLocked(false);
                }
                activeNpc = null; // Завершуємо діалог
            }
        } else {
            dialogueTable.setVisible(false);
        }
    }
}
