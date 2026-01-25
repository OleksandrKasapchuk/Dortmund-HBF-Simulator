package com.mygame.events;

import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.game.DayManager;
import com.mygame.game.GameStateManager;

public class Events {

    private Events() {}

    // ───── Dialogue ─────
    public record DialogueFinishedEvent(String npcId) {}
    public record DialogueStartedEvent(String npcId) {}

    public record DarkOverlayEvent(float duration){}
    public record FireworkExplodedEvent(){}
    public record TransitionRequestedEvent(String targetWorldId, float targetX, float targetY){}
    // ───── Quest ─────
    public record QuestStartedEvent(String questId) {}
    public record QuestProgressEvent(String questId, int currentProgress, int maxProgress) {}
    public record QuestCompletedEvent(String questId) {}

    // ───── World & Interaction ─────
    public record WorldChangedEvent(String newWorldId) {}
    public record NewDayEvent(int newDayCount) {}
    public record PhaseChangedEvent(DayManager.Phase newPhase) {}

    public record InventoryChangedEvent(ItemDefinition item, int newAmount) {}
    public record ItemInteractionEvent(Item item, Player player){}
    public record ItemSearchedEvent(Player player, String itemKey, int amount) {}
    public record ItemUsedEvent(ItemDefinition item) {}
    public record CreateItemEvent(String itemKey, float x, float y) {}

    public record ActionRequestEvent(String actionId) {} // Нова подія
    public record InteractEvent() {}

    public record PlayerStateChangedEvent(Player.State newState) {}
    public record GameStateChangedEvent(GameStateManager.GameState newState) {}

    // ───── Police ─────
    public record PoliceStateChangedEvent(Police.PoliceState newState) {}

    // ───── Message ─────
    public record MessageEvent(String message) {}
    public record NotEnoughMessageEvent(ItemDefinition item) {}
    public record AddItemMessageEvent(ItemDefinition item, int amount) {}

    // ───── Save ─────
    public record SaveRequestEvent() {}
}
