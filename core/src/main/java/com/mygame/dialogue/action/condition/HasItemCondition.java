package com.mygame.dialogue.action.condition;

import com.mygame.assets.Assets;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;

public class HasItemCondition implements ConditionAction {

    private final GameContext ctx;
    private final String itemId;
    private final String nameKey;

    public HasItemCondition(GameContext ctx, String itemId, String nameKey) {
        this.ctx = ctx;
        this.itemId = itemId;
        this.nameKey = nameKey;
    }
    @Override
    public boolean check() {
        if (!ctx.getInventory().hasItem(ItemRegistry.get(itemId))) {
            ctx.ui.showNotEnough(Assets.items.get(nameKey));
            return false;
        }
        return true;
    }
}
