package com.mygame.action.provider;

import com.badlogic.gdx.utils.JsonValue;
import com.mygame.Main;
import com.mygame.action.ActionRegistry;
import com.mygame.game.GameContext;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.managers.TimerManager;

public class SystemActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("system.composite", (c, data) -> () -> {
            for (JsonValue subAction : data.get("actions")) {
                registry.createAction(c, subAction).run();
            }
        });

        registry.registerCreator("system.timer", (c, data) -> () -> {
            float delay = data.getFloat("delay", 1f);
            JsonValue actionData = data.get("action");
            TimerManager.setAction(() -> registry.createAction(c, actionData).run(), delay);
        });

        registry.registerAction("system.start", context.gsm::startGame);
        registry.registerAction("system.newGame", () -> {
            GameSettings newSettings = new GameSettings();
            newSettings.language = SettingsManager.load().language;
            SettingsManager.save(newSettings);
            Main.restartGame();
            Main.getGameInitializer().getManagerRegistry().getContext().gsm.startGame();
        });
        registry.registerAction("system.pause", context.gsm::togglePause);
        registry.registerAction("system.settings", context.gsm::toggleSettings);
        registry.registerAction("system.map", context.gsm::toggleMap);
        registry.registerAction("system.menu", context.gsm::exitToMenu);
    }
}
