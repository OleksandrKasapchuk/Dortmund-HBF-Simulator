package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.dialogue.DialogueNode;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
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
    private boolean completed;

    public PoliceChaseScenario(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void init() {
        // Слухаємо завершення квесту
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if (event.questId().equals("delivery")) {
                completed = true;
            }
        });

        // Слухаємо зміну стану поліції (нова логіка на лістенерах)
        EventBus.subscribe(Events.PoliceStateChangedEvent.class, event -> {
            if (!completed) return;
            Police police = ctx.npcManager.getSummonedPolice();
            if (police == null) return;

            switch (event.newState()) {
                case CAUGHT -> // Якщо спіймали — запускаємо примусовий діалог затримання
                    ctx.ui.getDialogueManager().startForcedDialogue(police);
                case ESCAPED -> {
                    // Якщо втекли — повертаємо музику, даємо нагороду і вбиваємо сутність копа
                    MusicManager.playMusic(Assets.getMusic("backMusic1"));
                    ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.ranAway"), 1.5f);

                    // Нагорода за втечу
                    ctx.player.getInventory().addItemAndNotify(ItemRegistry.get("money"), 50);

                    NPC boss = ctx.npcManager.getBoss();
                    if (boss != null) {
                        Runnable rewardAction = () -> boss.setDialogue(DialogueRegistry.getDialogue("boss", "after"));
                        boss.setDialogue(new DialogueNode(rewardAction, false, "message.boss.wellDone"));
                    }

                    ctx.npcManager.kill(police);
                }
                case CHASING -> {
                    // Можна додати специфічну логіку при початку погоні, якщо треба
                }
            }
        });
    }

    @Override
    public void update() {
        if (!completed) return;
        handlePolice();
    }

    private void handlePolice() {
        Police police = ctx.npcManager.getSummonedPolice();
        if (police == null) return;

        // Поліція оновлює свою позицію та перевіряє переходи
        Transition policeTransition = police.update(ctx.player);

        if (policeTransition != null) {
            TimerManager.setAction(() -> {
                World newWorld = WorldManager.getWorld(policeTransition.targetWorldId);
                if (newWorld != null) {
                    ctx.npcManager.moveSummonedPoliceToNewWorld(newWorld);
                    police.setX(policeTransition.targetX);
                    police.setY(policeTransition.targetY + 100);
                    police.setState(Police.PoliceState.CHASING);
                }
            }, 1.5f);
        }
    }
}
