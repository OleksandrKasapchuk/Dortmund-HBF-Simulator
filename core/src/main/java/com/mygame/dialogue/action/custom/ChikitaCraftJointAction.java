package com.mygame.dialogue.action.custom;

import com.mygame.assets.Assets;
import com.mygame.dialogue.action.DialogueAction;
import com.mygame.dialogue.action.condition.HasItemCondition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;
import com.mygame.assets.audio.SoundManager;

public class ChikitaCraftJointAction implements DialogueAction {
    private final GameContext ctx;

    public ChikitaCraftJointAction(GameContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void execute() {
        if (!new HasItemCondition(ctx, "grass", "item.grass.name").check()) return;
        if (!new HasItemCondition(ctx, "pape", "item.pape.name").check()) return;

        ctx.getInventory().removeItem(ItemRegistry.get("grass"), 1);
        ctx.getInventory().removeItem(ItemRegistry.get("pape"), 1);
        ctx.player.setMovementLocked(true);
        SoundManager.playSound(Assets.getSound("kosyakSound"));
        TimerManager.setAction(() -> {
            ctx.getInventory().addItemAndNotify(ItemRegistry.get("joint"), 1);
            ctx.player.setMovementLocked(false);
        }, 1f);
    }
}
