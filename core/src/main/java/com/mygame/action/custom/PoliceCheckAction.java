package com.mygame.action.custom;

import com.mygame.assets.Assets;
import com.mygame.action.GameAction;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;

public class PoliceCheckAction implements GameAction {

    private final GameContext ctx;

    public PoliceCheckAction(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void execute() {
        boolean hasIllegal =
            ctx.getInventory().getAmount(ItemRegistry.get("grass")) > 0 ||
                ctx.getInventory().getAmount(ItemRegistry.get("joint")) > 0 ||
                ctx.getInventory().getAmount(ItemRegistry.get("vape")) > 0;

        if (hasIllegal) {
            ctx.getInventory().removeItem(ItemRegistry.get("grass"), 9999);
            ctx.getInventory().removeItem(ItemRegistry.get("joint"), 9999);
            ctx.getInventory().removeItem(ItemRegistry.get("vape"), 9999);
            ctx.ui.getGameScreen().showInfoMessage(
                Assets.messages.get("message.police.stuffLost"), 1.5f
            );
        } else {
            ctx.ui.getGameScreen().showInfoMessage(
                Assets.messages.get("message.police.checkPassed"), 1.5f
            );
        }
    }
}
