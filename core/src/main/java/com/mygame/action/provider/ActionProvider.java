package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.game.GameContext;

@FunctionalInterface
public interface ActionProvider {
    void provide(GameContext context, ActionRegistry registry);
}
