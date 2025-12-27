package com.mygame.scenario;

import com.badlogic.gdx.Gdx;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;

public class GrassSeedTableScenario implements Scenario {

    private GameContext ctx;

    public GrassSeedTableScenario(GameContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void init() {
        Item table = ctx.itemManager.getTable();
        if (table != null) {
            table.setOnInteract(player -> {
                if (player.getInventory().hasItem(ItemRegistry.get("grass_seed"))) {
                    player.getInventory().removeItem(ItemRegistry.get("grass_seed"), 1);
                    triggerTable();
                } else {
                    ctx.ui.showNotEnough("item.grass_seed.name");
                }
            });
        }
    }

    @Override
    public void update(){
        Item table = ctx.itemManager.getTable();
        if (table != null) {
            table.updateCooldown(Gdx.graphics.getDeltaTime());
        }
    }

    private void triggerTable(){
        SoundManager.playSound(Assets.getSound("bush"));

        TimerManager.setAction(() ->
            ctx.ui.getGameUI().showInfoMessage("You planted grass", 1), 1.9f);
    }
}
