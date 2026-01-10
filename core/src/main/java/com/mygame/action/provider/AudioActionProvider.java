package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.game.GameContext;

public class AudioActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("audio.playSound", (c, data) -> () -> SoundManager.playSound(Assets.getSound(data.getString("id"))));
        registry.registerCreator("audio.playMusic", (c, data) -> () -> MusicManager.playMusic(Assets.getMusic(data.getString("id"))));
    }
}
