package com.mygame.dialogue;

import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
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
    private WorldManager worldManager;
    private DialogueRegistry dialogueRegistry;
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

    public DialogueManager(DialogueUI dialogueUI, Player player, WorldManager worldManager, DialogueRegistry dialogueRegistry) {
        this.dialogueUI = dialogueUI;
        this.player = player;
        this.worldManager = worldManager;
        this.dialogueRegistry = dialogueRegistry;
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
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
        player.setMovementLocked(state.forced || state.activeNode.isForced());
        EventBus.fire(new Events.DialogueStartedEvent(npc.getId()));
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

        dialogueUI.show(state.activeNpc, state.activeNode, choiceListener);

        // Start with empty text for typewriter animation
        dialogueUI.updateText("");
    }

    /**
     * Ends current dialogue and unlocks movement.
     */
    public void endDialogue() {
        if (!state.isActive()) return;

        String npcId = state.activeNpc.getId();

        if (player != null) player.setMovementLocked(false);

        // Used to prevent re-triggering Police forced dialogue
        if (state.forced && state.activeNpc != null) {
            recentlyFinishedForcedNpc = state.activeNpc;
        }

        dialogueUI.hide();

        state.activeNpc = null;
        state.activeNode = null;
        state.forced = false;

        // Fire event when dialogue finishes
        EventBus.fire(new Events.DialogueFinishedEvent(npcId));
    }

    private void handleInteraction() {
        if (interactCooldown > 0) return;

        if (!state.isActive()) {
            // Standard interaction - start a new dialogue
            if (worldManager.getCurrentWorld() != null) {
                for (NPC npc : worldManager.getCurrentWorld().getNpcs()) {
                    if (npc.isPlayerNear(player)) {
                        startDialogue(npc);
                        return;
                    }
                }
            }
        } else {
            // Dialogue is active - advance it
            String currentPhrase = state.activeNode.getTexts().get(state.textIndex);
            if (!state.textCompleted) {
                // Skip typewriter animation
                state.textCompleted = true;
                dialogueUI.updateText(currentPhrase);
            } else {
                if (state.isLastPhrase()) {
                    if (state.activeNode.getAction() != null) EventBus.fire(new Events.ActionRequestEvent(state.activeNode.getAction()));
                    if (!state.activeNode.getChoices().isEmpty()) return;
                    String nextNodeName = state.activeNode.getNextNode();
                    if (nextNodeName != null) {
                        state.activeNode = dialogueRegistry.getDialogue(state.activeNpc.getId(), nextNodeName);
                        displayCurrentNode();
                    }
                     else {
                        endDialogue();
                    }
                } else {
                    state.textIndex++;
                    state.textCompleted = false;
                    typeWriter.reset();
                }
            }
            interactCooldown = INTERACT_COOLDOWN_TIME;
        }
    }

    /**
     * Called every frame — handles:
     * - detecting new interactions
     * - updating text animation
     * - continuing dialogue on press
     */
    public void update(float delta) {
        if (worldManager.getCurrentWorld() == null) return;
        ArrayList<NPC> npcs = worldManager.getCurrentWorld().getNpcs();

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

        // Show choices only at the end of last phrase
        dialogueUI.showChoices(state.textCompleted && state.isLastPhrase() && !state.activeNode.getChoices().isEmpty());
    }
}
