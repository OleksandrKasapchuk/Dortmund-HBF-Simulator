package com.mygame.managers;

import com.mygame.Assets;
import com.mygame.entity.Player;
import com.mygame.managers.audio.MusicManager;
import com.mygame.managers.audio.SoundManager;
import com.mygame.ui.UIManager;

public class PlayerEffectManager {
    private final Player player;
    private final UIManager uiManager;

    public PlayerEffectManager(Player player, UIManager uiManager) {
        this.player = player;
        this.uiManager = uiManager;
    }

    public void registerEffects() {
        player.getInventory().registerEffect("joint", this::applyJointEffect);
        player.getInventory().registerEffect("ice tee", this::applyIceTeeEffect);
    }

    private void applyJointEffect() {
        SoundManager.playSound(Assets.lighterSound);
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                if (player.getState() == Player.State.NORMAL)
                    uiManager.getGameUI().showInfoMessage("You got stoned", 1.5f);

                player.setStone();
                uiManager.getInventoryUI().update(player);
                MusicManager.playMusic(Assets.kaifMusic);
            }
        }, 4f);
    }

    private void applyIceTeeEffect() {
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                if (player.getState() == Player.State.STONED)
                    uiManager.getGameUI().showInfoMessage("You got normal", 1.5f);
                player.setNormal();
                uiManager.getInventoryUI().update(player);
                MusicManager.playMusic(Assets.backMusic1);
            }
        }, 0.5f);
    }
}
