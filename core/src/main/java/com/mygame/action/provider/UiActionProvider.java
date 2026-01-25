package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;

public class UiActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("ui.notEnoughMessage", (c, data) -> () -> EventBus.fire(new Events.NotEnoughMessageEvent(c.itemRegistry.get(data.getString("item")))));
        registry.registerCreator("ui.message", (c, data) -> () -> EventBus.fire(new Events.MessageEvent(Assets.messages.get(data.getString("key")))));
        registry.registerCreator("ui.overlay", (c, data) -> () -> EventBus.fire( new Events.OverlayEvent(data.getFloat("duration"),data.getBoolean("isBlack"))));

        registry.registerAction("ui.inventory.toggle", context.ui::toggleInventoryTable);
        registry.registerAction("ui.quests.toggle", context.ui::toggleQuestTable);


    }
}
