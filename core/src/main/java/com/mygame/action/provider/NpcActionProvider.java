package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.assets.Assets;
import com.mygame.entity.npc.NPC;
import com.mygame.game.GameContext;

public class NpcActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("npc.setTexture", (c, data) -> () -> {
            var npc = c.npcManager.findNpcById((data.getString("npc")));
            if (npc != null) npc.setTexture(Assets.getTexture(data.getString("texture")));
        });


        registry.registerCreator("npc.spawnNearPlayer", (c, data) -> () -> {
            NPC npc = c.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) {
                npc.setWorld(c.worldManager.getCurrentWorld());
                npc.setX(c.player.getX() + data.getFloat("offsetX", 0f));
                npc.setY(c.player.getY() + data.getFloat("offsetY", 0f));
            }
        });
        registry.registerCreator("npc.teleport", (c, data) -> () -> c.npcManager.teleportNpc(
            data.getString("npc"),
            c.worldManager.getWorld(data.getString("world")),
            data.getFloat("x", 0f),
            data.getFloat("y", 0f)
        ));


        registry.registerCreator("npc.callPolice", (c, data) -> c.npcManager::callPolice);
    }
}
