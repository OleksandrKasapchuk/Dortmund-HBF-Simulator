package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.entity.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.global.QuestManager;
import com.mygame.world.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
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

        World mainWorld = new World("main","maps/main_station.tmx");
        World backWorld = new World("leopold","maps/leopold.tmx");

        World subwayWorld = new World("subway", "maps/subway.tmx");
        World homeWorld = new World("home","maps/home.tmx");

        WorldManager.addWorld(mainWorld);
        WorldManager.addWorld(backWorld);
        WorldManager.addWorld(subwayWorld);
        WorldManager.addWorld(homeWorld);

        WorldManager.setCurrentWorld("main");
        System.out.println("GameInitializer: Worlds created and configured.");

        GameSettings settings = SettingsManager.load();

         WorldManager.setCurrentWorld(WorldManager.getWorld(settings.currentWorldName));

        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.textureZoe, WorldManager.getCurrentWorld());
        System.out.println("GameInitializer: Player created.");

        managerRegistry = new ManagerRegistry(batch, font, player);
        System.out.println("GameInitializer: ManagerRegistry created.");

        // Load inventory and quests from settings
        if (settings.inventory != null) {
            settings.inventory.forEach((itemKey, amount) -> player.getInventory().addItem(ItemRegistry.get(itemKey), amount));
        }
        if (settings.activeQuests != null) {
            settings.activeQuests.forEach(key -> QuestManager.addQuest(new QuestManager.Quest(key, "quest." + key + ".name", "quest." + key + ".description")));
        }

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
    }
}
