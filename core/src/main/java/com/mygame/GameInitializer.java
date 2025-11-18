package com.mygame;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;
    private World world;

    private SpriteBatch batch;
    private BitmapFont font;

    private ManagerRegistry managerRegistry;
    private GameInputHandler gameInputHandler;

    public void initGame() {

        MusicManager.stopAll();
        QuestManager.reset();

        if (managerRegistry != null) {
            managerRegistry.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        world = new World();

        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world, null);

        managerRegistry = new ManagerRegistry(batch, font, player, world);

        ItemRegistry.init(managerRegistry);

        // Set dependencies
        player.setItemManager(managerRegistry.getItemManager());

        gameInputHandler = new GameInputHandler(managerRegistry.getGameStateManager());

        MusicManager.playMusic(Assets.startMusic);
    }

    public GameInputHandler getGameInputHandler() { return gameInputHandler; }
    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public Player getPlayer() { return player; }
    public World getWorld() { return world; }
    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() { return font; }

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
