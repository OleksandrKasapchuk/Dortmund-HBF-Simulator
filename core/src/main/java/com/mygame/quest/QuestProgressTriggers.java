package com.mygame.quest;

import com.mygame.action.ActionRegistry;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;

import java.util.Set;

/**
 * QuestProgressTriggers listens to game events and updates quest progress or status.
 */
public class QuestProgressTriggers {

    private static final String QUEST_FIREWORKS = "jan";
    private static final String QUEST_VISIT_LOCATIONS = "jason1";
    private static final String QUEST_TALK_NPCS = "jason2";
    private static final String ITEM_FIREWORK = "firework";

    private static Set<String> talkedNpcs;
    private static Set<String> visited;
    private static int lastFireworkCount = 0;

    public static void init() {
        refresh();

        // Прогрес квестів
        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));
        EventBus.subscribe(Events.WorldChangedEvent.class, event -> handleWorldChange(event.newWorldId()));
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> handleInventoryQuest(event.item(), event.newAmount()));

        // --- ОБРОБКА ЗАВЕРШЕННЯ КВЕСТУ ---
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            QuestManager.Quest quest = QuestManager.getQuest(event.questId());
            if (quest != null && quest.getOnComplete() != null) {
                // Виконуємо дію, назва якої вказана в JSON квесту
                ActionRegistry.executeAction(quest.getOnComplete());
            }
        });
    }

    public static void refresh() {
        GameSettings settings = SettingsManager.load();
        talkedNpcs = settings.talkedNpcs;
        visited = settings.visited;
        lastFireworkCount = 0;
    }

    private static void handleInventoryQuest(ItemDefinition item, int newAmount) {
        if (item == ItemRegistry.get(ITEM_FIREWORK)) {
            if (newAmount > lastFireworkCount) {
                progress(QUEST_FIREWORKS);
            }
            lastFireworkCount = newAmount;
        }
    }

    private static void handleNpcDialogue(String npcId) {
        if (talkedNpcs.add(npcId)) {
            progress(QUEST_TALK_NPCS);
        }
    }

    private static void handleWorldChange(String worldId) {
        if (visited.add(worldId)) {
            progress(QUEST_VISIT_LOCATIONS);
        }
    }

    private static void progress(String questKey) {
        QuestManager.Quest quest = QuestManager.getQuest(questKey);
        if (quest != null && quest.getStatus() == QuestManager.Status.ACTIVE) {
            quest.makeProgress();
        }
    }

    public static Set<String> getTalkedNpcs() { return talkedNpcs; }
    public static Set<String> getVisited() { return visited; }
}
