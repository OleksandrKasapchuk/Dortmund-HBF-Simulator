package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.entity.npc.Police;
import com.mygame.game.GameContext;

public class CustomActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("custom.startChase", (c, data) -> () -> {
            Police police = c.npcManager.getSummonedPolice();
            if (police != null) police.startChase(c.player);
        });
    }
}
