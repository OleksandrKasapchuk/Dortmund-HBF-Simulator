package com.mygame.action;

import com.mygame.entity.npc.NPC;
import com.mygame.game.GameContext;

public class SetDialogueAction implements GameAction {
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
            // Використовуємо тип NPC (наприклад, "police"), щоб знайти потрібний JSON файл діалогів
            npc.setDialogue(ctx.dialogueRegistry.getDialogue(npc.getType(), newDialogueId));
            npc.setCurrentDialogueNodeId(newDialogueId);
        }
    }
}
