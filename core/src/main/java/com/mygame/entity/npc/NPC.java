package com.mygame.entity.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.Entity;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.world.World;

/**
 * Basic NPC class that can move, pause, and interact with player via dialogue.
 * Handles simple back-and-forth movement and collision checks.
 */
public class NPC extends Entity {

    // --- Movement state ---
    private float timer = 0f;
    private boolean isPaused = false;
    private int directionX;
    private int directionY;

    // --- Behaviour settings ---
    private float pauseTime;
    private float moveTime;
    private int speed;

    // --- Identity & interaction ---
    private final String id;
    private final String name;
    private DialogueNode dialogue;
    private String currentDialogueNodeId = "start";
    private String currentTextureKey;
    private ItemManager itemManager;
    public NPC(
        String id, String name,
        int width, int height, float x, float y, Texture texture, World world,
        int directionX, int directionY, float pauseTime, float moveTime,
        int speed, DialogueNode dialogue, ItemManager itemManager
    ) {
        super(width, height, x, y, texture, world);
        this.id = id;
        this.name = name;
        this.dialogue = dialogue;
        this.currentTextureKey = id.toLowerCase();

        this.directionX = directionX;
        this.directionY = directionY;

        this.pauseTime = pauseTime;
        this.moveTime = moveTime;

        this.speed = speed;
        this.itemManager = itemManager;
    }

    @Override
    public void update(float delta) {

        // If NPC is static → do nothing
        if (moveTime == 0 || speed == 0) return;

        timer += delta;

        if (isPaused) {
            // --- Pause state ---
            if (timer >= pauseTime) {
                timer = 0f;
                isPaused = false;

                // Reverse direction after pause
                directionX *= -1;
                directionY *= -1;
            }
        } else {
            // --- Movement state ---
            float dx = directionX * speed * delta;
            float dy = directionY * speed * delta;

            Rectangle npcRect = new Rectangle(getX(), getY(), getWidth(), getHeight());

            // --- Collision detection on X ---
            npcRect.x += dx;
            if (!world.isCollidingWithMap(npcRect) && !isCollidingWithSolidItems(npcRect)) {
                this.setX(this.getX() + dx);
            }
            npcRect.x = getX(); // Reset X

            // --- Collision detection on Y ---
            npcRect.y += dy;
            if (!world.isCollidingWithMap(npcRect) && !isCollidingWithSolidItems(npcRect)) {
                this.setY(this.getY() + dy);
            }
            // npcRect.y is not reset because it's re-created on the next frame

            // After moving → switch to pause
            if (timer > moveTime) {
                isPaused = true;
                timer = 0f;
            }
        }
    }

    private boolean isCollidingWithSolidItems(Rectangle npcRect) {
        if (world == null) return false;
        for (Item item : itemManager.getAllItems()) {
            if (item.isSolid()) {
                Rectangle itemRect = new Rectangle(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                if (npcRect.overlaps(itemRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    // --- Dialogue ---
    public DialogueNode getDialogue() { return dialogue; }
    public void setDialogue(DialogueNode dialogue) { this.dialogue = dialogue; }

    public String getCurrentDialogueNodeId() { return currentDialogueNodeId; }
    public void setCurrentDialogueNodeId(String nodeId) { this.currentDialogueNodeId = nodeId; }

    // --- Texture ---
    public void setTexture(String textureKey) {
        this.currentTextureKey = textureKey;
        Texture tex = Assets.getTexture(textureKey);
        if (tex != null) {
            super.setTexture(tex);
        }
    }

    public String getCurrentTextureKey() {
        return currentTextureKey;
    }

    public String getId(){ return id; }
    public String getName() { return this.name; }
    public int getSpeed() { return speed; }
}
