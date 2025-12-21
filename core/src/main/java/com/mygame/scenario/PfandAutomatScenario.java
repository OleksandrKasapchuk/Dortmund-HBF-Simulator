package com.mygame.scenario;

import com.badlogic.gdx.Gdx;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;


public class PfandAutomatScenario implements Scenario {

    private GameContext ctx;


    public PfandAutomatScenario(GameContext ctx){
        this.ctx = ctx;
    }

    public void init() {
        EventBus.subscribe(Events.InteractionEvent.class, event -> {
            if (event.item().getType().getKey().equals("pfand_automat")) {
                handlePfandAutomat();
            }
        });
    }

    private void handlePfandAutomat(){
        Item pfandAutomat = ctx.itemManager.getPfandAutomat();
        if (pfandAutomat == null || pfandAutomat.getWorld() != WorldManager.getCurrentWorld()) return;
        pfandAutomat.updateCooldown(Gdx.graphics.getDeltaTime());

        if (pfandAutomat.isPlayerNear(ctx.player)) {
            if (ctx.ui.isInteractPressed() && pfandAutomat.canInteract()) {
                if(ctx.player.getInventory().getAmount(ItemRegistry.get("pfand")) >= 1){
                    ctx.player.getInventory().removeItem(ItemRegistry.get("pfand"),1);
                    triggerPfandAutomat(ctx.player, ctx.itemManager);
                } else {
                    ctx.ui.showNotEnough(("item.pfand.name"));
                }
            }
        }
    }

    private void triggerPfandAutomat(Player player, ItemManager itemManager){
        SoundManager.playSound(Assets.getSound("pfandAutomatSound"));

        // Timer to give money after 1.9 seconds
        TimerManager.setAction(() ->
            player.getInventory().addItemAndNotify(ItemRegistry.get("money"),1), 1.9f);

        itemManager.getPfandAutomat().startCooldown(1.9f);
    }
}
