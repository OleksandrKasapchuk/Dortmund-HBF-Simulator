package com.mygame.action.provider;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.action.ActionRegistry;
import com.mygame.assets.Assets;
import com.mygame.entity.PlantEntity;
import com.mygame.game.GameContext;
import com.mygame.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldActionProvider implements ActionProvider {

    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("world.createPlant", (c, data) -> () -> {
            String plantKey = data.getString("plantKey");
            if (plantKey == null || plantKey.isEmpty()) {
                return; // Or handle the error appropriately
            }

            // This assumes you have a way to get the interacted object's position.
            // You might need to adjust this depending on how you track the interaction context.
            float x = c.player.getX(); // Placeholder for interacted object's X
            float y = c.player.getY(); // Placeholder for interacted object's Y
            World world = c.worldManager.getCurrentWorld();

            // This is a simplified example of how you might get your textures.
            // You'll likely want a more robust asset loading system.
            Map<PlantEntity.Phase, Texture> textures = new HashMap<>();
            textures.put(PlantEntity.Phase.SEED, Assets.getTexture("plant_1"));
            textures.put(PlantEntity.Phase.SPROUT, Assets.getTexture("plant_2"));
            textures.put(PlantEntity.Phase.FLOWERING, Assets.getTexture("plant_3"));
            textures.put(PlantEntity.Phase.HARVESTABLE, Assets.getTexture("plant_4"));

            // You would also fetch other plant-specific data here, like growth time.
            float growthTime = 60f; // 60 seconds per phase, for example

            PlantEntity plant = new PlantEntity(x, y, world, textures, growthTime);
            world.addEntity(plant); // You'll need to implement this method in your World class.
        });
    }
}
