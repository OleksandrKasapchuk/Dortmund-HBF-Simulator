package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.action.SetDialogueAction;
import com.mygame.entity.npc.NPC;
import com.mygame.game.GameContext;

public class DialogueActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("dialogue.set", (c, data) -> () -> new SetDialogueAction(c,
                data.getString("npc"), data.getString("node")).execute());

        registry.registerCreator("dialogue.force", (c, data) -> () -> {
            NPC npc = c.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) c.ui.getDialogueManager().startForcedDialogue(npc);
        });
    }
}
