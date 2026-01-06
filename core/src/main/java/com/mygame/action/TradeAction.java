package com.mygame.action;


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
        ctx.getInventory().trade(ctx.itemRegistry.get(fromItem), ctx.itemRegistry.get(toItem), fromAmount, toAmount);
    }
}
