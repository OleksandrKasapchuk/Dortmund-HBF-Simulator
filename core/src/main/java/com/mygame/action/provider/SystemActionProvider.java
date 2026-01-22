package com.mygame.action.provider;

import com.badlogic.gdx.utils.JsonValue;
import com.mygame.Main;
import com.mygame.action.ActionRegistry;
import com.mygame.game.GameContext;
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

        registry.registerAction("act.system.start", context.gsm::startGame);
        registry.registerAction("act.system.newGame", () -> {
            SettingsManager.resetSettings();
            Main.restartGame();
            Main.getGameInitializer().getManagerRegistry().getContext().gsm.startGame();
        });
        registry.registerAction("act.system.pause", context.gsm::togglePause);
        registry.registerAction("act.system.settings", context.gsm::toggleSettings);
        registry.registerAction("act.system.map", context.gsm::toggleMap);
        registry.registerAction("act.system.menu", context.gsm::exitToMenu);
    }
}
