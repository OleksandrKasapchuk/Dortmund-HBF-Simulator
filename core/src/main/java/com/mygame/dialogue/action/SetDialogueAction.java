package com.mygame.dialogue.action;

import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.game.GameContext;

public class SetDialogueAction implements DialogueAction {
    private final GameContext ctx;

    private String npcId;
    private String newDialogueId;


    public SetDialogueAction(GameContext ctx, String npcIdForDialogue, String newDialogueId) {
        this.ctx = ctx;
        this.npcId = npcIdForDialogue;
        this.newDialogueId = newDialogueId;
    }

    @Override
    public void execute() {
        NPC npc = ctx.npcManager.findNpcById(npcId);
        if (npc != null) {
            npc.setDialogue(DialogueRegistry.getDialogue(npcId, newDialogueId));
            npc.setCurrentDialogueNodeId(newDialogueId);
        }
    }
}
