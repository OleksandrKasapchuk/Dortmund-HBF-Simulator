package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.world.WorldManager;

import java.util.List;

public class EntityRenderer {

    private final WorldManager worldManager;

    public EntityRenderer(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void renderEntities(SpriteBatch batch, List<? extends Renderable> entities) {
        if (worldManager.getCurrentWorld() == null) return;
        for (Renderable entity : entities) {
            if (entity.getWorld() != worldManager.getCurrentWorld()) continue;
            entity.draw(batch);
        }
    }
}
