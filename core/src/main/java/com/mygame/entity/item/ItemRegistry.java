package com.mygame.entity.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private final Map<String, ItemDefinition> types = new HashMap<>();

    public ItemRegistry() {

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

            String effect = null;

            if (item.has("effect")) {
                effect = item.getString("effect");
            }

            register(new ItemDefinition(key, name, description, effect));
        }
    }

    private void register(ItemDefinition type) {
        types.put(type.getKey(), type);
    }

    public ItemDefinition get(String key) {
        return types.get(key);
    }
}
