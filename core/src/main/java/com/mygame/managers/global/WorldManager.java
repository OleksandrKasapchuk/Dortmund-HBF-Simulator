package com.mygame.managers.global;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.Player;
import com.mygame.world.Transition;
import com.mygame.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {

    private static Map<String, World> worlds = new HashMap<>();
    private static World currentWorld;
    private static final float TRANSITION_COOLDOWN = 0.5f; // Cooldown in seconds
    private static float cooldownTimer = 0f;


    public static void addWorld(World world) {
        worlds.put(world.getName(), world);
    }

    public static World getCurrentWorld() {
        return currentWorld;
    }

    public static World getWorld(String id) {
        return worlds.get(id);
    }


    public static void setCurrentWorld(String id) {
        if (!worlds.containsKey(id)) return;
        currentWorld = worlds.get(id);
    }

    public static void draw(SpriteBatch batch, BitmapFont font, Player player) {
        if (currentWorld != null) currentWorld.draw(batch, font, player);
    }

    public static void update(float delta, Player player) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            return; // Don't check for transitions during cooldown
        }

        if (currentWorld == null) return;

        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        for (Transition transition : currentWorld.getTransitions()) {
            if (transition.area.overlaps(playerBounds)) {
                setCurrentWorld(transition.targetWorldId);
                player.setX(transition.targetX);
                player.setY(transition.targetY);
                player.setWorld(currentWorld);
                cooldownTimer = TRANSITION_COOLDOWN; // Start cooldown
                break; // Exit after one transition
            }
        }
    }
}
