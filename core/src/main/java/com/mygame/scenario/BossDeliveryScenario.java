package com.mygame.scenario;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameStateManager;
import com.mygame.managers.QuestManager;
import com.mygame.managers.TimerManager;
import com.mygame.ui.UIManager;

public class BossDeliveryScenario {

    private final Player player;
    private final NpcManager npcManager;
    private final UIManager uiManager;
    private final GameStateManager gameStateManager;

    private boolean bossFailureTriggered = false;

    public BossDeliveryScenario(Player player, NpcManager npcManager, UIManager uiManager, GameStateManager gameStateManager) {
        this.player = player;
        this.npcManager = npcManager;
        this.uiManager = uiManager;
        this.gameStateManager = gameStateManager;
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
                if (player.getInventory().getAmount(ItemRegistry.get("grass")) >= 1000) {
                    triggerQuestSuccess();
                } else {
                    uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.not_enough_grass"), 2f);
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
        NPC boss = npcManager.getBoss();
        if (boss == null) return;

        DialogueNode failureNode = new DialogueNode(() -> {
            gameStateManager.playerDied();
            SoundManager.playSound(Assets.getSound("gunShot"));
        }, true, "message.boss.failure.1", "message.boss.failure.2", "message.boss.failure.3");

        boss.setDialogue(failureNode);

        TimerManager.setAction(() -> {
            boss.setX(player.getX() - 100);
            boss.setY(player.getY());
            uiManager.getDialogueManager().startForcedDialogue(boss);
        }, 2f);
    }

    private void triggerQuestSuccess() {
        player.getInventory().removeItem(ItemRegistry.get("grass"), 1000);
        QuestManager.removeQuest("delivery");
        SoundManager.playSound(Assets.getSound("bushSound"));
        player.setMovementLocked(true);

        TimerManager.setAction(() -> {
            npcManager.callPolice();
            Police police = npcManager.getSummonedPolice();
            if (police == null) return;

            DialogueNode caughtNode = new DialogueNode(gameStateManager::playerDied, true, "message.boss.chase.caught");

            Runnable chaseAction = () -> {
                police.startChase(player);
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.chase.run"), 2f);
                MusicManager.playMusic(Assets.getMusic("backMusic4"));
                police.setDialogue(caughtNode);
            };

            police.setDialogue(new DialogueNode(chaseAction, true, "message.boss.dialogue.beforeChase.1", "message.boss.dialogue.beforeChase.2"));
            uiManager.getDialogueManager().startForcedDialogue(police);
        }, 2f);
    }
}
