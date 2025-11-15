package com.mygame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygame.dialogue.Dialogue;
import com.mygame.world.World;

/**
 * Police NPC that can chase the player and change states depending on distance.
 */
public class Police extends NPC {

    /** Possible states of police behaviour */
    public enum PoliceState { IDLE, CHASING, ESCAPED, CAUGHT }

    /** Current police state */
    private PoliceState state = PoliceState.IDLE;

    /** Constructor for Police NPC */
    public Police(String name, int width, int height, float x, float y,
                  Texture texture, World world, int speed, int distance, Dialogue dialogue) {

        // NPC constructor expects many params; most unused here (0,0,0f,0f)
        super(name, width, height, x, y, texture, world,
            0, 0, 0f, 0f,
            speed, distance, dialogue);
    }

    /** Updates police behaviour depending on its current state */
    public void update(Player player) {
        switch (state) {

            case CHASING:
                chasePlayer(player);

                // If player is close enough → caught
                if (isPlayerCaught(player)) {
                    state = PoliceState.CAUGHT;
                }
                // If player escaped far enough → escaped
                else if (isPlayerEscaped(player)) {
                    state = PoliceState.ESCAPED;
                }
                break;

            case ESCAPED:
            case CAUGHT:
            case IDLE:
                // These states do nothing during update.
                break;
        }
    }

    /** Player considered caught when within default "near" distance */
    private boolean isPlayerCaught(Player player) {
        return isPlayerNear(player);
    }

    /**
     * Player considered escaped if NOT near the police within a large radius.
     * (1200 = escape distance)
     */
    private boolean isPlayerEscaped(Player player) {
        return !isPlayerNear(player, 1200);
    }

    /**
     * Moves police toward the player's current position.
     * Only works if state == CHASING.
     */
    public void chasePlayer(Player player) {
        if (state != PoliceState.CHASING) return;

        float delta = Gdx.graphics.getDeltaTime();

        float targetX = player.getX();
        float targetY = player.getY();

        // Move horizontally
        if (this.getX() > targetX) {
            this.setX(this.getX() - getSpeed() * delta);
        } else if (this.getX() < targetX) {
            this.setX(this.getX() + getSpeed() * delta);
        }

        // Move vertically
        if (this.getY() > targetY) {
            this.setY(this.getY() - getSpeed() * delta);
        } else if (this.getY() < targetY) {
            this.setY(this.getY() + getSpeed() * delta);
        }
    }

    /**
     * Switches police to CHASING mode.
     */
    public void startChase() {
        state = PoliceState.CHASING;
    }

    /**
     * Returns the current state.
     */
    public PoliceState getState() {
        return state;
    }
}
