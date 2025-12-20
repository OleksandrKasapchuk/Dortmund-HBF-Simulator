package com.mygame.events;

import com.mygame.entity.player.Player;
import com.mygame.entity.item.Item;

public class Events {

    private Events() {}

    // ───── Dialogue ─────
    public record DialogueFinishedEvent(String npcId) {}

    // ───── Quest ─────
    public record QuestStartedEvent(String questId) {}
    public record QuestProgressEvent(String questId, int currentProgress, int maxProgress) {}
    public record QuestCompletedEvent(String questId) {}

    // ───── World & Interaction ─────
    public record InteractionEvent(Item item, Player player) {}
    public record InventoryChangedEvent(String itemId, int newAmount) {}

    public record WorldChangedEvent(String newWorldId) {}
}
