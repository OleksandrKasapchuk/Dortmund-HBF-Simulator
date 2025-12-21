package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.dialogue.action.SetDialogueAction;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;

public class BossDeliveryScenario implements Scenario {

    private GameContext ctx;
    private boolean bossFailureTriggered = false;

    public BossDeliveryScenario(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void init() {
        // Логіка провалу: якщо гравець викинув або втратив траву під час квесту
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> {
            if (QuestManager.hasQuest("delivery") && event.itemId().equals("grass") && event.newAmount() < 1000) {
                handleBossFail();
            }
        });
    }

    @Override
    public void update() {
        if (!QuestManager.hasQuest("delivery")) return;

        Item bush = ctx.itemManager.getBush();
        if (bush == null || bush.getWorld() != WorldManager.getCurrentWorld()) return;

        if (bush.isPlayerNear(ctx.player, 250)) {
            if (ctx.ui.isInteractPressed()) {
                if (ctx.player.getInventory().getAmount(ItemRegistry.get("grass")) >= 1000) {
                    triggerQuestSuccess();
                } else {
                    ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.not_enough_grass"), 2f);
                }
            }
        }
    }

    @Override
    public void draw() {
        if (!QuestManager.hasQuest("delivery")) return;

        Item bush = ctx.itemManager.getBush();
        if (bush == null || bush.getWorld() != WorldManager.getCurrentWorld()) return;

        if (bush.isPlayerNear(ctx.player, 250)) {
            ctx.ui.drawText(Assets.bundle.get("interact.bush"), bush.getCenterX(), bush.getCenterY());
        }
    }

    private void handleBossFail() {
        if (bossFailureTriggered) return;
        bossFailureTriggered = true;
        triggerBossFailure();
    }

    private void triggerBossFailure() {
        NPC boss = ctx.npcManager.getBoss();
        new SetDialogueAction(ctx, "boss", "failure").execute();

        TimerManager.setAction(() -> {
            boss.setWorld(ctx.player.getWorld());
            boss.setX(ctx.player.getX() - 100);
            boss.setY(ctx.player.getY());
            ctx.ui.getDialogueManager().startForcedDialogue(boss);
        }, 2f);
    }

    private void triggerQuestSuccess() {
        QuestManager.removeQuest("delivery");
        ctx.player.getInventory().removeItem(ItemRegistry.get("grass"), 1000);
        SoundManager.playSound(Assets.getSound("bushSound"));
        ctx.player.setMovementLocked(true);

        TimerManager.setAction(() -> {
            ctx.npcManager.callPolice();

            EventBus.fire(new Events.QuestCompletedEvent("delivery"));

            new SetDialogueAction(ctx, "summoned_police", "beforeChase").execute();
            ctx.ui.getDialogueManager().startForcedDialogue(ctx.npcManager.getSummonedPolice());
        }, 2f);
    }
}
