package com.mygame.managers.nonglobal;

import com.mygame.Assets;
import com.mygame.entity.Player;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.managers.global.TimerManager;
import com.mygame.ui.UIManager;

/**
 * PlayerEffectManager is responsible for managing special effects that are triggered
 * when the player uses certain items.
 * Effects can include:
 *  - Changing player state (e.g., stoned, normal)
 *  - Playing sounds or music
 *  - Displaying messages on the UI
 */
public class PlayerEffectManager {
    private final Player player;
    private final UIManager uiManager;

    public PlayerEffectManager(Player player, UIManager uiManager) {
        this.player = player;
        this.uiManager = uiManager;
    }

    /**
     * Effect triggered by using a "joint".
     * - Plays a lighter sound
     * - After 4 seconds:
     *      - Sets player state to STONED
     *      - Updates the inventory UI
     *      - Shows an info message
     *      - Plays special "kaif" music
     */
    public void applyJointEffect() {
        SoundManager.playSound(Assets.lighterSound);
        TimerManager.setAction(() -> {
            if (player.getState() == Player.State.NORMAL)
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.joint"), 1.5f);

            player.setStone();
            MusicManager.playMusic(Assets.kaifMusic);
        }, 4f);
    }

    /**
     * Effect triggered by using an "ice tee".
     * - After 0.5 seconds:
     *      - Resets player state to NORMAL if previously STONED
     *      - Updates the inventory UI
     *      - Shows an info message
     *      - Plays background music
     */
    public void applyIceTeaEffect() {
        TimerManager.setAction(() -> {
            if (player.getState() == Player.State.STONED)
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.ice_tea"), 1.5f);

            player.setNormal();
            MusicManager.playMusic(Assets.backMusic1);
        }, 0.5f);
    }
}
