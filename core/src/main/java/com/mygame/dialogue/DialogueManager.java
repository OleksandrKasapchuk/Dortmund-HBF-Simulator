package com.mygame.dialogue;

import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.managers.global.QuestManager;
import com.mygame.world.WorldManager;
import com.mygame.ui.inGameUI.DialogueUI;

import java.util.ArrayList;

/**
 * Controls all dialogue logic:
 * - starting / ending dialogues
 * - forced dialogues
 * - typewriter text animation
 * - switching lines & nodes
 * - choice selection
 * - cooldown to prevent spam-interaction
 */
public class DialogueManager {

    private final DialogueUI dialogueUI;
    private final Player player;

    private final DialogueState state = new DialogueState();
    private final TypeWriterController typeWriter = new TypeWriterController();

    private NPC recentlyFinishedForcedNpc;

    // Prevents multiple interactions per frame
    private float interactCooldown = 0f;
    private static final float INTERACT_COOLDOWN_TIME = 0.04f;

    /**
     * Listener that gets triggered when the player selects a choice.
     * Executes custom action + jumps to next dialogue node.
     */
    private final DialogueUI.ChoiceListener choiceListener = choice -> {
        if (!state.isActive()) return;

        if (choice.action() != null) choice.action().run();
        if (choice.nextNode() != null) {
            state.activeNode = choice.nextNode();
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
        if (state.isActive() || !npc.isPlayerNear(player)) return;
        state.forced = false;
        beginDialogue(npc);
    }

    /**
     * Starts a forced dialogue (Police).
     */
    public void startForcedDialogue(NPC npc) {
        if (state.isActive()) endDialogue();
        state.forced = true;
        beginDialogue(npc);
    }

    /**
     * Internal function that resets dialogue state and opens the UI.
     */
    private void beginDialogue(NPC npc) {
        state.activeNpc = npc;
        state.activeNode = npc.getDialogue();
        if (!state.isActive()) return;

        // Forced dialogue → player cannot move
        player.setMovementLocked(state.activeNode.isForced());

        displayCurrentNode();
    }

    /**
     * Displays the current node from the dialogue:
     * resets animation + updates UI.
     */
    private void displayCurrentNode() {
        state.resetNode();
        typeWriter.reset();
        interactCooldown = INTERACT_COOLDOWN_TIME;

        dialogueUI.show(state.activeNpc.getName(), state.activeNode, choiceListener);

        // Start with empty text for typewriter animation
        dialogueUI.updateText("");
    }

    /**
     * Ends current dialogue and unlocks movement.
     */
    public void endDialogue() {
        if (!state.isActive()) return;

        if (player != null) player.setMovementLocked(false);

        // Used to prevent re-triggering Police forced dialogue
        if (state.forced && state.activeNpc != null) {
            recentlyFinishedForcedNpc = state.activeNpc;
        }

        dialogueUI.hide();

        state.activeNpc = null;
        state.activeNode = null;
        state.forced = false;
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
        if (!state.isActive()) {

            if (player == null) return;

            // Allow Police to trigger again after player walks away
            if (recentlyFinishedForcedNpc != null &&
                !recentlyFinishedForcedNpc.isPlayerNear(player)) {
                recentlyFinishedForcedNpc = null;
            }

            // Police auto-dialog
            for (NPC npc : npcs) {
                if (npc.getDialogue().isForced() && npc.isPlayerNear(player) && npc != recentlyFinishedForcedNpc) {
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
        // If the player walks away → exit dialogue (unless forced)
        if (!state.activeNpc.isPlayerNear(player) && !state.forced) {
            endDialogue();
            return;
        }

        if (state.activeNode.getTexts().isEmpty()) {
            dialogueUI.updateText("");
            dialogueUI.showChoices(true);
            return;
        }

        String currentPhrase = state.activeNode.getTexts().get(state.textIndex);

        // Typewriter animation
        if (!state.textCompleted) {
            String animatedText = typeWriter.update(currentPhrase, delta);
            dialogueUI.updateText(animatedText);
            if(typeWriter.isFinished(currentPhrase)) {
                state.textCompleted = true;
            }
        }

        // Interact to skip/continue
        if (interactPressed && interactCooldown <= 0) {

            if (!state.textCompleted) {
                // Skip typewriter animation
                state.textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                if (state.isLastPhrase()) {
                    if (state.activeNode.getAction() != null) {
                        state.activeNode.getAction().run();
                    }

                    if (state.activeNode.getChoices().isEmpty()) {
                        if (QuestManager.getQuest("jason2") != null && !state.activeNpc.isTalked()) {
                            QuestManager.getQuest("jason2").makeProgress();
                            state.activeNpc.setTalked();
                        }
                        endDialogue();
                        return;
                    }
                } else {
                    state.textIndex++;
                    state.textCompleted = false;
                    typeWriter.reset();
                }
            }

            interactCooldown = INTERACT_COOLDOWN_TIME;
        }

        // Show choices only at the end of last phrase
        dialogueUI.showChoices(state.textCompleted && state.isLastPhrase() && !state.activeNode.getChoices().isEmpty());
    }
}
