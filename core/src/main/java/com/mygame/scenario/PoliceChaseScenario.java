package com.mygame.scenario;

import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.managers.TimerManager;
import com.mygame.world.World;
import com.mygame.world.transition.Transition;

public class PoliceChaseScenario implements Scenario {
    private GameContext ctx;
    private boolean completed;

    public PoliceChaseScenario(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void init() {
        GameSettings settings = SettingsManager.load();

        // Відновлення погоні зі збереження
        if (settings.policeChaseActive) {
            completed = true;
            EventBus.fire(new Events.ActionRequestEvent("act.npc.callPolice"));
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) {
                police.setX(settings.policeX);
                police.setY(settings.policeY);
                World world = ctx.worldManager.getWorld(settings.policeWorldName);
                if (world != null) {
                    ctx.npcManager.moveSummonedPoliceToNewWorld(world);
                }
                EventBus.fire(new Events.ActionRequestEvent("act.quest.chase.start"));
                EventBus.fire(new Events.ActionRequestEvent("act.quest.chase.restore_ui"));
            }
        }

        // Слухаємо завершення квесту доставки
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if (event.questId().equals("delivery")) completed = true;
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
        if (!completed) return;
        handlePolice();
    }

    private void handlePolice() {
        Police police = ctx.npcManager.getSummonedPolice();
        if (police == null) return;

        Transition policeTransition = police.update(ctx.player);
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
