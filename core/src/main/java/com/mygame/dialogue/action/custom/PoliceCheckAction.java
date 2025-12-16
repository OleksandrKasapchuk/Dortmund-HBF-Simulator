package com.mygame.dialogue.action.custom;

import com.mygame.Assets;
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
            ctx.inventory.getAmount(ItemRegistry.get("grass")) > 0 ||
                ctx.inventory.getAmount(ItemRegistry.get("joint")) > 0 ||
                ctx.inventory.getAmount(ItemRegistry.get("vape")) > 0;

        if (hasIllegal) {
            ctx.inventory.removeItem(ItemRegistry.get("grass"), 9999);
            ctx.inventory.removeItem(ItemRegistry.get("joint"), 9999);
            ctx.inventory.removeItem(ItemRegistry.get("vape"), 9999);
            ctx.gameUI.showInfoMessage(
                Assets.bundle.get("message.police.stuffLost"), 1.5f
            );
        } else {
            ctx.gameUI.showInfoMessage(
                Assets.bundle.get("message.police.checkPassed"), 1.5f
            );
        }
    }
}
