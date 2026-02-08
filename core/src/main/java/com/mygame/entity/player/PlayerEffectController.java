package com.mygame.entity.player;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

public class PlayerEffectController {

    public enum EffectLevel {OK, WARNING, CRITICAL, COLLAPSE}

    public enum CameraShakeType {
        VIBE_CRITICAL(0.3f, 6f),
        VIBE_COLLAPSE(1.2f, 10f);

        public final float duration;
        public final float intensity;

        CameraShakeType(float d, float i) {
            duration = d;
            intensity = i;
        }
    }

    private EffectLevel hungerState = EffectLevel.OK;
    private EffectLevel thirstState = EffectLevel.OK;
    private EffectLevel vibeState   = EffectLevel.OK;
    private float collapseTimer = 0f;
    private static final float TIME_TO_DEATH = 5f;

    public void update(Player player, float delta) {
        PlayerStatusController stats = player.getStatusController();

        hungerState = evaluate(stats.getHunger());
        thirstState = evaluate(stats.getThirst());
        vibeState   = evaluate(stats.getVibe());

        applyEffects(player, delta);
    }

    private EffectLevel evaluate(float value) {
        if (value <= 0) return EffectLevel.COLLAPSE;
        if (value < 20) return EffectLevel.CRITICAL;
        if (value < 40) return EffectLevel.WARNING;
        return EffectLevel.OK;
    }

    private void applyEffects(Player player, float delta) {
        PlayerMovementController movement = player.getMovementController();
        float speedMultiplier = 1f;

        if (hungerState == EffectLevel.CRITICAL) speedMultiplier *= 0.8f;
        if (thirstState == EffectLevel.CRITICAL) speedMultiplier *= 0.6f;
        if (vibeState   == EffectLevel.CRITICAL) speedMultiplier *= 0.7f;

        movement.setSpeedMultiplier(speedMultiplier);

        switch (vibeState) {
            case CRITICAL -> EventBus.fire(CameraShakeType.VIBE_CRITICAL);
            case COLLAPSE -> EventBus.fire(CameraShakeType.VIBE_COLLAPSE);
        }


        boolean allCollapse = hungerState == EffectLevel.COLLAPSE
            && thirstState == EffectLevel.COLLAPSE
            && vibeState == EffectLevel.COLLAPSE;

        if (allCollapse) {
            collapseTimer += delta;
            if (collapseTimer >= TIME_TO_DEATH) {
                EventBus.fire(new Events.ActionRequestEvent("act.player.die"));
            }
        } else {
            collapseTimer = 0f;
        }

    }
}
