package com.mygame.quest;

import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;

import java.util.Set;

/**
 * QuestObserver listens to game events and updates quest progress accordingly.
 * It also tracks which NPCs have been talked to within specific quest contexts.
 */
public class QuestObserver {

    private static Set<String> talkedNpcs;
    private static Set<String> visited;
    private static int lastFireworkCount = 0;

    public static void init() {
        refresh();
        // Clear previous subscriptions if any (depending on your EventBus implementation)
        // If your EventBus doesn't support clearing, ensure init() is called only once.

        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));
        EventBus.subscribe(Events.WorldChangedEvent.class, event -> handleWorldChange(event.newWorldId()));
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> handleJanQuest(event.item(), event.newAmount()));
    }

    /**
     * Reloads settings from the disk to synchronize memory with the save file.
     * Call this when starting a New Game or loading a save.
     */
    public static void refresh() {
        GameSettings settings = SettingsManager.load();
        talkedNpcs = settings.talkedNpcs;
        visited = settings.visited;
        lastFireworkCount = 0; // Reset tracking on refresh
    }

    public static void handleJanQuest(ItemDefinition item, int newAmount) {
        QuestManager.Quest quest = QuestManager.getQuest("jan");
        if (quest != null && item == ItemRegistry.get("firework")) {
            // Only progress if the amount actually increased
            if (newAmount > lastFireworkCount) {
                quest.makeProgress();
            }
            lastFireworkCount = newAmount;
        }
    }

    private static void handleNpcDialogue(String npcId) {
        if (!talkedNpcs.contains(npcId)) {
            QuestManager.Quest jasonQuest = QuestManager.getQuest("jason2");
            if (jasonQuest != null) {
                jasonQuest.makeProgress();
                talkedNpcs.add(npcId);
            }
        }
    }

    private static void handleWorldChange(String worldId) {
        if (!visited.contains(worldId)) {
            QuestManager.Quest jasonQuest = QuestManager.getQuest("jason1");
            if (jasonQuest != null) {
                jasonQuest.makeProgress();
                visited.add(worldId);
            }
        }
    }

    public static Set<String> getTalkedNpcs() {
        return talkedNpcs;
    }

    public static Set<String> getVisited() {
        return visited;
    }
}
