package com.mygame.dialogue.action.condition;

import com.mygame.assets.Assets;
import com.mygame.dialogue.action.ActionContext;
import com.mygame.entity.item.ItemRegistry;

public class HasItemCondition implements ConditionAction {

    private final ActionContext ctx;
    private final String itemId;
    private final String nameKey;

    public HasItemCondition(ActionContext ctx, String itemId, String nameKey) {
        this.ctx = ctx;
        this.itemId = itemId;
        this.nameKey = nameKey;
    }
    @Override
    public boolean check() {
        if (!ctx.getInventory().hasItem(ItemRegistry.get(itemId))) {
            ctx.ui.showNotEnough(Assets.bundle.get(nameKey));
            return false;
        }
        return true;
    }
}
