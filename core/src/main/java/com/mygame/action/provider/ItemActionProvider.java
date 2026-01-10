package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.entity.item.Item;
import com.mygame.game.GameContext;

public class ItemActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("item.startCooldown", (c, data) -> () -> {
            Item item = c.itemManager.getItem(data.getString("id"));
            if (item != null) item.startCooldown(data.getFloat("seconds", 1f));
        });
    }
}
