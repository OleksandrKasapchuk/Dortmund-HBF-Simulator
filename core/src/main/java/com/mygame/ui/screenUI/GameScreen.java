package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Queue;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.DayManager;
import com.mygame.world.WorldManager;

/**
 * GameUI handles the on-screen HUD elements during gameplay.
 */
public class GameScreen extends Screen {
    private final Label moneyLabel;
    private final Label infoLabel;
    private final Label worldLabel;
    private final Label dayLabel;
    private final Label phaseLabel;
    private float infoMessageTimer = 0f;

    private record Message(String text, float duration) {}
    private final Queue<Message> messageQueue = new Queue<>();
    private WorldManager worldManager;

    public GameScreen(Skin skin, WorldManager worldManager, DayManager dayManager, Player player) {
        super();
        this.worldManager = worldManager;

        // Налаштування root таблиці для HUD
        root.top().pad(30);

        // --- TOP ROW: World Name (Left) and Money (Right) ---
        worldLabel = createLabel(skin, "", 1.5f);
        moneyLabel = createLabel(skin, "", 1.5f);
        dayLabel = createLabel(skin, "", 1.5f);
        phaseLabel = createLabel(skin, "", 1.5f);

        root.add(worldLabel).expandX().left();
        root.add(dayLabel).center().padRight(20);
        root.add(phaseLabel).center();
        root.add(moneyLabel).expandX().right().row();

        // --- MIDDLE: Info Messages ---
        infoLabel = createLabel(skin, "", 2f);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setVisible(false);

        root.add(infoLabel).colspan(3).padTop(50).center();

        EventBus.subscribe(Events.WorldChangedEvent.class, event -> updateWorld(event.newWorldId()));

        EventBus.subscribe(Events.NewDayEvent.class, event -> updateDay(event.newDayCount()));
        EventBus.subscribe(Events.PhaseChangedEvent.class, event -> updatePhase(event.newPhase()));

        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> {
            if (event.item().getKey().equals("money")){
                updateMoney(event.newAmount());
            }
        });

        updateMoney(player.getInventory().getMoney());
        updatePhase(dayManager.getCurrentPhase());
    }

    public void updateMoney(int money) {
        moneyLabel.setText(Assets.ui.format("ui.money", money));
    }

    public void updateWorld(String worldName) {
        worldLabel.setText(Assets.ui.format("ui.world.name", Assets.ui.get("ui.world.name." + worldName)));
    }

    public void updateDay(int day) {
        dayLabel.setText(Assets.ui.format("ui.day", day));
    }

    public void updatePhase(DayManager.Phase phase) {
        phaseLabel.setText(Assets.ui.format("ui.phase", Assets.ui.get(phase.getLocalizationKey())));
    }

    public void showInfoMessage(String message, float duration) {
        messageQueue.addLast(new Message(message, duration));
    }

    public void update(float delta) {
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
    }
}
