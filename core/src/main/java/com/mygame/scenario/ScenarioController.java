package com.mygame.scenario;

import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.game.GameStateManager;
import com.mygame.ui.UIManager;

public class ScenarioController {
    public static void init(GameStateManager gsm, NpcManager npcManager, UIManager uiManager, ItemManager itemManager, Player player){
        new BossDeliveryScenario(player, npcManager, uiManager, gsm).init();
        new PfandAutomatScenario(uiManager, itemManager, player).init();
        new PoliceChaseScenario(gsm, npcManager,uiManager, player).init();
    }
}
