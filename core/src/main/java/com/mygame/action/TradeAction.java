package com.mygame.action;


import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameContext;

public class TradeAction implements GameAction {

    private final GameContext ctx;
    private String fromItem, toItem;
    private int fromAmount, toAmount;

    public TradeAction(GameContext ctx, String fromItem, String toItem, int fromAmount, int toAmount) {
        this.ctx = ctx;
        this.fromItem = fromItem;
        this.toItem = toItem;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    @Override
    public void execute() {
        ctx.getInventory().trade(ItemRegistry.get(fromItem), ItemRegistry.get(toItem), fromAmount, toAmount);
    }
}
