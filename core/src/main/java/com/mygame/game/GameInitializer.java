package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.action.ActionRegistry;
import com.mygame.entity.item.ItemInteractionSystem;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.ManagerRegistry;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestRegistry;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.load.SkinLoader;
import com.mygame.world.WorldManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;

    private SpriteBatch batch;
    private Skin skin;
    private ManagerRegistry managerRegistry;
    private GameInputHandler gameInputHandler;
    private GameContext ctx;
    private ScenarioController scController;

    public void initGame() {
        if (managerRegistry != null) managerRegistry.dispose();

        if (batch != null) batch.dispose();

        MusicManager.stopAll();

        batch = new SpriteBatch();

        initWorlds();

        skin = SkinLoader.loadSkin();

        GameSettings settings = SettingsManager.load();
        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.getTexture("zoe"), null);
        managerRegistry = new ManagerRegistry(batch, player, skin);

        ctx = managerRegistry.createContext();

        // --- Ініціалізація реєстрів ---
        ItemRegistry.init();
        QuestRegistry.init();   // Спочатку реєструємо квести з JSON
        QuestManager.init();    // Потім створюємо об'єкти квестів у менеджері

        DialogueRegistry.reset();
        ActionRegistry.registerAll(ctx);
        DialogueRegistry.init();
        player.getInventory().setUI(managerRegistry.getUiManager());

        for (World world : WorldManager.getWorlds().values()) {
            managerRegistry.getNpcManager().loadNpcsFromMap(world);
            managerRegistry.getItemManager().loadItemsFromMap(world);
            managerRegistry.getTransitionManager().loadTransitionsFromMap(world);
        }

        // --- Спочатку встановлюємо світ ---
        World startWorld = WorldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        WorldManager.setCurrentWorld(startWorld);

        // --- Потім ініціалізуємо сценарії та квести ---
        scController = new ScenarioController();
        scController.init(ctx);
        QuestProgressTriggers.init();
        ItemInteractionSystem.init();

        // Load other game state data
        if (settings.inventory != null)
            settings.inventory.forEach((itemKey, amount) -> player.getInventory().addItem(ItemRegistry.get(itemKey), amount));

        // Відновлюємо прогрес квестів зі збереження
        if (settings.activeQuests != null) {
            settings.activeQuests.forEach((key, saveData) -> {
                QuestManager.Quest q = QuestManager.getQuest(key);
                if (q != null) {
                    q.setStatus(saveData.status);
                    q.setProgress(saveData.progress);
                }
            });
        }

        gameInputHandler = new GameInputHandler(managerRegistry.getGameStateManager(), managerRegistry.getUiManager());

        MusicManager.playMusic(Assets.getMusic("start"));
    }

    public GameInputHandler getGameInputHandler() { return gameInputHandler; }
    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public Player getPlayer() { return player; }
    public SpriteBatch getBatch() { return batch; }
    public GameContext getContext() { return ctx; }
    public ScenarioController getScController(){ return scController; }

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (skin != null) skin.dispose();
    }

    public void initWorlds(){
        WorldManager.addWorld(new World("main", "maps/main_station.tmx"));
        WorldManager.addWorld(new World("leopold", "maps/leopold.tmx"));
        WorldManager.addWorld(new World("subway", "maps/subway.tmx"));
        WorldManager.addWorld(new World("home", "maps/home.tmx"));
        WorldManager.addWorld(new World("kamp", "maps/kamp.tmx"));
    }
}
