package com.mygame.entity.player;

import com.badlogic.gdx.math.MathUtils;

public class PlayerAnimationController {
    private float proceduralTimer = 0f;
    private Player player;

    public PlayerAnimationController(Player player) {
        this.player = player;
    }

    public void update(float delta, boolean isMoving) {
        if (!isMoving) {
            resetAnimation();
            return;
        }

        proceduralTimer += delta * 15f; // Трохи швидше для впевненості

        // 1. Обертання: тільки 2-3 градуси, імітуємо нахил тіла при кроці
        player.setRotation(MathUtils.sin(proceduralTimer) * 3f);

        // 2. Squash & Stretch: тільки Y, щоб персонаж "присідав" при кроці
        // 0.05f - це дуже легке стиснення, ледь помітне
        float bounce = Math.abs(MathUtils.sin(proceduralTimer));
        player.setScaleY(1f - bounce * 0.08f);

        // Не чіпаємо ScaleX, або робимо його мінімальним
        player.setScaleX(1f);
    }

    private void resetAnimation() {
        proceduralTimer = 0;
        player.setRotation(0);
        player.setScaleX(1f);
        player.setScaleY(1f);
    }
}
