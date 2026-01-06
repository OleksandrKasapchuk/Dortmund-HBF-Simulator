package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.world.WorldManager;

/**
 * GameUI handles the on-screen HUD elements during gameplay.
 */
public class GameScreen extends Screen {
    private final Label moneyLabel;
    private final Label infoLabel;
    private final Label worldLabel;
    private float infoMessageTimer = 0f;

    public GameScreen(Skin skin) {
        super();

        // Налаштування root таблиці для HUD
        root.top().pad(30);

        // --- TOP ROW: World Name (Left) and Money (Right) ---
        worldLabel = createLabel(skin, "", 1.5f);
        moneyLabel = createLabel(skin, "", 1.5f);

        root.add(worldLabel).expandX().left();
        root.add(moneyLabel).expandX().right().row();

        // --- MIDDLE: Info Messages ---
        infoLabel = createLabel(skin, "", 2f);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setVisible(false);

        // Додаємо повідомлення з великим відступом зверху
        root.add(infoLabel).colspan(2).padTop(100).center();
    }

    public void updateMoney(int money) {
        moneyLabel.setText(Assets.ui.format("ui.money", money));
    }

    public void updateWorld(String worldName) {
        worldLabel.setText(Assets.ui.format("ui.world.name", Assets.ui.get("ui.world.name." + worldName)));
    }

    public void showInfoMessage(String message, float duration) {
        infoLabel.setText(message);
        infoLabel.setVisible(true);
        infoMessageTimer = duration;
    }

    public void update(float delta, Player player) {
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) infoLabel.setVisible(false);
        }
        updateMoney(player.getInventory().getMoney());
        if (WorldManager.getCurrentWorld() != null) {
            updateWorld(WorldManager.getCurrentWorld().getName());
        }
    }
}
