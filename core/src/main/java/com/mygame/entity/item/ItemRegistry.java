package com.mygame.entity.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.entity.player.PlayerEffectManager;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    public static void init() {

        JsonReader reader = new JsonReader();
        JsonValue itemsArray = reader.parse(Gdx.files.internal("data/items.json"));

        for (JsonValue item : itemsArray) {

            String key = item.getString("key");
            String name = item.getString("name");
            String description = item.getString("description");

            Runnable effect = null;

            if (item.has("effect")) {
                effect = resolveEffect(
                    item.getString("effect")
                );
            }

            register(new ItemType(key, name, description, effect));
        }
    }

    private static Runnable resolveEffect(String effectName) {

        return switch (effectName) {
            case "applyJointEffect" ->
                PlayerEffectManager::applyJointEffect;

            case "applyIceTeaEffect" ->
                PlayerEffectManager::applyIceTeaEffect;

            default ->
                throw new RuntimeException("Unknown item effect: " + effectName);
        };
    }

    private static void register(ItemType type) {
        types.put(type.getKey(), type);
    }

    public static ItemType get(String key) {
        return types.get(key);
    }
}

