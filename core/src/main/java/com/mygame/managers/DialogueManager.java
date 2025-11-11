package com.mygame.managers;

import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.ui.DialogueUI;

public class DialogueManager {
    private final DialogueUI dialogueUI;
    private NPC activeNpc = null;
    private Dialogue activeDialogue = null;

    private float textTimer = 0f;
    private final float textSpeed = 0.05f;
    private boolean isForcedDialogue = false;

    private final Player player;

    private final DialogueUI.ChoiceListener choiceListener = choice -> {
        if (activeDialogue == null) return;

        if (choice.action != null) {
            choice.action.run();
        }

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
        activeDialogue.reset();
        player.setMovementLocked(true);
        displayCurrentNode();
    }

    private void displayCurrentNode() {
        textTimer = 0f;
        DialogueNode currentNode = activeDialogue.getCurrentNode();
        dialogueUI.show(activeNpc.getName(), currentNode, choiceListener);
        dialogueUI.updateText("");
    }

    public void endDialogue() {
        if (!isDialogueActive()) return;
        isForcedDialogue = false;
        activeDialogue.reset();
        dialogueUI.hide();
        activeNpc = null;
        activeDialogue = null;
        if (this.player != null) {
            this.player.setMovementLocked(false);
        }
    }

    public void forceAdvance() {
        if (!isDialogueActive()) return;
        String fullText = activeDialogue.getCurrentNode().getText();
        textTimer = fullText.length() * textSpeed;
    }

    public void update(float delta, boolean interactPressed) {
        if (!isDialogueActive()) return;

        if (!activeNpc.isPlayerNear(player) && !isForcedDialogue) {
            endDialogue();
            return;
        }

        DialogueNode currentNode = activeDialogue.getCurrentNode();
        String fullText = currentNode.getText();
        int lettersToShow = (int) (textTimer / textSpeed);

        if (!currentNode.getChoices().isEmpty()) {
            dialogueUI.updateText(fullText);
            dialogueUI.showChoices(true);
            return;
        }

        dialogueUI.showChoices(false);

        if (interactPressed) {
            if (lettersToShow < fullText.length()) {
                textTimer = fullText.length() * textSpeed;
            } else {
                endDialogue();
                return;
            }
        }

        textTimer += delta;
        lettersToShow = Math.min(fullText.length(), (int) (textTimer / textSpeed));
        dialogueUI.updateText(fullText.substring(0, lettersToShow));
    }
}
package com.mygame.managers;

import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.ui.DialogueUI;

import java.util.ArrayList;

public class DialogueManager {
    private float textTimer = 0f;
    private final float textSpeed = 0.05f;
    private NPC activeNpc = null;
    private NPC recentlyFinishedForcedNpc = null;
    private boolean forcedDialogue = false;

    private final DialogueUI dialogueUI;

    public DialogueManager(DialogueUI dialogueUI) {
        this.dialogueUI = dialogueUI;
    }

    public void startForcedDialogue(NPC npc) {
        activeNpc = npc;
        textTimer = 0f;
        forcedDialogue = true;
    }

    public boolean isDialogueActive() {
        return activeNpc != null;
    }

    public void forceAdvance() {
        if (activeNpc != null) {
            textTimer = activeNpc.getCurrentPhrase().length() * textSpeed;
        }
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

        if (activeNpc != null && !forcedDialogue && !activeNpc.isPlayerNear(player)) {
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

                // Якщо це forced NPC — відмічаємо як recentlyFinishedForcedNpc
                if (forcedDialogue) {
                    recentlyFinishedForcedNpc = activeNpc;
                    forcedDialogue = false;
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
