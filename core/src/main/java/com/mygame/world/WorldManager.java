package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.ui.inGameUI.Overlay;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private final Map<String, World> worlds = new HashMap<>();
    private World currentWorld;

    // Context for event handlers and updates
    private Player player;
    private Overlay overlay;

    public WorldManager(Player player, Overlay overlay) {
        addWorld(new World("main", "maps/main_station.tmx"));
        addWorld(new World("leopold", "maps/leopold.tmx"));
        addWorld(new World("subway", "maps/subway.tmx"));
        addWorld(new World("home", "maps/home.tmx"));
        addWorld(new World("kamp", "maps/kamp.tmx"));
        this.player = player;
        this.overlay = overlay;
    }


    public void addWorld(World world) { worlds.put(world.getName(), world); }

    public World getCurrentWorld() { return currentWorld; }

    public World getWorld(String id) { return worlds.get(id); }

    public Map<String, World> getWorlds() { return worlds; }

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
}
