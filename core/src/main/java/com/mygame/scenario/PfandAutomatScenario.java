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
import com.mygame.managers.TimerManager;
import com.mygame.ui.UIManager;
import com.mygame.world.WorldManager;


public class PfandAutomatScenario implements Scenario {

    private UIManager uiManager;
    private Player player;
    private ItemManager itemManager;

    public PfandAutomatScenario(UIManager uiManager, ItemManager itemManager, Player player){
        this.uiManager = uiManager;
        this.player = player;
        this.itemManager = itemManager;
    }

    public void init() {
        EventBus.subscribe(Events.InteractionEvent.class, event -> {
            if (event.item().getType().getKey().equals("pfand_automat")) {
                handlePfandAutomat(Gdx.graphics.getDeltaTime(), itemManager, uiManager, player);
            }
        });
    }

    private void handlePfandAutomat(float delta, ItemManager itemManager, UIManager uiManager, Player player){
        Item pfandAutomat = itemManager.getPfandAutomat();
        if (pfandAutomat == null || pfandAutomat.getWorld() != WorldManager.getCurrentWorld()) return;
        pfandAutomat.updateCooldown(delta);

        if (pfandAutomat.isPlayerNear(player)) {
            if (uiManager.isInteractPressed() && pfandAutomat.canInteract()) {
                if(player.getInventory().getAmount(ItemRegistry.get("pfand")) >= 1){
                    player.getInventory().removeItem(ItemRegistry.get("pfand"),1);
                    triggerPfandAutomat(player, itemManager);
                } else {
                    uiManager.showNotEnough(("item.pfand.name"));
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
