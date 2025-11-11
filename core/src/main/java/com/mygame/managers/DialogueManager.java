package com.mygame.managers;

import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.ui.DialogueUI;

import java.util.ArrayList;

public class DialogueManager {
    private final DialogueUI dialogueUI;
    private final Player player;

    private NPC activeNpc = null;
    private Dialogue activeDialogue = null;
    private NPC recentlyFinishedForcedNpc = null;

    private float textTimer = 0f;
    private final float textSpeed = 0.05f;
    private boolean isForcedDialogue = false;
    private boolean nodeActionExecuted = false;
    private boolean textCompleted = false;

    private int currentTextIndex = 0;

    private float interactCooldown = 0f;
    private final float INTERACT_COOLDOWN_TIME = 0.04f;

    private final DialogueUI.ChoiceListener choiceListener = choice -> {
        if (activeDialogue == null) return;
        if (choice.action != null) choice.action.run();

        if (choice.nextNode != null) {
            activeDialogue.choose(choice);
            displayCurrentNode();
        } else {
            endDialogue();
        }
    };

    public DialogueManager(DialogueUI dialogueUI, Player player) {
        this.dialogueUI = dialogueUI;
        this.player = player;
    }

    public boolean isDialogueActive() {
        return activeDialogue != null;
    }

    public void startDialogue(NPC npc) {
        if (isDialogueActive() || !npc.isPlayerNear(player)) return;
        isForcedDialogue = false;
        commonStartDialogue(npc);
    }

    public void startForcedDialogue(NPC npc) {
        if (isDialogueActive()) endDialogue();
        isForcedDialogue = true;
        commonStartDialogue(npc);
    }

    private void commonStartDialogue(NPC npc) {
        activeNpc = npc;
        activeDialogue = npc.getDialogue();
        if (activeDialogue == null) return;
        activeDialogue.reset();
        if (isForcedDialogue || "Police".equals(npc.getName())) {
            player.setMovementLocked(true);
        }
        displayCurrentNode();
    }

    private void displayCurrentNode() {
        currentTextIndex = 0;
        resetTimersAndFlags();
        DialogueNode currentNode = activeDialogue.getCurrentNode();
        dialogueUI.show(activeNpc.getName(), currentNode, choiceListener);
        displayText();
    }

    private void displayText() {
        resetTimersAndFlags();
        dialogueUI.updateText(""); // Clear previous text
    }

    private void resetTimersAndFlags() {
        textTimer = 0f;
        nodeActionExecuted = false;
        textCompleted = false;
        interactCooldown = INTERACT_COOLDOWN_TIME;
    }

    public void endDialogue() {
        if (!isDialogueActive()) return;
        DialogueNode currentNode = activeDialogue.getCurrentNode();
        // The action for a terminal node is now EXCLUSIVELY called here.
        if (currentNode != null && currentNode.getChoices().isEmpty() && currentNode.getAction() != null && !nodeActionExecuted) {
            currentNode.getAction().run();
            nodeActionExecuted = true; // Mark as executed
        }

        if (isForcedDialogue || (activeNpc != null && "Police".equals(activeNpc.getName()))) {
            recentlyFinishedForcedNpc = activeNpc;
            if (this.player != null) this.player.setMovementLocked(false);
        }
        isForcedDialogue = false;
        if(activeDialogue != null) activeDialogue.reset();
        dialogueUI.hide();
        activeNpc = null;
        activeDialogue = null;
    }

    public void update(float delta, boolean interactPressed, ArrayList<NPC> npcs) {
        if (interactCooldown > 0) {
            interactCooldown -= delta;
        }

        if (!isDialogueActive()) {
            // Start dialogue logic (cleaned up)
            if (player == null) return;
            if (recentlyFinishedForcedNpc != null && !recentlyFinishedForcedNpc.isPlayerNear(player)) recentlyFinishedForcedNpc = null;
            for (NPC npc : npcs) {
                if ("Police".equals(npc.getName()) && npc.isPlayerNear(player) && npc != recentlyFinishedForcedNpc) {
                    startForcedDialogue(npc); return;
                }
            }
            if (interactPressed && interactCooldown <= 0) {
                for (NPC npc : npcs) {
                    if (npc.isPlayerNear(player)) {
                        startDialogue(npc); return;
                    }
                }
            }
            return;
        }

        if (!activeNpc.isPlayerNear(player) && !isForcedDialogue) {
            endDialogue(); return;
        }

        DialogueNode currentNode = activeDialogue.getCurrentNode();
        String currentPhrase = currentNode.getTexts().get(currentTextIndex);

        // Typing Effect
        if (!textCompleted) {
            textTimer += delta;
            int lettersToShow = (int) (textTimer / textSpeed);
            if (lettersToShow >= currentPhrase.length()) {
                textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                dialogueUI.updateText(currentPhrase.substring(0, lettersToShow));
            }
        }

        // Interaction Logic
        if (interactPressed && interactCooldown <= 0) {
            if (!textCompleted) {
                textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                boolean isLastPhrase = currentTextIndex >= currentNode.getTexts().size() - 1;
                if (isLastPhrase) {
                    if (currentNode.getChoices().isEmpty()) {
                        endDialogue();
                    }
                } else {
                    currentTextIndex++;
                    displayText();
                }
            }
            interactCooldown = INTERACT_COOLDOWN_TIME;
        }

        // UI State Logic
        boolean isLastPhrase = currentTextIndex >= currentNode.getTexts().size() - 1;
        dialogueUI.showChoices(textCompleted && isLastPhrase && !currentNode.getChoices().isEmpty());
    }
}
