package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.Assets;
import com.mygame.DarkOverlay;
import com.mygame.entity.Player;
import com.mygame.managers.global.TimerManager;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private static final Map<String, World> worlds = new HashMap<>();
    private static World currentWorld;
    private static final float TRANSITION_COOLDOWN = 0.5f; // Cooldown in seconds
    private static float cooldownTimer = 0f;
    private static boolean inTransitionZone = false;

    public static void addWorld(World world) {
        worlds.put(world.getName(), world);
    }

    public static World getCurrentWorld() {
        return currentWorld;
    }

    public static World getWorld(String id) {
        return worlds.get(id);
    }

    public static Map<String, World> getWorlds(){return worlds;}
    public static void disposeWorlds() {
        for (World world : worlds.values()) {
            world.dispose();
        }
        worlds.clear();
        currentWorld = null;
    }

    public static void setCurrentWorld(String id) {
        if (!worlds.containsKey(id)) return;
        currentWorld = worlds.get(id);
    }

    public static void setCurrentWorld(World world) {
        currentWorld = world;
    }

    public static void renderMap(OrthographicCamera camera) {
        if (currentWorld != null) {
            currentWorld.renderMap(camera);
        }
    }

    public static void drawEntities(SpriteBatch batch, BitmapFont font, Player player) {
        if (currentWorld != null) currentWorld.draw(batch, font, player);

        if (inTransitionZone) {
            font.draw(batch, Assets.bundle.get("world.pressEToTransition"), player.getX(), player.getY() + player.getHeight() + 30);
        }
    }

    public static void update(float delta, Player player, boolean interactPressed, DarkOverlay darkOverlay) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            inTransitionZone = false; // Don't show prompt during cooldown
            return;
        }

        if (currentWorld == null) return;

        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        Transition activeTransition = null;
        for (Transition transition : currentWorld.getTransitions()) {
            if (transition.area.overlaps(playerBounds)) {
                activeTransition = transition;
                break;
            }
        }

        inTransitionZone = activeTransition != null;

        if (inTransitionZone && interactPressed) {
            final Transition finalTransition = activeTransition;

            darkOverlay.show(1, 0.75f);
            TimerManager.setAction(() -> {
                setCurrentWorld(finalTransition.targetWorldId);
                player.setX(finalTransition.targetX);
                player.setY(finalTransition.targetY);
                player.setWorld(currentWorld);
                inTransitionZone = false;
                cooldownTimer = TRANSITION_COOLDOWN; // Start cooldown
            }, 0.05f);

        }
    }
}
