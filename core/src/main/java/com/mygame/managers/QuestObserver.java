package com.mygame.managers;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.HashSet;
import java.util.Set;

/**
 * QuestObserver listens to game events and updates quest progress accordingly.
 * It also tracks which NPCs have been talked to within specific quest contexts.
 */
public class QuestObserver {

    // Persistent storage for NPCs talked to (could be saved/loaded in GameSettings)
    private static final Set<String> talkedNpcs = new HashSet<>();

    public static void init() {
        // Listen for finished dialogues
        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));

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
}
