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

    // Persistent storage for NPCs talked to (could be saved/loaded in GameSettings)
    static GameSettings settings = SettingsManager.load();

    private static final Set<String> talkedNpcs = settings.talkedNpcs;
    private static final Set<String> visited = settings.visited;


    public static void init() {
        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));
        EventBus.subscribe(Events.WorldChangedEvent.class, event -> handleWorldChange(event.newWorldId()));
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
