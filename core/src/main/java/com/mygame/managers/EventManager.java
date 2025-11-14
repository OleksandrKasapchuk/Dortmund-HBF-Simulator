package com.mygame.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.Main;
import com.mygame.entity.Item;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.Police;
import com.mygame.managers.audio.MusicManager;
import com.mygame.managers.audio.SoundManager;
import com.mygame.ui.UIManager;

public class EventManager {
    private Player player;
    private NpcManager npcManager;
    private UIManager uiManager;
    private ItemManager itemManager;
    private BitmapFont font;
    private SpriteBatch batch;
    private boolean bossFailureTriggered = false;

    public EventManager(Player player, NpcManager npcManager, UIManager uiManager, ItemManager itemManager, SpriteBatch batch, BitmapFont font) {
        this.player = player;
        this.npcManager = npcManager;
        this.uiManager = uiManager;
        this.itemManager = itemManager;
        this.batch = batch;
        this.font = font;
    }

    public void update(float delta) {
        handleBossFail();
        handleBossSuccess();
        handlePfandAutomat(delta);
        handlePolice();
    }

    private void handleBossFail() {
        if (!QuestManager.hasQuest("Big delivery")) return;
        if (bossFailureTriggered) return;
        if (player.getInventory().getAmount("grass") >= 1000) return;

        bossFailureTriggered = true;
        triggerBossFailure();
    }

    private void triggerBossFailure() {
        NPC boss = npcManager.getBoss();
        if (boss == null) return;

        DialogueNode failureNode = new DialogueNode(() -> {
            Main.getGameStateManager().playerDied();
            SoundManager.playSound(Assets.gunShot);
        },
            "You are not doing the task!",
            "I told you to hide the grass, not lose it.",
            "Now you will regret this...");

        boss.setDialogue(new Dialogue(failureNode));

        TimerManager.setAction(() -> {
            boss.setX(player.getX() - 100);
            boss.setY(player.getY());
            uiManager.getDialogueManager().startForcedDialogue(boss);
        }, 2f);
    }

    private void handleBossSuccess() {
        if (!QuestManager.hasQuest("Big delivery")) return;

        if (!itemManager.getBush().isPlayerNear(player)) return;

        font.draw(batch, "Press E to hide your kg", itemManager.getBush().getX(), itemManager.getBush().getY());

        if (uiManager.isInteractPressed()) {triggerQuestSuccess();}
    }

    private void triggerQuestSuccess() {
        player.getInventory().removeItem("grass", 1000);
        QuestManager.removeQuest("Big delivery");
        SoundManager.playSound(Assets.bushSound);
        player.setMovementLocked(true);

        TimerManager.setAction(() -> {
            npcManager.callPolice();
            Police police = npcManager.getPolice1();
            if (police == null) return;

            DialogueNode caughtNode = new DialogueNode(Main.getGameStateManager()::playerDied, "You got caught!");

            Runnable chaseAction = () -> {
                police.startChase();
                uiManager.getGameUI().showInfoMessage("RUN", 2f);
                MusicManager.playMusic(Assets.backMusic4);
                police.setDialogue(new Dialogue(caughtNode));
            };

            police.setDialogue(new Dialogue(new DialogueNode(chaseAction, "What are you doing?", "Stop right there!")));

            uiManager.getDialogueManager().startForcedDialogue(police);
        }, 2f);
    }

    public void handlePfandAutomat(float delta){
        Item pfandAutomat = itemManager.getPfandAutomat();
        pfandAutomat.updateCooldown(delta);

        if (pfandAutomat.isPlayerNear(player)) {
            font.draw(batch,"Press E to change your pfand for money",
                pfandAutomat.getX(),
                pfandAutomat.getY() + 150);

            if (uiManager.isInteractPressed() && pfandAutomat.canInteract()) {
                if(player.getInventory().removeItem("pfand",1)){
                    triggerPfandAutomat();
                } else {
                    uiManager.getGameUI().showInfoMessage("You don't have enough pfand",1f);
                }
            }
        }
    }

    public void triggerPfandAutomat(){
        SoundManager.playSound(Assets.pfandAutomatSound);
        TimerManager.setAction(() -> {
            SoundManager.playSound(Assets.moneySound);
            uiManager.getGameUI().showInfoMessage("You got 1 money for pfand",1f);
            player.getInventory().addItem("money",1);
        }, 1.9f);

        itemManager.getPfandAutomat().startCooldown(1.9f);
    }

    public void handlePolice(){
        if (npcManager.getPolice1() == null) return;

        Police police = npcManager.getPolice1();
        police.update(player);

        switch (police.getState()) {
            case ESCAPED -> {
                MusicManager.playMusic(Assets.backMusic1);
                uiManager.getGameUI().showInfoMessage("You ran away from the police", 1.5f);
                npcManager.kill(police);
                player.getInventory().addItem("money", 50);
            }
            case CAUGHT -> Main.getGameStateManager().playerDied();
        }
    }
}
