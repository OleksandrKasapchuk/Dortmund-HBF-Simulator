package com.mygame.dialogue.action.custom;

import com.mygame.assets.Assets;
import com.mygame.dialogue.action.ActionContext;
import com.mygame.dialogue.action.DialogueAction;
import com.mygame.entity.item.ItemRegistry;

public class PoliceCheckAction implements DialogueAction {

    private final ActionContext ctx;

    public PoliceCheckAction(ActionContext ctx) {
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
            ctx.getGameUI().showInfoMessage(
                Assets.bundle.get("message.police.stuffLost"), 1.5f
            );
        } else {
            ctx.getGameUI().showInfoMessage(
                Assets.bundle.get("message.police.checkPassed"), 1.5f
            );
        }
    }
}
