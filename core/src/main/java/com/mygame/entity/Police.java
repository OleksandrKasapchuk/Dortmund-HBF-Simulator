package com.mygame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygame.Dialogue;
import com.mygame.world.World;


public class Police extends NPC {
    public enum PoliceState { IDLE, CHASING, ESCAPED, CAUGHT }
    private PoliceState state = PoliceState.IDLE;

    public Police(String name, int width, int height, float x, float y, Texture texture, World world, int speed, int distance, Dialogue dialogue) {
        super(name, width, height, x, y, texture, world, 0, 0, 0f, 0f, speed, distance, dialogue);
    }

    public void update(Player player) {
        switch (state) {
            case CHASING:
                chasePlayer(player);
                if (isPlayerCaught(player)) state = PoliceState.CAUGHT;
                else if (isPlayerEscaped(player)) state = PoliceState.ESCAPED;
                break;
            case ESCAPED:
            case CAUGHT:
            case IDLE:
                break;
        }
    }

    private boolean isPlayerCaught(Player player) {return isPlayerNear(player);}

    private boolean isPlayerEscaped(Player player) {return !isPlayerNear(player,1200);}

    public void chasePlayer(Player player) {
        if (state == PoliceState.CHASING) {
            float delta = Gdx.graphics.getDeltaTime();

            float targetX = player.getX();
            float targetY = player.getY();

            if (this.getX() > targetX) this.setX(this.getX() - getSpeed() * delta);
            else if (this.getX() < targetX) this.setX(this.getX() + getSpeed() * delta);

            if (this.getY() > targetY) this.setY(this.getY() - getSpeed() * delta);
            else if (this.getY() < targetY) this.setY(this.getY() + getSpeed() * delta);

        }
    }
    public void startChase() { state = PoliceState.CHASING; }
    public PoliceState getState() { return state; }
}
