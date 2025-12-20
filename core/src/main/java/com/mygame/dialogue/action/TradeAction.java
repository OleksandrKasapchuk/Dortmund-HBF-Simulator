package com.mygame.dialogue.action;


import com.mygame.entity.item.ItemRegistry;

public class TradeAction implements DialogueAction {

    private final ActionContext ctx;
    private String fromItem, toItem;
    private int fromAmount, toAmount;

    public TradeAction(ActionContext ctx, String fromItem, String toItem, int fromAmount, int toAmount) {
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
