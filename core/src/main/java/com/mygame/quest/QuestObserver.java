package com.mygame.quest;

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

    public static void init() {
        refresh();
        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));
        EventBus.subscribe(Events.WorldChangedEvent.class, event -> handleWorldChange(event.newWorldId()));
    }

    /**
     * Reloads settings from the disk to synchronize memory with the save file.
     * Call this when starting a New Game or loading a save.
     */
    public static void refresh() {
        GameSettings settings = SettingsManager.load();
        talkedNpcs = settings.talkedNpcs;
        visited = settings.visited;
    }

    private static void handleNpcDialogue(String npcId) {
        if (!talkedNpcs.contains(npcId)) {
            QuestManager.Quest jasonQuest = QuestManager.getQuest("jason2");
            if (jasonQuest != null) {
                jasonQuest.makeProgress();
                talkedNpcs.add(npcId);
                save();
            }
        }
    }

    private static void handleWorldChange(String worldId) {
        if (!visited.contains(worldId)) {
            QuestManager.Quest jasonQuest = QuestManager.getQuest("jason1");
            if (jasonQuest != null) {
                jasonQuest.makeProgress();
                visited.add(worldId);
                save();
            }
        }
    }

    private static void save() {
        GameSettings settings = SettingsManager.load();
        settings.talkedNpcs = talkedNpcs;
        settings.visited = visited;
        SettingsManager.save(settings);
    }

    public static Set<String> getTalkedNpcs() {
        return talkedNpcs;
    }

    public static Set<String> getVisited() {
        return visited;
    }
}
