package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.dialogue.DialogueNode;
import com.mygame.dialogue.action.SetDialogueAction;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.managers.TimerManager;

public class BossDeliveryScenario {

    private GameContext ctx;

    private boolean bossFailureTriggered = false;

    public BossDeliveryScenario(GameContext ctx) {
        this.ctx = ctx;
    }

    public void init() {
        // Логіка провалу: якщо гравець викинув або втратив траву під час квесту
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> {
            if (QuestManager.hasQuest("delivery") && event.itemId().equals("grass") && event.newAmount() < 1000) {
                handleBossFail();
            }
        });

        // Логіка успіху: взаємодія з кущем
        EventBus.subscribe(Events.InteractionEvent.class, event -> {
            if (QuestManager.hasQuest("delivery") && event.item().getType().getKey().equals("bush")) {
                if (ctx.player.getInventory().getAmount(ItemRegistry.get("grass")) >= 1000) {
                    triggerQuestSuccess();
                } else {
                    ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.not_enough_grass"), 2f);
                }
            }
        });
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
        ctx.player.getInventory().removeItem(ItemRegistry.get("grass"), 1000);
        QuestManager.removeQuest("delivery");
        SoundManager.playSound(Assets.getSound("bushSound"));
        ctx.player.setMovementLocked(true);

        TimerManager.setAction(() -> {
            ctx.npcManager.callPolice();
            Police police = ctx.npcManager.getSummonedPolice();
            if (police == null) return;

            DialogueNode caughtNode = new DialogueNode(ctx.gsm::playerDied, true, "message.boss.chase.caught");

            Runnable chaseAction = () -> {
                police.startChase(ctx.player);
                ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.chase.run"), 2f);
                MusicManager.playMusic(Assets.getMusic("backMusic4"));
                police.setDialogue(caughtNode);
            };

            police.setDialogue(new DialogueNode(chaseAction, true, "message.boss.dialogue.beforeChase.1", "message.boss.dialogue.beforeChase.2"));
            ctx.ui.getDialogueManager().startForcedDialogue(police);
        }, 2f);
    }
}
