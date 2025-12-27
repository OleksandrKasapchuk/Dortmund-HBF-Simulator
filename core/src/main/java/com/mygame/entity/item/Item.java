package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.entity.Entity;
import com.mygame.world.World;

/**
 * Represents a world item (e.g., spoon, pfand).
 * Can be interactable, solid, and have pickup radius + interaction cooldown.
 */
public class Item extends Entity {

    // --- Item properties ---
    private ItemDefinition type;
    private boolean canBePickedUp;
    private boolean solid;
    private int distance;           // distance at which player can interact/pick up

    // --- Interaction cooldown ---
    private float cooldownTimer = 0f;

    public Item(
        ItemDefinition type, int width, int height,
        float x, float y, int distance,
        Texture texture, World world,
        boolean canBePickedUp, boolean solid
    ) {
        super(width, height, x, y, texture, world);
        this.type = type;
        this.canBePickedUp = canBePickedUp;
        this.solid = solid;
        this.distance = distance;
    }

    @Override
    public void update(float delta) {
        // Items do not move or update by default.
    }

    // --- Basic getters ---
    public String getName() { return type.getNameKey(); }
    public ItemDefinition getType() { return type; }
    public boolean canBePickedUp() { return canBePickedUp; }
    public boolean isSolid() { return solid; }
    public int getDistance() { return distance; }
    // --- Cooldown logic ---
    /**
     * Updates cooldown timer.
     */
    public void updateCooldown(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
    }

    /**
     * @return true if item can currently be interacted with.
     */
    public boolean canInteract() {
        return cooldownTimer <= 0;
    }
    /**
     * Starts cooldown after interaction.
     */
    public void startCooldown(float seconds) {
        cooldownTimer = seconds;
    }
}
