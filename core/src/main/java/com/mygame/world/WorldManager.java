package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.ui.inGameUI.DarkOverlay;
import com.mygame.world.transition.Transition;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private final Map<String, World> worlds = new HashMap<>();
    private World currentWorld;
    private static final float TRANSITION_COOLDOWN = 0.5f; // Cooldown in seconds
    private float cooldownTimer = 0f;
    private boolean inTransitionZone = false;

    // Context for event handlers and updates
    private Player player;
    private DarkOverlay darkOverlay;
    private Transition activeTransition;

    public WorldManager(Player player, DarkOverlay darkOverlay) {
        addWorld(new World("main", "maps/main_station.tmx"));
        addWorld(new World("leopold", "maps/leopold.tmx"));
        addWorld(new World("subway", "maps/subway.tmx"));
        addWorld(new World("home", "maps/home.tmx"));
        addWorld(new World("kamp", "maps/kamp.tmx"));
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
        this.player = player;
        this.darkOverlay = darkOverlay;
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
    }

    public void setCurrentWorld(World world) {
        this.currentWorld = world;
        EventBus.fire(new Events.WorldChangedEvent(currentWorld.getName()));
    }

    public void renderMap(OrthographicCamera camera) {
        if (currentWorld != null) {
            currentWorld.renderMap(camera);
        }
    }

    public void drawEntities(SpriteBatch batch, BitmapFont font) {
        if (currentWorld != null) currentWorld.draw(batch);

        if (inTransitionZone && player != null) {
            font.draw(batch, Assets.ui.get("world.pressEToTransition"), player.getX(), player.getY() + player.getHeight() + 30);
        }
    }

    private void handleInteraction() {
        if (inTransitionZone && activeTransition != null) {
            EventBus.fire(new Events.DarkOverlayEvent(0.8f));
            setCurrentWorld(activeTransition.targetWorldId);
            player.setX(activeTransition.targetX);
            player.setY(activeTransition.targetY);
            player.setWorld(currentWorld);
            inTransitionZone = false;
            cooldownTimer = TRANSITION_COOLDOWN; // Start cooldown
        }
    }

    public void update(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            inTransitionZone = false;
            return;
        }

        if (currentWorld == null || player == null) return;

        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        activeTransition = null;
        for (Transition transition : currentWorld.getTransitions()) {
            if (transition.area.overlaps(playerBounds)) {
                activeTransition = transition;
                break;
            }
        }

        inTransitionZone = activeTransition != null;
    }
}
