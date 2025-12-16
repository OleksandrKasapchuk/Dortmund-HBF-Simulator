package com.mygame.dialogue.action.custom;

import com.mygame.Assets;
import com.mygame.dialogue.action.ActionContext;
import com.mygame.dialogue.action.DialogueAction;
import com.mygame.dialogue.action.condition.HasItemCondition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.global.TimerManager;
import com.mygame.managers.global.audio.SoundManager;

public class ChikitaCraftJointAction implements DialogueAction {
    private final ActionContext ctx;

    public ChikitaCraftJointAction(ActionContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void execute() {
        if (!new HasItemCondition(ctx, "grass", "item.grass.name").check()) return;
        if (!new HasItemCondition(ctx, "pape", "item.pape.name").check()) return;

        ctx.inventory.removeItem(ItemRegistry.get("grass"), 1);
        ctx.inventory.removeItem(ItemRegistry.get("pape"), 1);
        ctx.player.setMovementLocked(true);
        SoundManager.playSound(Assets.getSound("kosyakSound"));
        TimerManager.setAction(() -> {
            ctx.inventory.addItemAndNotify(ItemRegistry.get("joint"), 1);
            ctx.player.setMovementLocked(false);
        }, 1f);
    }
}
