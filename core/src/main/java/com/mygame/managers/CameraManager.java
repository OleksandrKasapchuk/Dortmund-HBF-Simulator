package com.mygame.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.world.World;

/**
 * CameraManager handles the game's camera, including:
 * - Following the player.
 * - Clamping the camera to the world boundaries.
 * - Handling camera shake effects.
 * - Managing viewport resizing.
 */
public class CameraManager {
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final Player player;

    // Camera shake parameters
    private float shakeDuration = 0;
    private float shakeIntensity = 0;

    // Zoom parameters
    private float targetZoom = 1f;
    private float zoomSpeed = 3f; // швидкість плавного переходу

    private final float VIEWPORT_WIDTH = 2000f;
    private final float VIEWPORT_HEIGHT = 1000f;

    public CameraManager(Player player) {
        this.player = player;
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.apply();
        EventBus.subscribe(Events.DialogueStartedEvent.class, e -> setZoom(0.75f));
        EventBus.subscribe(Events.DialogueFinishedEvent.class, e -> setZoom(1f));
        EventBus.subscribe(Events.FireworkExplodedEvent.class, e -> {
            SoundManager.playSound(Assets.getSound("firework_explosion"));
            TimerManager.setAction(() -> shake(2, 20), 2f);
        });
    }

    /** Updates camera position and handles shake effects */
    public void update(float delta, World world) {
        // The alpha value determines how quickly the camera catches up to the player.
        // A smaller value means smoother, more delayed movement.
        float alpha = 0.1f;

        // Calculate the target position (center of the player)
        float targetX = player.getX() + player.getWidth() / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;

        // Smoothly interpolate the camera's position towards the target
        camera.position.x += (targetX - camera.position.x) * alpha;
        camera.position.y += (targetY - camera.position.y) * alpha;

        // --- Камера shake ---
        if (shakeDuration > 0) {
            float offsetX = (MathUtils.random() - 0.5f) * 2 * shakeIntensity;
            float offsetY = (MathUtils.random() - 0.5f) * 2 * shakeIntensity;
            camera.position.x += offsetX;
            camera.position.y += offsetY;
            shakeDuration -= delta;
        }
        camera.zoom += (targetZoom - camera.zoom) * zoomSpeed * delta;
        // Clamp camera to world boundaries
        clampCameraToWorld(world);

        camera.update();
    }

    /** Set target zoom (1f = default) */
    public void setZoom(float zoom) {
        targetZoom = MathUtils.clamp(zoom, 0.1f, 5f);
    }

    /** Trigger camera shake */
    public void shake(float duration, float intensity) {
        shakeDuration = duration;
        shakeIntensity = intensity;
    }

    /** Handles window resizing */
    public void resize(int width, int height, World world) {
        viewport.update(width, height, false);
        centerOnPlayer();
        clampCameraToWorld(world);
        camera.update();
    }

    private void centerOnPlayer() {
        float targetX = player.getX() + player.getWidth() / 2f;
        float targetY = player.getY() + player.getHeight() / 2f;
        camera.position.set(targetX, targetY, 0);
    }

    private void clampCameraToWorld(World world) {
        float halfViewportWidth = viewport.getWorldWidth() / 2f;
        float halfViewportHeight = viewport.getWorldHeight() / 2f;

        float maxX = world.mapWidth - halfViewportWidth;
        float maxY = world.mapHeight - halfViewportHeight;

        camera.position.x = MathUtils.clamp(camera.position.x, halfViewportWidth, maxX);
        camera.position.y = MathUtils.clamp(camera.position.y, halfViewportHeight, maxY);
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
