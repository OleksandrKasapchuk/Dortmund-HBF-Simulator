package com.mygame.entity.item.itemData;

import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;

public class SearchData {
    private boolean searched = false;
    private String rewardItemKey;
    private int rewardAmount;

    public SearchData() {}

    public SearchData(String rewardItemKey, int rewardAmount){
        this.rewardItemKey = rewardItemKey;
        this.rewardAmount = rewardAmount;
    }

    public void search(Player player) {
        if (searched) return;
        markSearched();
        // Замість ActionRegistry.executeAction викликаємо івент
        EventBus.fire(new Events.ActionRequestEvent("act.item.search.basic"));
        TimerManager.setAction(() -> {
            EventBus.fire(new Events.ItemSearchedEvent(player, rewardItemKey, rewardAmount));
            EventBus.fire(new Events.SaveRequestEvent());
        }, 2);
    }

    public boolean isSearched() {
        return searched;
    }

    public void markSearched() {
        this.searched = true;
    }

    public String getRewardItemKey() {
        return rewardItemKey;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }
}
