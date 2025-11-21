package com.mygame;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.Player;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.world.Transition;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;

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

        World mainWorld = new World("main","maps/map1.txt");
        World backWorld = new World("back","maps/map2.txt");

        mainWorld.addTransition(new Transition("back", 350, 200, new Rectangle(1200, 1700, 1000, 200)));
        backWorld.addTransition(new Transition("main", 1600, 1600, new Rectangle(100, 100, 200, 200)));

        WorldManager.addWorld(mainWorld);
        WorldManager.addWorld(backWorld);
        WorldManager.setCurrentWorld("main");

        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, WorldManager.getCurrentWorld());

        managerRegistry = new ManagerRegistry(batch, font, player);

        gameInputHandler = new GameInputHandler(managerRegistry.getGameStateManager());

        MusicManager.playMusic(Assets.startMusic);
    }

    public GameInputHandler getGameInputHandler() { return gameInputHandler; }
    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public Player getPlayer() { return player; }
    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() {return font;}

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
