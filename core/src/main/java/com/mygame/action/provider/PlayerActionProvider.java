package com.mygame.action.provider;

import com.badlogic.gdx.Gdx;
import com.mygame.action.ActionRegistry;
import com.mygame.entity.player.Player;
import com.mygame.game.GameContext;

public class PlayerActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("player.die", (c, data) -> c.gsm::playerDied);

        registry.registerCreator("player.checkState", (c, data) -> () -> {
            String requiredState = data.getString("state").toUpperCase();
            if (c.player.getState().name().equals(requiredState)) {
                if (data.has("action")) {
                    registry.createAction(c, data.get("action")).run();
                }
            } else {
                if (data.has("onFail")) {
                    registry.createAction(c, data.get("onFail")).run();
                }
            }
        });

        registry.registerCreator("player.setState", (c, data) -> () -> {
            String stateName = data.getString("key").toUpperCase();
            try {
                Player.State state = Player.State.valueOf(stateName);
                c.player.setState(state);
            } catch (IllegalArgumentException e) {
                Gdx.app.log("ActionRegistry", "Unknown player state: " + stateName);
            }
        });

        registry.registerCreator("player.lockMovement", (c, data) -> () -> c.player.setMovementLocked(data.getBoolean("locked")));

        registry.registerAction("player.sleep", context.dayManager::sleep);
    }
}
