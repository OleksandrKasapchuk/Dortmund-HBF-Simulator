package com.mygame.ui.inGameUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

public class DarkOverlay {

    private final ShapeRenderer shapeRenderer;

    private enum State {HIDDEN, FADING_IN, VISIBLE, FADING_OUT}

    private State currentState = State.HIDDEN;
    private float currentAlpha = 0f;
    private float targetAlpha = 1f;
    private float fadeDuration = 0.1f;
    private float stayTimer = 0f;

    public DarkOverlay() {
        shapeRenderer = new ShapeRenderer();
        EventBus.subscribe(Events.DarkOverlayEvent.class, event -> show(1f, event.duration()));
    }

    /**
     * Показує затемнення з ефектом появи (fade-in).
     *
     * @param maxAlpha    Цільова прозорість (від 0.0 до 1.0).
     * @param staySeconds Скільки секунд екран залишатиметься видимим перед автоматичним зникненням.
     *                    Якщо 0, залишається видимим до виклику hide().
     */
    public void show(float maxAlpha, float staySeconds) {
        this.targetAlpha = MathUtils.clamp(maxAlpha, 0.5f, 1f);
        this.stayTimer = staySeconds;
        this.currentState = State.FADING_IN;
    }
    public void update(float delta) {
        if (fadeDuration <= 0) return;

        float alphaStep = (targetAlpha / fadeDuration) * delta;

        switch (currentState) {
            case FADING_IN:
                currentAlpha += alphaStep;
                if (currentAlpha >= targetAlpha) {
                    currentAlpha = targetAlpha;
                    currentState = State.VISIBLE;
                }
                break;

            case VISIBLE:
                if (stayTimer > 0) {
                    stayTimer -= delta;
                    if (stayTimer <= 0) {
                        currentState = State.FADING_OUT;
                    }
                }
                break;

            case FADING_OUT:
                currentAlpha -= alphaStep;
                if (currentAlpha <= 0) {
                    currentAlpha = 0;
                    currentState = State.HIDDEN;
                }
                break;
        }
    }

    public void render() {
        if (currentState == State.HIDDEN) {
            return;
        }

        // Вмикаємо змішування кольорів для підтримки прозорості
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Налаштовуємо матрицю проєкції для малювання в екранних координатах
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, currentAlpha);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Вимикаємо змішування, щоб не впливати на інший рендеринг
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
