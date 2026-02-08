package com.mygame.entity.player;

import com.mygame.assets.Assets;

public class PlayerAnimationController {
    public enum WalkFrame {
        IDLE,
        LEFT,
        RIGHT
    }
    private WalkFrame currentWalkFrame = WalkFrame.IDLE;

    private float walkAnimTimer = 0f;
    private float walkAnimInterval = 0.1f; // швидкість анімації
    private float walkBobbingTimer = 0f; // таймер для коливань
    private final int baseHeight; // базова висота

    private boolean stepRightNext = true; // щоб чергувати ноги
    private Player player;

    public PlayerAnimationController(Player player, int baseHeight) {
        this.baseHeight = baseHeight;
        this.player = player;
    }

    public void update(float delta, boolean isMoving) {
        updateWalkAnimation(delta, isMoving);
        updateTexture(isMoving);
    }

    private void updateWalkAnimation(float delta, boolean isMoving) {
        if (!isMoving) {
            currentWalkFrame = WalkFrame.IDLE;
            walkAnimTimer = 0f;
            walkBobbingTimer = 0f; // Скидаємо таймер коливань
            return;
        }

        walkAnimTimer += delta;
        walkBobbingTimer += delta; // Оновлюємо таймер коливань

        if (walkAnimTimer >= walkAnimInterval) {
            walkAnimTimer = 0f;

            if (currentWalkFrame == WalkFrame.IDLE) {
                currentWalkFrame = stepRightNext ? WalkFrame.RIGHT : WalkFrame.LEFT;
            } else {
                currentWalkFrame = WalkFrame.IDLE;
                stepRightNext = !stepRightNext;
            }
        }
    }

    private void updateTexture(boolean isMoving) {
        if (!isMoving) {
            player.setTexture(Assets.getTexture("zoe.3d"));
            player.setHeight(baseHeight);
            return;
        }

        // Плавна зміна висоти за допомогою синусоїди
        float bobbingAmount = (float) (Math.sin(walkBobbingTimer * 20f) * 5f);
        player.setHeight(baseHeight + (int) bobbingAmount);

        switch (currentWalkFrame) {
            case LEFT:
                player.setTexture(Assets.getTexture("zoe.3d_left"));
                break;
            case RIGHT:
                player.setTexture(Assets.getTexture("zoe.3d_right"));
                break;
            default:
                // Залишаємось на останній текстурі кроку, поки не зупинимось
                // Це запобігає мерехтінню до idle текстури під час ходьби
                break;
        }
    }
}
