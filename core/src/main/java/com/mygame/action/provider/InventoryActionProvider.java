package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.action.TradeAction;
import com.mygame.game.GameContext;

public class InventoryActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("inventory.trade", (c, data) -> () -> new TradeAction(c,
                data.getString("from"), data.getString("to"),
                data.getInt("fromAmount"), data.getInt("toAmount")).execute());

        registry.registerCreator("inventory.add", (c, data) -> () -> c.player.getInventory().addItemAndNotify(
                c.itemRegistry.get(data.getString("id")),
                data.getInt("amount", 1)));

        registry.registerCreator("inventory.remove", (c, data) -> () -> {
            if (data.has("items")) {
                for (String itemId : data.get("items").asStringArray()) {
                    c.player.getInventory().removeItem(c.itemRegistry.get(itemId), data.getInt("amount", 9999));
                }
            } else {
                c.player.getInventory().removeItem(
                        c.itemRegistry.get(data.getString("id")),
                        data.getInt("amount", 1));
            }
        });

        registry.registerCreator("inventory.conditionalTrade", (c, data) -> () -> {
            if (c.player.getInventory().trade(c.itemRegistry.get(data.getString("from")),
                    c.itemRegistry.get(data.getString("to")),
                    data.getInt("fromAmount"), data.getInt("toAmount"))) {
                if (data.has("onSuccess")) {
                    registry.createAction(c, data.get("onSuccess")).run();
                }
            } else {
                if (data.has("onFail")) {
                    registry.createAction(c, data.get("onFail")).run();
                }
            }
        });

        registry.registerCreator("inventory.check", (c, data) -> () -> {
            boolean conditionMet = false;
            if (data.has("items")) {
                for (String itemId : data.get("items").asStringArray()) {
                    if (c.player.getInventory().getAmount(c.itemRegistry.get(itemId)) > 0) {
                        conditionMet = true;
                        break;
                    }
                }
            } else if (data.has("itemId")) {
                int amount = data.getInt("amount", 1);
                conditionMet = c.player.getInventory().getAmount(c.itemRegistry.get(data.getString("itemId"))) >= amount;
            }

            if (conditionMet) {
                if (data.has("action")) registry.createAction(c, data.get("action")).run();
                if (data.has("onSuccess")) registry.createAction(c, data.get("onSuccess")).run();
            } else {
                if (data.has("onFail")) registry.createAction(c, data.get("onFail")).run();
            }
        });
    }
}
