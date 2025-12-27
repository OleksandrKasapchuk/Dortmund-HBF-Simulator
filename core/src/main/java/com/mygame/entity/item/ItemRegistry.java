package com.mygame.entity.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.entity.player.PlayerEffectManager;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemDefinition> types = new HashMap<>();

    public static void init() {

        JsonReader reader = new JsonReader();
        JsonValue itemsArray = reader.parse(Gdx.files.internal("data/items.json"));

        for (JsonValue item : itemsArray) {

            String key = item.getString("key");
            boolean pickupable = item.getBoolean("pickupable", false);

            String name = null;
            String description = null;

            if (pickupable) {
                name = "item." + key + ".name";
                description = "item." + key + ".description";
            }

            Runnable effect = null;

            if (item.has("effect")) {
                effect = resolveEffect(
                    item.getString("effect")
                );
            }

            register(new ItemDefinition(key, name, description, pickupable, effect));
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

    private static void register(ItemDefinition type) {
        types.put(type.getKey(), type);
    }

    public static ItemDefinition get(String key) {
        return types.get(key);
    }
}
