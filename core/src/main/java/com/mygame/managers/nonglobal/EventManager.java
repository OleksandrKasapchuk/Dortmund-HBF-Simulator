package com.mygame.managers.nonglobal;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.Item;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.Police;
import com.mygame.entity.item.ItemRegistry;
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
        // Hint for bush (quest hide spot)
        if (QuestManager.hasQuest("Big delivery") && itemManager.getBush().isPlayerNear(player)) {
            font.draw(batch, "Press E to hide your kg", itemManager.getBush().getX(), itemManager.getBush().getY());
        }

        // Hint for Pfand Automat
        Item pfandAutomat = itemManager.getPfandAutomat();
        if (pfandAutomat.isPlayerNear(player)) {
            font.draw(batch,"Press E to change your pfand for money",
                pfandAutomat.getX(),
                pfandAutomat.getY() + 150);
        }
    }

    // --- Handle boss failure event ---
    private void handleBossFail() {
        if (!QuestManager.hasQuest("Big delivery")) return;
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
            "You are not doing the task!",
            "I told you to hide the grass, not lose it.",
            "Now you will regret this...");

        boss.setDialogue(new Dialogue(failureNode));

        // Set timer to start dialogue after 2 seconds
        TimerManager.setAction(() -> {
            boss.setX(player.getX() - 100);
            boss.setY(player.getY());
            uiManager.getDialogueManager().startForcedDialogue(boss);
        }, 2f);
    }

    // --- Handle boss quest success ---
    private void handleBossSuccess() {
        if (!QuestManager.hasQuest("Big delivery")) return;
        if (!itemManager.getBush().isPlayerNear(player)) return;

        if (uiManager.isInteractPressed()) {
            triggerQuestSuccess();
        }
    }

    private void triggerQuestSuccess() {
        // Remove items and quest
        player.getInventory().removeItem(ItemRegistry.get("grass"), 1000);
        QuestManager.removeQuest("Big delivery");
        SoundManager.playSound(Assets.bushSound);
        player.setMovementLocked(true);

        // Call police after 2 seconds
        TimerManager.setAction(() -> {
            npcManager.callPolice();
            Police police = npcManager.getPolice1();
            if (police == null) return;

            // Dialogue for being caught
            DialogueNode caughtNode = new DialogueNode(gameStateManager::playerDied, "You got caught!");

            Runnable chaseAction = () -> {
                police.startChase();                           // Start police chase
                uiManager.getGameUI().showInfoMessage("RUN", 2f); // Show warning
                MusicManager.playMusic(Assets.backMusic4);    // Change music to chase
                police.setDialogue(new Dialogue(caughtNode)); // Assign caught dialogue
            };

            // Dialogue before chase
            police.setDialogue(new Dialogue(new DialogueNode(chaseAction, "What are you doing?", "Stop right there!")));
            uiManager.getDialogueManager().startForcedDialogue(police);
        }, 2f);
    }

    // --- Handle Pfand Automat interaction ---
    public void handlePfandAutomat(float delta){
        Item pfandAutomat = itemManager.getPfandAutomat();
        pfandAutomat.updateCooldown(delta);

        if (pfandAutomat.isPlayerNear(player)) {
            if (uiManager.isInteractPressed() && pfandAutomat.canInteract()) {
                if(player.getInventory().removeItem(ItemRegistry.get("pfand"),1)){
                    triggerPfandAutomat();
                } else {
                    uiManager.getGameUI().showInfoMessage("You don't have enough pfand",1f);
                }
            }
        }
    }

    public void triggerPfandAutomat(){
        SoundManager.playSound(Assets.pfandAutomatSound);

        // Timer to give money after 1.9 seconds
        TimerManager.setAction(() -> {
            SoundManager.playSound(Assets.moneySound);
            uiManager.getGameUI().showInfoMessage("You got 1 money for pfand",1f);
            player.getInventory().addItem(ItemRegistry.get("money"),1);
        }, 1.9f);

        itemManager.getPfandAutomat().startCooldown(1.9f);
    }

    // --- Handle police behavior ---
    public void handlePolice(){
        if (npcManager.getPolice1() == null) return;

        Police police = npcManager.getPolice1();
        police.update(player);

        switch (police.getState()) {
            case ESCAPED -> {
                MusicManager.playMusic(Assets.backMusic1); // Change music back
                uiManager.getGameUI().showInfoMessage("You ran away from the police", 1.5f);
                npcManager.kill(police);

                // Reward player for escaping
                Runnable rewardAction = () -> {
                    player.getInventory().addItem(ItemRegistry.get("money"), 50);
                    uiManager.getGameUI().showInfoMessage("You got 50 money", 1.5f);
                    npcManager.getBoss().setDialogue(new Dialogue(new DialogueNode("What do you want from me?")));
                };

                npcManager.getBoss().setDialogue(new Dialogue(new DialogueNode(rewardAction, "Oh, you've managed this.", "Well done!")));
            }
            case CAUGHT -> gameStateManager.playerDied(); // Player dies if caught
        }
    }
}
