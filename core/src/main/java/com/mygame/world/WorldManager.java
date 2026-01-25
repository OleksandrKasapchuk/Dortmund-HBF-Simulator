package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;
import com.mygame.ui.inGameUI.DarkOverlay;
import com.mygame.world.zone.QuestZone;
import com.mygame.world.zone.TransitionZone;
import com.mygame.world.zone.Zone;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private final Map<String, World> worlds = new HashMap<>();
    private World currentWorld;
    private static final float TRANSITION_COOLDOWN = 0.5f; // Cooldown in seconds
    private float cooldownTimer = 0f;
    private boolean inZone = false;

    // Context for event handlers and updates
    private Player player;
    private DarkOverlay darkOverlay;
    private Zone activeZone;

    public WorldManager(Player player, DarkOverlay darkOverlay) {
        addWorld(new World("main", "maps/main_station.tmx"));
        addWorld(new World("leopold", "maps/leopold.tmx"));
        addWorld(new World("subway", "maps/subway.tmx"));
        addWorld(new World("home", "maps/home.tmx"));
        addWorld(new World("kamp", "maps/kamp.tmx"));
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
        EventBus.subscribe(Events.TransitionRequestedEvent.class, this::handleTransition);
        this.player = player;
        this.darkOverlay = darkOverlay;
    }
    private void handleTransition(Events.TransitionRequestedEvent e) {
        if (cooldownTimer > 0) return;
        if (player == null) return;

        EventBus.fire(new Events.DarkOverlayEvent(0.8f));
        TimerManager.setAction(() -> {
            setCurrentWorld(e.targetWorldId());
            player.setX(e.targetX());
            player.setY(e.targetY());
            player.setWorld(currentWorld);
            cooldownTimer = TRANSITION_COOLDOWN; // Start cooldown
        }, 0.2f);
    }

    public void addWorld(World world) {
        worlds.put(world.getName(), world);
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    public World getWorld(String id) {
        return worlds.get(id);
    }

    public Map<String, World> getWorlds() {
        return worlds;
    }

    public void disposeWorlds() {
        for (World world : worlds.values()) {
            world.dispose();
        }
        worlds.clear();
        currentWorld = null;
    }

    public void setCurrentWorld(String id) {
        if (!worlds.containsKey(id)) return;
        this.currentWorld = worlds.get(id);
        EventBus.fire(new Events.WorldChangedEvent(currentWorld.getName()));
        EventBus.fire(new Events.SaveRequestEvent());
    }

    public void setCurrentWorld(World world) {
        this.currentWorld = world;
        EventBus.fire(new Events.WorldChangedEvent(currentWorld.getName()));
        EventBus.fire(new Events.SaveRequestEvent());
    }

    public void renderBottomLayers(OrthographicCamera camera) {
        if (currentWorld != null) {
            currentWorld.renderBottomLayers(camera);
        }
    }

    public void renderTopLayers(OrthographicCamera camera) {
        if (currentWorld != null) {
            currentWorld.renderTopLayers(camera);
        }
    }

    public void drawEntities(SpriteBatch batch, BitmapFont font) {
        if (currentWorld != null) {
            // Малюємо всі TransitionZone
            for (Zone zone : currentWorld.getZones()) {
                if (zone instanceof TransitionZone tz) {
                    Rectangle rect = tz.getArea();
                    float textX = rect.x + rect.width / 2 - 50;
                    float textY = rect.y + rect.height / 2;
                    Assets.myFont.draw(batch, Assets.ui.get("ui.world.name." + tz.targetWorldId), textX, textY);
                }
            }
        }

        if (inZone && player != null && activeZone.isEnabled()) {
            if (activeZone instanceof TransitionZone tz) {
                font.draw(batch, Assets.ui.get("world.pressEToTransition"),
                    player.getX(), player.getY() + player.getHeight() + 30);
            } else if (activeZone instanceof QuestZone qz) {
                font.draw(batch, Assets.ui.get("interact"),
                    player.getX(), player.getY() + player.getHeight() + 30);
            }
        }
    }

    private void handleInteraction() {
        if (inZone && activeZone != null && activeZone.isEnabled()) {
            activeZone.onInteract();
        }
    }

    public void update(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            inZone = false;
            return;
        }

        if (currentWorld == null || player == null) return;

        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        activeZone = null;
        for (Zone zone : currentWorld.getZones()) {
            if (zone.getArea().overlaps(playerBounds)) {
                activeZone = zone;
                break;
            }
        }

        inZone = activeZone != null;
    }
}
