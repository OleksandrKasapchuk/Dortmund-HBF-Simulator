package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.save.GameSettings;
import com.mygame.managers.global.save.SettingsManager;
import com.mygame.world.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;

    private SpriteBatch batch;

    private ManagerRegistry managerRegistry;
    private GameInputHandler gameInputHandler;

    public void initGame() {
        if (managerRegistry != null) managerRegistry.dispose();

        if (batch != null) batch.dispose();

        MusicManager.stopAll();
        QuestManager.reset();

        batch = new SpriteBatch();


        GameSettings settings = SettingsManager.load();
        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.getTexture("zoe"), null);
        managerRegistry = new ManagerRegistry(batch, Assets.myFont, player);

        player.getInventory().setUI(managerRegistry.getUiManager());

        // 3. Set the player's world and the current world
        World startWorld = WorldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        WorldManager.setCurrentWorld(startWorld);

        // 5. Load other game state data
        if (settings.inventory != null)
            settings.inventory.forEach((itemKey, amount) -> player.getInventory().addItem(ItemRegistry.get(itemKey), amount));

        if (settings.activeQuests != null) {
            settings.activeQuests.forEach((key, saveData) -> QuestManager.addQuest(new QuestManager.Quest(key, saveData.progressable, saveData.progress, saveData.maxProgress)));
        }

        gameInputHandler = new GameInputHandler(managerRegistry.getGameStateManager(), managerRegistry.getUiManager());

        MusicManager.playMusic(Assets.getMusic("startMusic"));
    }

    public GameInputHandler getGameInputHandler() { return gameInputHandler; }
    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public Player getPlayer() { return player; }
    public SpriteBatch getBatch() { return batch; }


    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
    }
}
