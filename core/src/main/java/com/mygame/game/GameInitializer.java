package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.Assets;
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
        System.out.println("GameInitializer: Initializing game...");

        MusicManager.stopAll();
        QuestManager.reset();

        if (managerRegistry != null) {
            managerRegistry.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }

        batch = new SpriteBatch();
        font = Assets.myFont; // Use the font from Assets
        System.out.println("GameInitializer: Batch and font created.");

        World mainWorld = new World("main","maps/map1.txt");
        World backWorld = new World("back","maps/map2.txt");

        mainWorld.addTransition(new Transition("back", 350, 200, new Rectangle(1200, 1700, 1000, 200)));
        backWorld.addTransition(new Transition("main", 1600, 1600, new Rectangle(100, 100, 200, 200)));

        WorldManager.addWorld(mainWorld);
        WorldManager.addWorld(backWorld);
        WorldManager.setCurrentWorld("main");
        System.out.println("GameInitializer: Worlds created and configured.");

        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, WorldManager.getCurrentWorld());
        System.out.println("GameInitializer: Player created.");

        managerRegistry = new ManagerRegistry(batch, font, player);
        System.out.println("GameInitializer: ManagerRegistry created.");

        gameInputHandler = new GameInputHandler(managerRegistry.getGameStateManager());
        System.out.println("GameInitializer: GameInputHandler created.");

        MusicManager.playMusic(Assets.startMusic);
        System.out.println("GameInitializer: Game initialization complete.");
    }

    public GameInputHandler getGameInputHandler() { return gameInputHandler; }
    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public Player getPlayer() { return player; }
    public SpriteBatch getBatch() { return batch; }
    public BitmapFont getFont() {return font;}

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        // The font is managed and disposed by the Assets class, so no need to dispose it here.
    }
}
