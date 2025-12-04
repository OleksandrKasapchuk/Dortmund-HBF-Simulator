package com.mygame.managers.nonglobal;

import com.mygame.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.world.WorldManager;
import com.mygame.ui.DialogueUI;

import java.util.ArrayList;

/**
 * Controls all dialogue logic:
 * - starting / ending dialogues
 * - forced dialogues (Police)
 * - typewriter text animation
 * - switching lines & nodes
 * - choice selection
 * - cooldown to prevent spam-interaction
 */
public class DialogueManager {

    private final DialogueUI dialogueUI;
    private final Player player;

    // Dialogue state
    private NPC activeNpc;
    private DialogueNode activeDialogue;
    private NPC recentlyFinishedForcedNpc;

    private boolean isForcedDialogue = false;
    private boolean textCompleted = false;

    // Typewriter animation
    private int currentTextIndex;
    private float textTimer;
    private static final float TEXT_SPEED = 0.05f;

    // Prevents multiple interactions per frame
    private float interactCooldown = 0f;
    private static final float INTERACT_COOLDOWN_TIME = 0.04f;

    /**
     * Listener that gets triggered when the player selects a choice.
     * Executes custom action + jumps to next dialogue node.
     */
    private final DialogueUI.ChoiceListener choiceListener = choice -> {
        if (activeDialogue == null) return;

        if (choice.action() != null) choice.action().run();
        if (choice.nextNode() != null) {
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

    /**
     * Starts a normal (non-forced) dialogue if player is close enough.
     */
    public void startDialogue(NPC npc) {
        if (activeDialogue != null || !npc.isPlayerNear(player)) return;
        isForcedDialogue = false;
        beginDialogue(npc);
    }

    /**
     * Starts a forced dialogue (Police).
     */
    public void startForcedDialogue(NPC npc) {
        if (activeDialogue != null) endDialogue();
        isForcedDialogue = true;
        beginDialogue(npc);
    }

    /**
     * Internal function that resets dialogue state and opens the UI.
     */
    private void beginDialogue(NPC npc) {
        activeNpc = npc;
        activeDialogue = npc.getDialogue();
        if (activeDialogue == null) return;

//        activeDialogue.reset();

        // Forced dialogue or Police → player cannot move
        player.setMovementLocked(isForcedDialogue || Assets.bundle.get("npc.police.name").equals(npc.getName()));

        displayCurrentNode();
    }

    /**
     * Displays the current node from the dialogue:
     * resets animation + updates UI.
     */
    private void displayCurrentNode() {
        currentTextIndex = 0;
        resetTimers();

        dialogueUI.show(activeNpc.getName(), activeDialogue, choiceListener);

        // Start with empty text for typewriter animation
        dialogueUI.updateText("");
    }

    private void resetTimers() {
        textTimer = 0f;
        textCompleted = false;
        interactCooldown = INTERACT_COOLDOWN_TIME;
    }

    /**
     * Ends current dialogue and unlocks movement.
     */
    public void endDialogue() {
        if (activeDialogue == null) return;

        if (player != null) player.setMovementLocked(false);

        // Used to prevent re-triggering Police forced dialogue
        if (isForcedDialogue && activeNpc != null) {
            recentlyFinishedForcedNpc = activeNpc;
        }

        dialogueUI.hide();

        activeNpc = null;
        activeDialogue = null;
        isForcedDialogue = false;
    }

    /**
     * Called every frame — handles:
     * - detecting new interactions
     * - updating text animation
     * - continuing dialogue on press
     */
    public void update(float delta, boolean interactPressed) {
        if (WorldManager.getCurrentWorld() == null) return;
        ArrayList<NPC> npcs = WorldManager.getCurrentWorld().getNpcs();

        // Cooldown
        if (interactCooldown > 0) interactCooldown -= delta;

        // ─────────────────────────────
        // NO DIALOGUE ACTIVE
        // ─────────────────────────────
        if (activeDialogue == null) {

            if (player == null) return;

            // Allow Police to trigger again after player walks away
            if (recentlyFinishedForcedNpc != null &&
                !recentlyFinishedForcedNpc.isPlayerNear(player)) {
                recentlyFinishedForcedNpc = null;
            }

            // Police auto-dialog
            for (NPC npc : npcs) {
                if (Assets.bundle.get("npc.police.name").equals(npc.getName())
                    && npc.isPlayerNear(player)
                    && npc != recentlyFinishedForcedNpc) {
                    startForcedDialogue(npc);
                    return;
                }
            }

            // Standard interaction
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

        // ─────────────────────────────
        // DIALOGUE IS ACTIVE
        // ─────────────────────────────
//
        // If the player walks away → exit dialogue (unless forced)
        if (!activeNpc.isPlayerNear(player) && !isForcedDialogue) {
            endDialogue();
            return;
        }

        if (activeDialogue.getTexts().isEmpty()) {
            dialogueUI.updateText("");
            dialogueUI.showChoices(true);
            return;
        }

        String currentPhrase = activeDialogue.getTexts().get(currentTextIndex);

        // Typewriter animation
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

        // Interact to skip/continue
        if (interactPressed && interactCooldown <= 0) {

            if (!textCompleted) {
                // Skip typewriter animation
                textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                boolean lastPhrase = currentTextIndex >= activeDialogue.getTexts().size() - 1;

                if (lastPhrase) {
                    if (activeDialogue.getAction() != null) {
                        activeDialogue.getAction().run();
                    }
                    if (activeDialogue.getChoices().isEmpty()) {
                        endDialogue();
                        return;
                    }
                } else {
                    currentTextIndex++;
                    resetTimers();
                }
            }

            interactCooldown = INTERACT_COOLDOWN_TIME;
        }

        boolean lastPhrase = currentTextIndex >= activeDialogue.getTexts().size() - 1;

        // Show choices only at the end of last phrase
        dialogueUI.showChoices(textCompleted && lastPhrase && !activeDialogue.getChoices().isEmpty());
    }
}
