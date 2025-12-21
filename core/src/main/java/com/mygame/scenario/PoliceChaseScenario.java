package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;
import com.mygame.world.transition.Transition;

public class PoliceChaseScenario implements Scenario {
    private GameContext ctx;

    public PoliceChaseScenario(GameContext ctx){
        this.ctx = ctx;
    }

    public void init() {
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if (event.questId().equals("delivery")) {
                handlePolice();
            }
        });
    }

    public void handlePolice(){
        Police police = ctx.npcManager.getSummonedPolice();
        if (police == null) return;

        // Update police logic and check if a transition is triggered
        Transition policeTransition = police.update(ctx.player);

        if (policeTransition != null) {
            TimerManager.setAction(() -> {
                World newWorld = WorldManager.getWorld(policeTransition.targetWorldId);
                if (newWorld != null) {
                    // Якщо поліція увійшла в перехід, перемістіть її в новий світ
                    ctx.npcManager.moveSummonedPoliceToNewWorld(newWorld);
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
                ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.ranAway"), 1.5f);
                ctx.npcManager.kill(police);

                // Reward player for escaping
                Runnable rewardAction = () -> {
                    ctx.player.getInventory().addItemAndNotify(ItemRegistry.get("money"),50);
                    ctx.npcManager.getBoss().setDialogue(new DialogueNode("message.boss.whatDoYouWant"));
                };

                ctx.npcManager.getBoss().setDialogue(new DialogueNode(rewardAction, false, "message.boss.wellDone"));
            }
            case CAUGHT -> ctx.gsm.playerDied(); // Player dies if caught
        }
}}
