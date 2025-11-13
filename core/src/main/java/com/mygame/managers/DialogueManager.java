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

    private NPC activeNpc;
    private Dialogue activeDialogue;
    private NPC recentlyFinishedForcedNpc;

    private boolean isForcedDialogue = false;
    private boolean textCompleted = false;

    private int currentTextIndex;
    private float textTimer;
    private static final float TEXT_SPEED = 0.05f;

    private float interactCooldown = 0f;
    private static final float INTERACT_COOLDOWN_TIME = 0.04f;

    private final DialogueUI.ChoiceListener choiceListener = choice -> {
        if (activeDialogue == null) return;

        if (choice.action != null) choice.action.run();

        if (choice.nextNode != null) {
            activeDialogue.choose(choice);
            displayCurrentNode();
        } else { endDialogue(); }
    };

    public DialogueManager(DialogueUI dialogueUI, Player player) {
        this.dialogueUI = dialogueUI;
        this.player = player;
    }

    public boolean isDialogueActive() {return activeDialogue != null;}

    public void startDialogue(NPC npc) {
        if (isDialogueActive() || !npc.isPlayerNear(player)) return;
        isForcedDialogue = false;
        beginDialogue(npc);
    }

    public void startForcedDialogue(NPC npc) {
        if (isDialogueActive()) endDialogue();
        isForcedDialogue = true;
        beginDialogue(npc);
    }

    private void beginDialogue(NPC npc) {
        activeNpc = npc;
        activeDialogue = npc.getDialogue();
        if (activeDialogue == null) return;

        activeDialogue.reset();
        player.setMovementLocked(isForcedDialogue || "Police".equals(npc.getName()));

        displayCurrentNode();
    }

    private void displayCurrentNode() {
        currentTextIndex = 0;
        resetTimers();
        DialogueNode node = activeDialogue.getCurrentNode();

        dialogueUI.show(activeNpc.getName(), node, choiceListener);
        dialogueUI.updateText("");
    }

    private void resetTimers() {
        textTimer = 0f;
        textCompleted = false;
        interactCooldown = INTERACT_COOLDOWN_TIME;
    }

    public void endDialogue() {
        if (!isDialogueActive()) return;

        if (player != null) player.setMovementLocked(false);

        if (isForcedDialogue && activeNpc != null) {recentlyFinishedForcedNpc = activeNpc;}

        isForcedDialogue = false;
        if (activeDialogue.getCurrentNode().getAction() != null) activeDialogue.getCurrentNode().getAction().run();
        if (activeDialogue != null) activeDialogue.reset();

        dialogueUI.hide();
        activeNpc = null;
        activeDialogue = null;
    }

    public void update(float delta, boolean interactPressed, ArrayList<NPC> npcs) {
        if (interactCooldown > 0) interactCooldown -= delta;

        if (!isDialogueActive()) {
            if (player == null) return;

            if (recentlyFinishedForcedNpc != null && !recentlyFinishedForcedNpc.isPlayerNear(player)) recentlyFinishedForcedNpc = null;

            for (NPC npc : npcs) {
                if ("Police".equals(npc.getName()) && npc.isPlayerNear(player) && npc != recentlyFinishedForcedNpc) {
                    startForcedDialogue(npc);
                    return;
                }
            }

            if (interactPressed && interactCooldown <= 0) {
                for (NPC npc : npcs) {
                    if (npc.isPlayerNear(player)) {
                        startDialogue(npc);
                        return;
                    }
                }
            }
            return;
        }

        if (!activeNpc.isPlayerNear(player) && !isForcedDialogue) {endDialogue();return;}

        DialogueNode currentNode = activeDialogue.getCurrentNode();
        if (currentNode == null || currentNode.getTexts().isEmpty()) {
            dialogueUI.updateText("");
            dialogueUI.showChoices(true);
            return;
        }

        String currentPhrase = currentNode.getTexts().get(currentTextIndex);

        if (!textCompleted) {
            textTimer += delta;
            int lettersToShow = (int) (textTimer / TEXT_SPEED);
            if (lettersToShow >= currentPhrase.length()) {
                textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                dialogueUI.updateText(currentPhrase.substring(0, lettersToShow));
            }
        }

        if (interactPressed && interactCooldown <= 0) {
            if (!textCompleted) {
                textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                boolean lastPhrase = currentTextIndex >= currentNode.getTexts().size() - 1;
                if (lastPhrase) {
                    if (currentNode.getChoices().isEmpty()) {
                        endDialogue();
                    }
                } else {
                    currentTextIndex++;
                    resetTimers();
                }
            }
            interactCooldown = INTERACT_COOLDOWN_TIME;
        }

        boolean lastPhrase = currentTextIndex >= currentNode.getTexts().size() - 1;
        dialogueUI.showChoices(textCompleted && lastPhrase && !currentNode.getChoices().isEmpty());
    }
}
