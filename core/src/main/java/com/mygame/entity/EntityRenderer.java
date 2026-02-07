package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.Comparator;
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

    public void renderSorted(SpriteBatch batch, List<Renderable> entities) {
        if (worldManager.getCurrentWorld() == null) return;

        List<Renderable> toRender = new ArrayList<>();
        for (Renderable entity : entities) {
             if (entity.getWorld() == worldManager.getCurrentWorld()) {
                toRender.add(entity);
            }
        }

        toRender.sort(Comparator.comparing(Renderable::getY).reversed());

        for (Renderable entity : toRender) {
            entity.draw(batch);
        }
    }
}
