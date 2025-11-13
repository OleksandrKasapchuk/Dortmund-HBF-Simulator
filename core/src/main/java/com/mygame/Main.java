package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.managers.CameraManager;
import com.mygame.managers.GameStateManager;
import com.mygame.managers.PlayerEffectManager;
import com.mygame.managers.TimerManager;
import com.mygame.managers.audio.MusicManager;
import com.mygame.managers.audio.SoundManager;
import com.mygame.entity.NPC;
import com.mygame.managers.NpcManager;
import com.mygame.entity.Player;
import com.mygame.managers.ItemManager;
import com.mygame.managers.PfandManager;
import com.mygame.managers.QuestManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;

public class Main extends ApplicationAdapter {
    private static Main instance;

    private Player player;

    private World world;
    private static UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private PlayerEffectManager playerEffectManager;
    private CameraManager cameraManager;
    private static GameStateManager gameStateManager;

    private SpriteBatch batch;
    private BitmapFont font;


    private boolean bossFailureTriggered = false;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        Assets.load();

        initGame();

        player.getInventory().setOnInventoryChanged(() -> {
            if (uiManager.getInventoryUI().isVisible()) {
                uiManager.getInventoryUI().update(player);
            }
        });
    }
    private void initGame() {
        MusicManager.stopAll();
        QuestManager.reset();

        bossFailureTriggered = false;

        world = new World();
        itemManager = new ItemManager(world);
        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world, itemManager);
        if (uiManager != null) uiManager.dispose();
        uiManager = new UIManager(player);

        playerEffectManager = new PlayerEffectManager(player, uiManager);
        playerEffectManager.registerEffects();

        cameraManager = new CameraManager(4000, 2000);

        npcManager = new NpcManager(batch, player, world, uiManager, font);
        pfandManager = new PfandManager();

        gameStateManager = new GameStateManager(uiManager);

        uiManager.setCurrentStage("MENU");
        MusicManager.playMusic(Assets.startMusic);
    }

    public static void restartGame() {instance.initGame();}

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        switch (gameStateManager.getState()) {
            case MENU: renderMenu(delta); break;
            case PLAYING: renderGame(delta); break;
            case PAUSED: renderPaused(delta); break;
            case SETTINGS: renderSettings(delta); break;
            case DEATH: renderDeath(); break;
        }
    }
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gameStateManager.togglePause();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameStateManager.toggleSettings();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gameStateManager.getState() == GameStateManager.GameState.MENU)
            gameStateManager.startGame();
    }
    public void renderMenu(float delta) {
        uiManager.update(delta, player, npcManager.getNpcs());
        uiManager.render();
    }

    public void renderGame(float delta){
        if (QuestManager.hasQuest("Big delivery") && player.getInventory().getAmount("grass") < 1000 && !bossFailureTriggered) {
            bossFailureTriggered = true;
            NPC boss = npcManager.getBoss();
            if (boss != null) {
                DialogueNode failureNode = new DialogueNode(() -> {
                    Main.getGameStateManager().playerDied();
                    SoundManager.playSound(Assets.gunShot);
                }, "You are not doing the task!", "I told you to hide the grass, not lose it.", "Now you will regret this...");
                boss.setDialogue(new Dialogue(failureNode));

                TimerManager.setAction(() -> {
                    boss.setX(player.getX() - 100);
                    boss.setY(player.getY());
                    uiManager.getDialogueManager().startForcedDialogue(boss);
                }, 2f);
            }
        }
        player.update(delta);
        itemManager.update(player);

        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);

        if (player.getState() == Player.State.STONED){npcManager.getPolice().setDialogue(new Dialogue(new DialogueNode(Main.getGameStateManager()::playerDied, "Are you stoned?", "You are caught")));}

        if(npcManager.updatePolice()){
            MusicManager.playMusic(Assets.backMusic1);
            uiManager.getGameUI().showInfoMessage("You ran away from the police", 1.5f);
            Runnable rewardAction = ()->{
                player.getInventory().addItem("money", 50);
                uiManager.getGameUI().showInfoMessage("You got 50 money", 1.5f);
            };
            npcManager.getBoss().setDialogue(new Dialogue(new DialogueNode(rewardAction, "Oh, you've managed this.", "Well done!")));
        }

        cameraManager.update(player, batch);

        batch.begin();
        world.draw(batch);

        if (QuestManager.hasQuest("Big delivery") && itemManager.getBush().isPlayerNear(player)) {
            font.draw(batch, "Press E to hide your kg", itemManager.getBush().getX(), itemManager.getBush().getY());
            if (uiManager.isInteractPressed()) {
                player.getInventory().removeItem("grass", 1000);
                QuestManager.removeQuest("Big delivery");
                SoundManager.playSound(Assets.bushSound);
                player.setMovementLocked(true);
                TimerManager.setAction(() -> {
                    npcManager.callPolice();
                    NPC police1 = npcManager.getPolice1();
                    if (police1 != null) {
                        DialogueNode caughtNode = new DialogueNode(Main.getGameStateManager()::playerDied, "You got caught!");
                        Runnable chaseAction = () -> {
                            police1.setFollowing(true);
                            uiManager.getGameUI().showInfoMessage("RUN", 2f);
                            MusicManager.playMusic(Assets.backMusic4);
                            police1.setDialogue(new Dialogue(caughtNode));
                        };

                        police1.setDialogue(new Dialogue(new DialogueNode(chaseAction, "What are you doing?","Stop right there!")));
                        uiManager.getDialogueManager().startForcedDialogue(police1);
                    }
                }, 2f);
            }
        }
        itemManager.draw(batch);

        itemManager.getPfandAutomat().updateCooldown(delta);

        if (itemManager.getPfandAutomat().isPlayerNear(player)) {
            font.draw(batch,"Press E to change your pfand for money",
                itemManager.getPfandAutomat().getX(),
                itemManager.getPfandAutomat().getY() + 150);

            if (uiManager.isInteractPressed() && itemManager.getPfandAutomat().canInteract()) {
                if(player.getInventory().removeItem("pfand",1)){
                    SoundManager.playSound(Assets.pfandAutomatSound);
                    TimerManager.setAction(() -> {
                        SoundManager.playSound(Assets.moneySound);
                        uiManager.getGameUI().showInfoMessage("You got 1 money for pfand",1f);
                        player.getInventory().addItem("money",1);
                    }, 1.9f);

                    itemManager.getPfandAutomat().startCooldown(1.9f);
                } else {
                    uiManager.getGameUI().showInfoMessage("You don't have enough pfand",1f);
                }
            }
        }
        player.draw(batch);
        npcManager.render(delta);
        pfandManager.draw(batch);
        batch.end();
        uiManager.render();
        uiManager.resetButtons();
    }

    private void renderPaused(float delta) {
        uiManager.update(delta, player, npcManager.getNpcs());
        uiManager.render();
    }

    private void renderSettings(float delta) {
        uiManager.update(delta, player, npcManager.getNpcs());
        uiManager.render();
    }

    private void renderDeath() {uiManager.render();}

    @Override
    public void resize(int width, int height) {
        cameraManager.resize(width, height);
        if (uiManager != null) uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        Assets.dispose();
        batch.dispose();
        font.dispose();
        uiManager.dispose();
        MusicManager.stopAll();
    }
    public static CameraManager getCameraManager() {return instance.cameraManager;}
    public static GameStateManager getGameStateManager() {return gameStateManager;}
}
