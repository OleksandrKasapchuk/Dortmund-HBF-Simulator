package com.mygame.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygame.entity.Player;

public class CameraManager {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final float worldWidth;
    private final float worldHeight;

    public CameraManager(float worldWidth, float worldHeight) {
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(2000, 1000, camera);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void update(Player player, SpriteBatch batch) {
        if (player == null) return;

        float targetX = player.getX() + player.getWidth() / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;

        float cameraX = Math.max(camera.viewportWidth / 2f, Math.min(targetX, worldWidth - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f, Math.min(targetY, worldHeight - camera.viewportHeight / 2f));

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
    }

    public void resize(int width, int height) {viewport.update(width, height, true);}
    public OrthographicCamera getCamera() {return camera;}
}
