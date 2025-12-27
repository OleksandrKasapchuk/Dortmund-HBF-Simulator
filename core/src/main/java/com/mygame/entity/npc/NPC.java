package com.mygame.entity.npc;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.Entity;
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

    public NPC(
        String id, String name,
        int width, int height, float x, float y, Texture texture, World world,
        int directionX, int directionY, float pauseTime, float moveTime,
        int speed, DialogueNode dialogue
    ) {
        super(width, height, x, y, texture, world);
        this.id = id;
        this.name = name;
        this.dialogue = dialogue;

        this.directionX = directionX;
        this.directionY = directionY;

        this.pauseTime = pauseTime;
        this.moveTime = moveTime;

        this.speed = speed;
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
            float newX = this.getX() + directionX * speed * delta;
            float newY = this.getY() + directionY * speed * delta;

            // --- Collision detection on X ---
            boolean collideX =
                world.isSolid(newX, this.getY() - this.getHeight() - 20) ||
                    world.isSolid(newX + this.getWidth(), this.getY() - this.getHeight() - 20) ||
                    world.isSolid(newX, this.getY() - 20) ||
                    world.isSolid(newX + this.getWidth(), this.getY() - 20);

            if (!collideX) this.setX(newX);

            // --- Collision detection on Y ---
            boolean collideY =
                world.isSolid(this.getX(), newY - this.getHeight() - 20) ||
                    world.isSolid(this.getX() + this.getWidth(), newY - this.getHeight() - 20) ||
                    world.isSolid(this.getX(), newY - 20) ||
                    world.isSolid(this.getX() + this.getWidth(), newY - 20);

            if (!collideY) this.setY(newY);

            // After moving → switch to pause
            if (timer > moveTime) {
                isPaused = true;
                timer = 0f;
            }
        }
    }

    // --- Dialogue ---
    public DialogueNode getDialogue() { return dialogue; }
    public void setDialogue(DialogueNode dialogue) { this.dialogue = dialogue; }

    public String getCurrentDialogueNodeId() { return currentDialogueNodeId; }
    public void setCurrentDialogueNodeId(String nodeId) { this.currentDialogueNodeId = nodeId; }

    public String getId(){ return id; }
    public String getName() { return this.name; }
    public void setTexture(Texture texture) { this.texture = texture; }
    public int getSpeed() { return speed; }
}
