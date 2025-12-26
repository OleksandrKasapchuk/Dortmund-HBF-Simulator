package com.mygame.scenario;


import com.badlogic.gdx.Gdx;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;


public class GrassSeedTableScenario implements Scenario {

    private GameContext ctx;

    public GrassSeedTableScenario(GameContext ctx){
        this.ctx = ctx;
    }

    @Override
    public void init() {}

    @Override
    public void update(){
        Item table = ctx.itemManager.getTable();
        if (table == null) return;

        table.updateCooldown(Gdx.graphics.getDeltaTime());

        if (table.isPlayerNear(ctx.player)) {
            if (ctx.ui.isInteractPressed()) {
                if(ctx.player.getInventory().hasItem(ItemRegistry.get("grass_seed"))){
                    ctx.player.getInventory().removeItem(ItemRegistry.get("grass_seed"),1);
                    triggerTable();
                } else {
                    ctx.ui.showNotEnough(("item.grass_seed.name"));
                }
            }
        }
    }

    @Override
    public void draw() {
        Item table = ctx.itemManager.getTable();
        if (table == null || table.getWorld() != WorldManager.getCurrentWorld()) return;

        if (table.isPlayerNear(ctx.player) && ctx.player.getInventory().hasItem(ItemRegistry.get("grass_seed"))) {
            ctx.ui.drawText(Assets.ui.get("interact.npc"), table.getCenterX(), table.getCenterY());
        }
    }

    private void triggerTable(){
        SoundManager.playSound(Assets.getSound("bush"));

        // Timer to give money after 1.9 seconds
        TimerManager.setAction(() ->
            ctx.ui.getGameUI().showInfoMessage("You planted grass", 1), 1.9f);
    }
}
