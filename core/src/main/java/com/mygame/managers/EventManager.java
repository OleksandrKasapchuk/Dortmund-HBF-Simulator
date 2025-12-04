package com.mygame.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.entity.npc.Police;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.game.GameStateManager;
import com.mygame.world.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.TimerManager;
import com.mygame.ui.UIManager;

/**
 * Handles all in-game events, including boss quest success/failure,
 * Pfand Automat interaction, and police encounters.
 */
public class EventManager {

    private Player player;
    private NpcManager npcManager;
    private UIManager uiManager;
    private ItemManager itemManager;
    private BitmapFont font;
    private SpriteBatch batch;

    private boolean bossFailureTriggered = false; // Flag to ensure boss failure event triggers only once

    private GameStateManager gameStateManager;

    // --- Constructor ---
    public EventManager(Player player, NpcManager npcManager, UIManager uiManager, ItemManager itemManager, SpriteBatch batch, BitmapFont font,GameStateManager gameStateManager) {
        this.player = player;
        this.npcManager = npcManager;
        this.uiManager = uiManager;
        this.itemManager = itemManager;
        this.batch = batch;
        this.font = font;
        this.gameStateManager = gameStateManager;
    }

    // --- Main update method called each frame ---
    public void update(float delta) {
        handleBossFail();       // Check if player fails boss quest
        handleBossSuccess();    // Check if player completes boss quest
        handlePfandAutomat(delta); // Update Pfand Automat interaction
        handlePolice();         // Update police behavior
    }

    // --- Render interaction hints for the player ---
    public void render() {
        Item bush = itemManager.getBush();
        if (bush != null && QuestManager.hasQuest("delivery") && bush.isPlayerNear(player) && bush.getWorld() == WorldManager.getCurrentWorld()) {
            font.draw(batch, Assets.bundle.get("world.pressEToHideKg"), bush.getX(), bush.getY());
        }

        Item pfandAutomat = itemManager.getPfandAutomat();
        if (pfandAutomat != null && pfandAutomat.isPlayerNear(player) && pfandAutomat.getWorld() == WorldManager.getCurrentWorld()) {
            font.draw(batch, Assets.bundle.get("interact.pfandAutomat"),
                pfandAutomat.getX(),
                pfandAutomat.getY() + 150);
        }
    }

    // --- Handle boss failure event ---
    private void handleBossFail() {
        if (!QuestManager.hasQuest("delivery")) return;
        if (bossFailureTriggered) return;
        if (player.getInventory().getAmount(ItemRegistry.get("grass")) >= 1000) return;

        bossFailureTriggered = true;
        triggerBossFailure();
    }

    private void triggerBossFailure() {
        NPC boss = npcManager.getBoss();
        if (boss == null) return;

        // Create boss failure dialogue
        DialogueNode failureNode = new DialogueNode(() -> {
            gameStateManager.playerDied(); // Kill player
            SoundManager.playSound(Assets.gunShot); // Play gunshot sound
        },
            Assets.bundle.get("message.boss.failure.1"),
            Assets.bundle.get("message.boss.failure.2"),
            Assets.bundle.get("message.boss.failure.3"));

        boss.setDialogue(failureNode);

        // Set timer to start dialogue after 2 seconds
        TimerManager.setAction(() -> {
            boss.setX(player.getX() - 100);
            boss.setY(player.getY());
            uiManager.getDialogueManager().startForcedDialogue(boss);
        }, 2f);
    }

    // --- Handle boss quest success ---
    private void handleBossSuccess() {
        if (!QuestManager.hasQuest("delivery")) return;
        Item bush = itemManager.getBush();
        if (bush == null) return;
        if (!bush.isPlayerNear(player)) return;

        if (uiManager.isInteractPressed() && bush.getWorld() ==  WorldManager.getCurrentWorld()) {
            triggerQuestSuccess();
        }
    }

    private void triggerQuestSuccess() {
        // Remove items and quest
        player.getInventory().removeItem(ItemRegistry.get("grass"), 1000);
        QuestManager.removeQuest("delivery");
        SoundManager.playSound(Assets.bushSound);
        player.setMovementLocked(true);

        // Call police after 2 seconds
        TimerManager.setAction(() -> {
            npcManager.callPolice();
            Police police = npcManager.getSummonedPolice();
            if (police == null) return;

            // Dialogue for being caught
            DialogueNode caughtNode = new DialogueNode(gameStateManager::playerDied, Assets.bundle.get("message.boss.chase.caught"));

            Runnable chaseAction = () -> {
                police.startChase();                           // Start police chase
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.chase.run"), 2f); // Show warning
                MusicManager.playMusic(Assets.backMusic4);    // Change music to chase
                police.setDialogue(caughtNode); // Assign caught dialogue
            };

            // Dialogue before chase
            police.setDialogue(new DialogueNode(chaseAction, Assets.bundle.get("message.boss.dialogue.beforeChase.1"), Assets.bundle.get("message.boss.dialogue.beforeChase.2")));
            uiManager.getDialogueManager().startForcedDialogue(police);
        }, 2f);
    }

    // --- Handle Pfand Automat interaction ---
    public void handlePfandAutomat(float delta){
        Item pfandAutomat = itemManager.getPfandAutomat();
        if (pfandAutomat == null || pfandAutomat.getWorld() != WorldManager.getCurrentWorld()) return;
        pfandAutomat.updateCooldown(delta);

        if (pfandAutomat.isPlayerNear(player)) {
            if (uiManager.isInteractPressed() && pfandAutomat.canInteract()) {
                if(player.getInventory().removeItem(ItemRegistry.get("pfand"),1)){
                    triggerPfandAutomat();
                } else {
                    uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.notEnoughPfand"),1f);
                }
            }
        }
    }

    public void triggerPfandAutomat(){
        SoundManager.playSound(Assets.pfandAutomatSound);

        // Timer to give money after 1.9 seconds
        TimerManager.setAction(() -> {
            player.addMoney(1);
            uiManager.showEarned(1, Assets.bundle.get("item.money.name"));
        }, 1.9f);

        itemManager.getPfandAutomat().startCooldown(1.9f);
    }

    // --- Handle police behavior ---
    public void handlePolice(){
        if (npcManager.getSummonedPolice() == null) return;

        Police police = npcManager.getSummonedPolice();
        police.update(player);

        switch (police.getState()) {
            case ESCAPED -> {
                MusicManager.playMusic(Assets.backMusic1); // Change music back
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.ranAway"), 1.5f);
                npcManager.kill(police);

                // Reward player for escaping
                Runnable rewardAction = () -> {
                    player.addMoney(50);
                    uiManager.showEarned(50, Assets.bundle.get("item.money.name"));
                    npcManager.getBoss().setDialogue(new DialogueNode(Assets.bundle.get("message.boss.whatDoYouWant")));
                };

                npcManager.getBoss().setDialogue(new DialogueNode(rewardAction, Assets.bundle.get("message.boss.wellDone")));
            }
            case CAUGHT -> gameStateManager.playerDied(); // Player dies if caught
        }
    }
}
