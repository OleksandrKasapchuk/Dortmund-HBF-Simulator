package com.mygame.dialogue.action;

import com.mygame.assets.Assets;
import com.mygame.entity.npc.NPC;

public class SetDialogueAction implements DialogueAction {
    private final ActionContext ctx;

    private String npcNameBundleKey;
    private String npcIdForDialogue;
    private String newDialogueId;


    public SetDialogueAction(ActionContext ctx, String npcNameBundleKey, String npcIdForDialogue, String newDialogueId) {
        this.ctx = ctx;
        this.npcNameBundleKey = npcNameBundleKey;
        this.npcIdForDialogue = npcIdForDialogue;
        this.newDialogueId = newDialogueId;
    }

    @Override
    public void execute() {
        NPC npc = ctx.npcManager.findNpcByName(Assets.bundle.get(npcNameBundleKey));
        if (npc != null) {
            npc.setDialogue(ctx.registry.getDialogue(npcIdForDialogue, newDialogueId));
        }
    }
}
