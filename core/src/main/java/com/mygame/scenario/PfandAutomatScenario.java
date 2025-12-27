package com.mygame.scenario;

import com.badlogic.gdx.Gdx;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;

public class PfandAutomatScenario implements Scenario {

    private GameContext ctx;

    public PfandAutomatScenario(GameContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void init() {
        Item pfandAutomat = ctx.itemManager.getPfandAutomat();
        if (pfandAutomat != null) {
            pfandAutomat.setOnInteract(player -> {
                if (!pfandAutomat.canInteract()) return;

                if (player.getInventory().getAmount(ItemRegistry.get("pfand")) >= 1) {
                    player.getInventory().removeItem(ItemRegistry.get("pfand"), 1);
                    triggerPfandAutomat(pfandAutomat);
                } else {
                    ctx.ui.showNotEnough("item.pfand.name");
                }
            });
        }
    }

    @Override
    public void update(){
        Item pfandAutomat = ctx.itemManager.getPfandAutomat();
        if (pfandAutomat != null) {
            pfandAutomat.updateCooldown(Gdx.graphics.getDeltaTime());
        }
    }

    private void triggerPfandAutomat(Item pfandAutomat){
        SoundManager.playSound(Assets.getSound("pfandAutomat"));

        // Timer to give money after 1.9 seconds
        TimerManager.setAction(() ->
            ctx.player.getInventory().addItemAndNotify(ItemRegistry.get("money"), 1), 1.9f);

        pfandAutomat.startCooldown(1.9f);
    }
}
