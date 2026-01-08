package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
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

    private record Message(String text, float duration) {}
    private final Queue<Message> messageQueue = new Queue<>();
    private WorldManager worldManager;

    public GameScreen(Skin skin, WorldManager worldManager) {
        super();
        this.worldManager = worldManager;

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
        root.add(infoLabel).colspan(2).padTop(50).center();
    }

    public void updateMoney(int money) {
        moneyLabel.setText(Assets.ui.format("ui.money", money));
    }

    public void updateWorld(String worldName) {
        worldLabel.setText(Assets.ui.format("ui.world.name", Assets.ui.get("ui.world.name." + worldName)));
    }

    public void showInfoMessage(String message, float duration) {
        messageQueue.addLast(new Message(message, duration));
    }

    public void update(float delta, Player player) {
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) {
                infoLabel.setVisible(false);
            }
        }

        if (infoMessageTimer <= 0 && !messageQueue.isEmpty()) {
            Message nextMessage = messageQueue.removeFirst();
            infoLabel.setText(nextMessage.text());
            infoLabel.setVisible(true);
            infoMessageTimer = nextMessage.duration();
        }

        updateMoney(player.getInventory().getMoney());
        if (worldManager.getCurrentWorld() != null) {
            updateWorld(worldManager.getCurrentWorld().getName());
        }
    }
}
