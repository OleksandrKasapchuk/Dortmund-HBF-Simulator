package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.managers.nonglobal.GameStateManager;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.nonglobal.NpcManager;
import com.mygame.entity.Player;
import com.mygame.managers.global.QuestManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;

/**
 * Main class / entry point of the game.
 * Handles initialization, rendering, input, and switching between different game states.
 */
public class Main extends ApplicationAdapter {

    private static Main instance;                 // Singleton instance for global access
    private Player player;                        // Player instance
    private World world;                          // Game world (tile map and blocks)
    private static ManagerRegistry managerRegistry; // Registry containing all managers (UI, NPCs, Quests, etc.)

    private SpriteBatch batch;                    // Batch for drawing sprites
    private BitmapFont font;                      // Font for text rendering

    @Override
    public void create() {
        Assets.load();                            // Load all textures, sounds, and music
        instance = this;
        initGame();                               // Initialize game objects and managers
    }

    /**
     * Initializes or resets the game state.
     * Stops music, resets quests, disposes old resources if necessary, and sets up player/world/managers.
     */
    private void initGame() {
        MusicManager.stopAll();
        QuestManager.reset();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        world = new World();
        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world, null);

        managerRegistry = new ManagerRegistry(batch, font, player, world);
        player.setItemManager(managerRegistry.getItemManager());

        MusicManager.playMusic(Assets.startMusic);
    }

    /**
     * Allows restarting the game from anywhere.
     */
    public static void restartGame() { instance.initGame(); }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(); // Check for key presses

        // Render according to current game state
        switch (managerRegistry.getGameStateManager().getState()) {
            case MENU: renderMenu(delta); break;
            case PLAYING: renderGame(delta); break;
            case PAUSED: renderPaused(delta); break;
            case SETTINGS: renderSettings(delta); break;
            case DEATH: renderDeath(); break;
        }
    }

    /**
     * Handles key input for global game actions (pause, settings, start game).
     */
    private void handleInput() {
        GameStateManager gameStateManager = managerRegistry.getGameStateManager();
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gameStateManager.togglePause();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameStateManager.toggleSettings();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gameStateManager.getState() == GameStateManager.GameState.MENU)
            gameStateManager.startGame();
    }

    /**
     * Updates and renders the main menu.
     */
    public void renderMenu(float delta) {
        managerRegistry.getUiManager().update(delta, player, managerRegistry.getNpcManager().getNpcs());
        managerRegistry.getUiManager().render();
    }

    /**
     * Updates and renders the game world, player, NPCs, and UI during normal gameplay.
     */
    public void renderGame(float delta) {
        NpcManager npcManager = managerRegistry.getNpcManager();

        player.update(delta);

        // Example: trigger death dialogue if player is in "stoned" state
        if (player.getState() == Player.State.STONED) {
            npcManager.getPolice().setDialogue(
                new Dialogue(
                    new DialogueNode(managerRegistry.getGameStateManager()::playerDied,
                        "Are you stoned?", "You are caught")
                )
            );
        }

        managerRegistry.update(delta);

        // --- World Rendering ---
        batch.begin();
        world.draw(batch);
        player.draw(batch);
        managerRegistry.render(); // Render other managed objects (NPCs, items, etc.)
        batch.end();

        // --- UI Rendering ---
        managerRegistry.getUiManager().render();
    }

    /**
     * Updates and renders the pause screen.
     */
    private void renderPaused(float delta) {
        UIManager uiManager = managerRegistry.getUiManager();
        uiManager.update(delta, player, managerRegistry.getNpcManager().getNpcs());
        uiManager.render();
    }

    /**
     * Updates and renders the settings screen.
     */
    private void renderSettings(float delta) {
        UIManager uiManager = managerRegistry.getUiManager();
        uiManager.update(delta, player, managerRegistry.getNpcManager().getNpcs());
        uiManager.render();
    }

    /**
     * Renders the death screen.
     */
    private void renderDeath() {
        managerRegistry.getUiManager().render();
    }

    @Override
    public void resize(int width, int height) {
        managerRegistry.resize();
    }

    @Override
    public void dispose() {
        Assets.dispose();
        batch.dispose();
        font.dispose();
        managerRegistry.dispose();
        MusicManager.stopAll();
    }

    public static ManagerRegistry getManagerRegistry() { return managerRegistry; }
}
