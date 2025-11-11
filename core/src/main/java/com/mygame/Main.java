package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;

    public enum GameState { MENU, PLAYING, PAUSED, SETTINGS, DEATH}
    private static GameState state = GameState.MENU;

    // Flag to prevent the boss failure dialogue from looping
    private boolean bossFailureTriggered = false;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);
        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);
        Assets.load();

        initGame();
        player.getInventory().registerEffect("joint", () -> {
            SoundManager.playSound(Assets.lighterSound);

            com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                @Override
                public void run() {
                    player.setStone();
                    MusicManager.playMusic(Assets.kaifMusic);
                    uiManager.getGameUI().showInfoMessage("You got stoned",1.5f);
                }
            }, 4f);
        });
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

        npcManager = new NpcManager(batch, player, world, uiManager, font);
        pfandManager = new PfandManager();

        state = GameState.MENU;
        uiManager.setCurrentStage("MENU");
        MusicManager.playMusic(Assets.startMusic);
    }

    public static void restartGame() {instance.initGame();}

    public static void startGame() {
        state = GameState.PLAYING;
        MusicManager.playMusic(Assets.backMusic1);
        uiManager.setCurrentStage("GAME");
    }

    public static void playerDied() {
        state = GameState.DEATH;
        MusicManager.stopAll();
        MusicManager.playMusic(Assets.backMusic4);
        uiManager.setCurrentStage("DEATH");
    }

    public static void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            MusicManager.pauseMusic();
            uiManager.setCurrentStage("PAUSE");
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            MusicManager.resumeMusic();
            uiManager.setCurrentStage("GAME");
        }
    }

    public static void toggleSettings() {
        if (state == GameState.PLAYING) {
            state = GameState.SETTINGS;
            uiManager.setCurrentStage("SETTINGS");
        } else if (state == GameState.SETTINGS) {
            state = GameState.PLAYING;
            uiManager.setCurrentStage("GAME");
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        MusicManager.update(delta);
        switch (state) {
            case MENU: renderMenu(); break;
            case PAUSED: renderPaused(); break;
            case PLAYING: renderGame(); break;
            case SETTINGS: renderSettings(); break;
            case DEATH: renderDeath(); break;
        }
    }

    public void renderMenu() {
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {startGame();}
    }

    public void renderGame(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggleSettings();
            return;
        }

        if (QuestManager.hasQuest("Big delivery") && player.getInventory().getAmount("grass") < 1000 && !bossFailureTriggered) {
            bossFailureTriggered = true;
            NPC boss = npcManager.getBoss();
            if (boss != null) {
                DialogueNode failureNode = new DialogueNode(() -> {
                    Main.playerDied();
                    SoundManager.playSound(Assets.gunShot);
                }, "You are not doing the task!", "I told you to hide the grass, not lose it.", "Now you will regret this...");
                boss.setDialogue(new Dialogue(failureNode));

                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        boss.setX(player.getX() - 100);
                        boss.setY(player.getY());
                        uiManager.getDialogueManager().startForcedDialogue(boss);
                    }
                }, 2f);
            }
        }

        float delta = Gdx.graphics.getDeltaTime();

        player.update(delta);
        itemManager.update(player);

        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);

        if (player.getState() == Player.State.STONED){
            npcManager.getPolice().setDialogue(new Dialogue(new DialogueNode(Main::playerDied, "Are you stoned?", "You are caught")));
        }

        if(npcManager.updatePolice()){
            MusicManager.playMusic(Assets.backMusic1);
            uiManager.getGameUI().showInfoMessage("You ran away from the police", 1.5f);
            Runnable rewardAction = ()->{
                player.getInventory().addItem("money", 50);
                uiManager.getGameUI().showInfoMessage("You got 50 money", 1.5f);
            };
            npcManager.getBoss().setDialogue(new Dialogue(new DialogueNode(rewardAction, "Oh, you've managed this.", "Well done!")));
        }

        float targetX = player.getX() + player.getWidth() / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;
        float cameraX = Math.max(camera.viewportWidth / 2f, Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f, Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.draw(batch);

        if (QuestManager.hasQuest("Big delivery") && itemManager.getBush().isPlayerNear(player)) {
            font.draw(batch, "Press E to hide your kg", itemManager.getBush().getX(), itemManager.getBush().getY());
            if (uiManager.isInteractPressed()) {
                player.getInventory().removeItem("grass", 1000);
                QuestManager.removeQuest("Big delivery");
                SoundManager.playSound(Assets.bushSound);
                player.setMovementLocked(true);
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        npcManager.callPolice();
                        NPC police1 = npcManager.getPolice1();
                        if (police1 != null) {
                            DialogueNode caughtNode = new DialogueNode(Main::playerDied, "You got caught!");
                            Runnable chaseAction = () -> {
                                police1.setFollowing(true);
                                uiManager.getGameUI().showInfoMessage("RUN", 2f);
                                MusicManager.playMusic(Assets.backMusic4);
                                police1.setDialogue(new Dialogue(caughtNode));
                            };
                            police1.setDialogue(new Dialogue(new DialogueNode(chaseAction, "What are you doing? Stop right there!")));
                            uiManager.getDialogueManager().startForcedDialogue(police1);
                        }
                    }
                }, 2f);
            }
        }
        itemManager.draw(batch);

        itemManager.getPfandAutomat().updateCooldown(Gdx.graphics.getDeltaTime());

        if (itemManager.getPfandAutomat().isPlayerNear(player)) {
            font.draw(batch,"Press E to change your pfand for money",
                itemManager.getPfandAutomat().getX(),
                itemManager.getPfandAutomat().getY() + 150);

            if (uiManager.isInteractPressed() && itemManager.getPfandAutomat().canInteract()) {
                if(player.getInventory().removeItem("pfand",1)){
                    SoundManager.playSound(Assets.pfandAutomatSound);
                    com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                        @Override
                        public void run() {
                            SoundManager.playSound(Assets.moneySound);
                            uiManager.getGameUI().showInfoMessage("You got 1 money for pfand",1f);
                            player.getInventory().addItem("money",1);
                        }
                    }, 1.9f);

                    itemManager.getPfandAutomat().startCooldown(2f);
                } else {
                    uiManager.getGameUI().showInfoMessage("You don't have enough pfand",1f);
                }
            }
        }
        player.draw(batch);
        npcManager.render();
        pfandManager.draw(batch);
        batch.end();
        uiManager.render();
        uiManager.resetButtons();
    }

    public void renderPaused() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
            return;
        }
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
    }

    public void renderSettings() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            toggleSettings();
            return;
        }
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
    }

    public void renderDeath(){uiManager.render();}

    @Override
    public void resize(int width, int height) {
        if (viewport != null) viewport.update(width, height, true);
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
    public static Main getInstance() {
        return instance;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
