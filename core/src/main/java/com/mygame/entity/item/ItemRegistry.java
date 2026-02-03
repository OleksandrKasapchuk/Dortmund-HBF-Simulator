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

            String name = "item." + key + ".name";
            String description = "item." + key + ".description";

            String effect = null;

            if (item.has("effect") && item.getBoolean("effect")) {
                effect = "act.item." + key + ".use";
            }
            int width = item.getInt("width", 64);
            int height = item.getInt("height", 64);

            register(new ItemDefinition(key, name, description, effect, width, height, pickupable));
        }
    }

    private void register(ItemDefinition type) {
        types.put(type.getKey(), type);
    }

    public ItemDefinition get(String key) {
        return types.get(key);
    }
}
