package com.mygame.scenario;

import com.badlogic.gdx.Gdx;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.GameStateManager;
import com.mygame.managers.TimerManager;
import com.mygame.world.World;
import com.mygame.world.zone.TransitionZone;

public class PoliceChaseScenario implements Scenario {
    private GameContext ctx;
    private boolean completed;
    private boolean musicRestored = false;

    public PoliceChaseScenario(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void init() {
        // Слухаємо завершення квесту доставки
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if (event.questId().equals("delivery")) {
                Gdx.app.log("PoliceChaseScenario", "Delivery completed, preparing for chase.");
                completed = true;
            }
            // Скидаємо стан сценарію, коли сама погоня завершена
            if (event.questId().equals("chase")) {
                Gdx.app.log("PoliceChaseScenario", "Chase quest completed, cleaning up scenario.");
                completed = false;
                musicRestored = false;
            }
        });

        // Слухаємо зміну стану поліції
        EventBus.subscribe(Events.PoliceStateChangedEvent.class, event -> {
            if (!completed) return;

            switch (event.newState()) {
                case CAUGHT -> EventBus.fire(new Events.ActionRequestEvent("act.npc.summoned_police.force_dialogue"));
                case ESCAPED -> EventBus.fire(new Events.ActionRequestEvent("act.quest.chase.complete"));
            }
        });
    }

    @Override
    public void update() {
        if (ctx.gsm.getState() != GameStateManager.GameState.PLAYING) return;

        // Якщо квест уже завершений — нічого не робимо
        if (ctx.questManager.getQuest("chase") != null && ctx.questManager.getQuest("chase").isCompleted()) {
            if (completed) {
                completed = false;
                musicRestored = false;
            }
            return;
        }

        // Якщо гра завантажена, перевіряємо чи активний квест погоні
        if (!completed && ctx.questManager.hasQuest("chase")) {
            completed = true;
            Gdx.app.log("PoliceChaseScenario", "Chase quest detected active, restoring state.");
        }

        if (!completed) return;

        // Відновлення музики один раз після завантаження
        if (!musicRestored) {
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) {
                Gdx.app.log("PoliceChaseScenario", "Restoring chase music and UI.");
                EventBus.fire(new Events.ActionRequestEvent("act.quest.chase.restore_ui"));
                musicRestored = true;
            }
        }

        handlePolice();
    }

    private void handlePolice() {
        Police police = ctx.npcManager.getSummonedPolice();
        if (police == null) return;

        TransitionZone policeTransition = police.update(ctx.player);
        if (policeTransition != null) {
            TimerManager.setAction(() -> {
                World newWorld = ctx.worldManager.getWorld(policeTransition.targetWorldId);
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
