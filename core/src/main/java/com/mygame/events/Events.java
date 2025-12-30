package com.mygame.events;

import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;

public class Events {

    private Events() {}

    // ───── Dialogue ─────
    public record DialogueFinishedEvent(String npcId) {}

    // ───── Quest ─────
    public record QuestStartedEvent(String questId) {}
    public record QuestProgressEvent(String questId, int currentProgress, int maxProgress) {}
    public record QuestCompletedEvent(String questId) {}

    // ───── World & Interaction ─────
    public record InventoryChangedEvent(ItemDefinition item, int newAmount) {}
    public record WorldChangedEvent(String newWorldId) {}
    public record ItemInteractionEvent(Item item, Player player){}

    public record ItemUsedEvent(ItemDefinition item) {}
    public record PlayerStateChangedEvent(Player.State newState) {}

    // ───── Player)
    // ───── Police ─────
    public record PoliceStateChangedEvent(Police.PoliceState newState) {}

    // ───── Message ─────
    public record MessageEvent(String message, float duration) {}
    public record NotEnoughMessageEvent(ItemDefinition item) {}
    public record AddItemMessageEvent(ItemDefinition item, int amount) {}
}
