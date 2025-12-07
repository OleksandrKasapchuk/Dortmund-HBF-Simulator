package com.mygame.entity.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.player.Player;
import com.mygame.world.World;
import com.mygame.world.WorldManager;
import com.mygame.world.transition.Transition;

/**
 * Police NPC that can chase the player and change states depending on distance.
 */
public class Police extends NPC {

    public enum PoliceState { IDLE, CHASING, ESCAPED, CAUGHT, TRANSITIONING }
    private PoliceState state = PoliceState.IDLE;

    // Target coordinates for the chase
    private float targetX;
    private float targetY;

    private World targetWorld; // Світ, куди поліція хоче дійти
    private boolean movingToTransition = false;
    private Transition activeTransition = null; // The transition the police is currently moving towards

    private float lostTimer = 0f;
    private static final float MAX_LOST_TIME = 5f; // якщо поліція не бачить 5 секунд — втеча

    public Police(String name, int width, int height, float x, float y,
                  Texture texture, World world, int speed, int distance, DialogueNode dialogue) {
        super(name, width, height, x, y, texture, world,
            0, 0, 0f, 0f, // Static patrol path, not used for police
            speed, distance, dialogue);
    }

    public Transition findTransitionToPlayer(World playerWorld) {
        for (Transition t : getWorld().getTransitions()) {
            if (t.targetWorldId.equals(playerWorld.getName())) {
                Gdx.app.log("Police", "Found a transition to " + playerWorld.getName() + " at " + t.area);
                return t;
            }
        }
        Gdx.app.log("Police", "Could not find a transition to " + playerWorld.getName());
        return null;
    }

    /**
     * Updates police behaviour depending on its current state.
     * @param player The player being chased.
     * @return The Transition object if the police enters a transition zone, otherwise null.
     */
    public Transition update(Player player) {

        switch (state) {
            case CHASING:
                Gdx.app.log("Police", "State: CHASING. Current world: " + getWorld().getName());
                if (player.getWorld() == this.getWorld()) {
                    // Гравець у тому ж світі – звичайне переслідування
                    targetX = player.getX();
                    targetY = player.getY();
                    movingToTransition = false;
                    activeTransition = null;
                    Gdx.app.log("Police", "Player in same world. Chasing to (" + targetX + ", " + targetY + ")");
                } else {
                    // Гравець в іншому світі
                    Gdx.app.log("Police", "Player in different world: " + player.getWorld().getName());
                    if (!movingToTransition) {
                        activeTransition = findTransitionToPlayer(player.getWorld());
                        if (activeTransition != null) {
                            targetX = activeTransition.area.x + activeTransition.area.width / 2;
                            targetY = activeTransition.area.y + activeTransition.area.height / 2;
                            targetWorld = WorldManager.getWorld(activeTransition.targetWorldId);
                            movingToTransition = true;
                            Gdx.app.log("Police", "Found transition to " + targetWorld.getName() + ". Moving to transition zone at (" + targetX + ", " + targetY + ")");
                        } else {
                             Gdx.app.log("Police", "No transition found to player's world: " + player.getWorld().getName());
                        }
                    }

                    if (movingToTransition && activeTransition != null) {
                        // Якщо поліція досягла зони переходу
                        if (activeTransition.area.contains(getX(), getY())) {
                            Gdx.app.log("Police", "Reached transition zone. Requesting transition to " + activeTransition.targetWorldId);
                            movingToTransition = false; // Reset for next chase
                            Transition transitionToReturn = activeTransition;
                            activeTransition = null;
                            setState(PoliceState.TRANSITIONING); // Перехід у стан очікування
                            return transitionToReturn; // Signal that a transition should occur
                        }
                    }
                }

                chase(); // Move towards the target coordinates

                // Check for state changes
                if (isPlayerCaught(player)) {
                    setState(PoliceState.CAUGHT);
                } else if (isPlayerEscaped(player)) {
                    setState(PoliceState.ESCAPED);
                }

                break;

            case TRANSITIONING:
                Gdx.app.log("Police", "State: TRANSITIONING");
                // Нічого не робити, очікувати, поки EventManager не перемістить поліцейського
                break;

            case ESCAPED:
                 Gdx.app.log("Police", "State: ESCAPED");
                 // Do nothing in these states.
                break;
            case CAUGHT:
                 Gdx.app.log("Police", "State: CAUGHT");
                 // Do nothing in these states.
                break;
            case IDLE:
                 Gdx.app.log("Police", "State: IDLE");
                 // Do nothing in these states.
                break;
        }
        return null; // No transition triggered
    }

    private boolean isPlayerCaught(Player player) {
        // Caught only if in the same world and near the player
        return player.getWorld() == this.getWorld() && isPlayerNear(player);
    }

   private boolean isPlayerEscaped(Player player) {
        if (player.getWorld() != this.getWorld()) {
            // Якщо поліція не рухається до переходу і не може знайти шлях, то гравець втік
            return !movingToTransition && activeTransition == null;
        }
        // Логіка для того ж світу: якщо гравець занадто далеко занадто довго
        if (!isPlayerNear(player, 800)) {
            lostTimer += Gdx.graphics.getDeltaTime();
            return lostTimer >= MAX_LOST_TIME;
        } else {
            lostTimer = 0f;
            return false;
        }
    }

    /**
     * Moves police toward the stored target coordinates.
     */
    private void chase() {
        if (state != PoliceState.CHASING) return;

        float delta = Gdx.graphics.getDeltaTime();

        // Move horizontally toward targetX
        if (this.getX() > targetX) {
            this.setX(this.getX() - getSpeed() * delta);
        } else if (this.getX() < targetX) {
            this.setX(this.getX() + getSpeed() * delta);
        }

        // Move vertically toward targetY
        if (this.getY() > targetY) {
            this.setY(this.getY() - getSpeed() * delta);
        } else if (this.getY() < targetY) {
            this.setY(this.getY() + getSpeed() * delta);
        }
    }

    public void startChase(Player player) {
        this.state = PoliceState.CHASING;
        Gdx.app.log("Police", "Starting chase on player in world: " + player.getWorld().getName());
        // Set initial target coordinates
        this.targetX = player.getX();
        this.targetY = player.getY();
    }

    public PoliceState getState() {
        return state;
    }

    public void setState(PoliceState state) {
        this.state = state;
        Gdx.app.log("Police", "State changed to: " + state);
    }
}
