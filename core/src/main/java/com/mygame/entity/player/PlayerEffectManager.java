package com.mygame.entity.player;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.managers.TimerManager;
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
    private static Player player;
    private static UIManager uiManager;

    public static void init(Player p, UIManager ui) {
        player = p;
        uiManager = ui;
    }

    public static void applyJointEffect() {
        SoundManager.playSound(Assets.getSound("lighterSound"));
        TimerManager.setAction(() -> {
            if (player.getState() == Player.State.NORMAL)
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.joint"), 1.5f);

            player.setStone();
        }, 4f);
    }

    public static void applyIceTeaEffect() {
        TimerManager.setAction(() -> {
            if (player.getState() == Player.State.STONED)
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.ice_tea"), 1.5f);

            player.setNormal();
            MusicManager.playMusic(Assets.getMusic("backMusic1"));
        }, 0.5f);
    }
}
