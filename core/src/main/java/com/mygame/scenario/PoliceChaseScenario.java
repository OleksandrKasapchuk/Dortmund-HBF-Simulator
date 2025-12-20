package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameStateManager;
import com.mygame.managers.TimerManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;
import com.mygame.world.transition.Transition;

public class PoliceChaseScenario implements Scenario {
    private NpcManager npcManager;
    private UIManager uiManager;
    private Player player;
    private GameStateManager gsm;

    public PoliceChaseScenario(GameStateManager gsm, NpcManager npcManager, UIManager uiManager, Player player){
        this.npcManager = npcManager;
        this.uiManager = uiManager;
        this.player = player;
        this.gsm = gsm;
    }


    public void init() {
        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> {
            if (event.npcId().equals("police_chase")) {
                handlePolice();
            }
        });
    }

    public void handlePolice(){
        Police police = npcManager.getSummonedPolice();
        if (police == null) return;

        // Update police logic and check if a transition is triggered
        Transition policeTransition = police.update(player);

        if (policeTransition != null) {
            TimerManager.setAction(() -> {
                World newWorld = WorldManager.getWorld(policeTransition.targetWorldId);
                if (newWorld != null) {
                    // Якщо поліція увійшла в перехід, перемістіть її в новий світ
                    npcManager.moveSummonedPoliceToNewWorld(newWorld);
                    // Встановіть позицію поліцейського поруч із гравцем у новому світі
                    police.setX(policeTransition.targetX);
                    police.setY(policeTransition.targetY + 100);
                    police.setState(Police.PoliceState.CHASING);
                }
            }, 1.5f);

        }

        // Handle state changes (escaped, caught)
        switch (police.getState()) {
            case ESCAPED -> {
                MusicManager.playMusic(Assets.getMusic("backMusic1")); // Change music back
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.ranAway"), 1.5f);
                npcManager.kill(police);

                // Reward player for escaping
                Runnable rewardAction = () -> {
                    player.getInventory().addItemAndNotify(ItemRegistry.get("money"),50);
                    npcManager.getBoss().setDialogue(new DialogueNode("message.boss.whatDoYouWant"));
                };

                npcManager.getBoss().setDialogue(new DialogueNode(rewardAction, false, "message.boss.wellDone"));
            }
            case CAUGHT -> gsm.playerDied(); // Player dies if caught
        }
}}
