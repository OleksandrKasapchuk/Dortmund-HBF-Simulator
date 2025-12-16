package com.mygame.entity.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.managers.ManagerRegistry;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    public static void init(ManagerRegistry managerRegistry) {

        JsonReader reader = new JsonReader();
        JsonValue itemsArray = reader.parse(Gdx.files.internal("data/items.json"));

        for (JsonValue item : itemsArray) {

            String key = item.getString("key");
            String name = item.getString("name");
            String description = item.getString("description");

            Runnable effect = null;

            if (item.has("effect")) {
                effect = resolveEffect(
                    item.getString("effect"),
                    managerRegistry
                );
            }

            register(new ItemType(key, name, description, effect));
        }
    }

    private static Runnable resolveEffect(String effectName, ManagerRegistry registry) {

        return switch (effectName) {
            case "applyJointEffect" ->
                registry.getPlayerEffectManager()::applyJointEffect;

            case "applyIceTeaEffect" ->
                registry.getPlayerEffectManager()::applyIceTeaEffect;

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

