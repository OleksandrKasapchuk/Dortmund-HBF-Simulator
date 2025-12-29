package com.mygame.quest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class QuestRegistry {
    private static final Map<String, QuestDefinition> quests = new HashMap<>();

    public static void init() {
        quests.clear();
        JsonReader reader = new JsonReader();
        JsonValue questsArray = reader.parse(Gdx.files.internal("data/quests/quests.json"));

        for (JsonValue questJson : questsArray) {
            String key = questJson.getString("key");
            boolean progressable = questJson.getBoolean("progressable", false);
            int maxProgress = questJson.getInt("maxProgress", 1);
            String onComplete = questJson.getString("onComplete", null);

            register(new QuestDefinition(key, progressable, maxProgress, onComplete));
        }
    }

    private static void register(QuestDefinition definition) {
        quests.put(definition.key(), definition);
    }

    public static QuestDefinition get(String key) {
        return quests.get(key);
    }

    public static Collection<QuestDefinition> getDefinitions() {
        return quests.values();
    }

    public record QuestDefinition(String key, boolean progressable, int maxProgress, String onComplete) {}
}
