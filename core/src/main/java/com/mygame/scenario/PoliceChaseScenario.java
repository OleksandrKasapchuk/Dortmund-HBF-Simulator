package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.dialogue.action.AddQuestAction;
import com.mygame.dialogue.action.CompleteEventAction;
import com.mygame.dialogue.action.SetDialogueAction;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.managers.TimerManager;
import com.mygame.quest.QuestManager;
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
        GameSettings settings = SettingsManager.load();

        // Відновлення погоні зі збереження
        if (settings.policeChaseActive) {
            completed = true;
            ctx.npcManager.callPolice();
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) {
                police.setX(settings.policeX);
                police.setY(settings.policeY);
                World world = WorldManager.getWorld(settings.policeWorldName);
                if (world != null) {
                    ctx.npcManager.moveSummonedPoliceToNewWorld(world);
                }
                police.startChase(ctx.player);
                MusicManager.playMusic(Assets.getMusic("backMusic4"));
                new SetDialogueAction(ctx, "summoned_police", "caught").execute();
            }
        }

        // Слухаємо завершення квесту
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if (event.questId().equals("delivery")) {
                completed = true;
            }
        });

        // Слухаємо зміну стану поліції
        EventBus.subscribe(Events.PoliceStateChangedEvent.class, event -> {
            if (!completed) return;
            Police police = ctx.npcManager.getSummonedPolice();
            if (police == null) return;

            switch (event.newState()) {
                case CAUGHT ->
                    ctx.ui.getDialogueManager().startForcedDialogue(police);
                case ESCAPED -> {
                    MusicManager.playMusic(Assets.getMusic("backMusic1"));
                    ctx.ui.getGameUI().showInfoMessage(Assets.messages.get("message.generic.ranAway"), 1.5f);

                    NPC boss = ctx.npcManager.getBoss();
                    if (boss != null) {
                        new SetDialogueAction(ctx, "boss", "wellDone").execute();
                        new CompleteEventAction(ctx, "boss_quest_escaped").execute();
                        QuestManager.removeQuest("chase");
                        new AddQuestAction("boss_end",false,0,0).execute();
                    }
                    ctx.npcManager.kill(police);
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
