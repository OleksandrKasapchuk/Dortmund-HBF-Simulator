package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class WorldMapUI extends Screen {

    private final Table layout;
    private final ShapeRenderer shapeRenderer;
    private final Skin skin;
    private final Map<String, Label> worldLabels = new HashMap<>();
    private final Map<String, com.badlogic.gdx.math.Vector2> worldPositions = new HashMap<>();

    public WorldMapUI(Skin skin) {
        this.skin = skin;
        this.shapeRenderer = new ShapeRenderer();
        this.layout = new Table();
        this.layout.setFillParent(true);
        getStage().addActor(this.layout);

        setupMap();
    }

    private void setupMap() {
        // Define abstract positions for your worlds to be visible on a 2000x1000 screen
        worldPositions.put("main", new com.badlogic.gdx.math.Vector2(950, 500));
        worldPositions.put("leopold", new com.badlogic.gdx.math.Vector2(950, 700));
        worldPositions.put("home", new com.badlogic.gdx.math.Vector2(1400, 300));
        worldPositions.put("subway", new com.badlogic.gdx.math.Vector2(1200, 500));
        worldPositions.put("kamp", new com.badlogic.gdx.math.Vector2(950, 300));
        worldPositions.put("club", new com.badlogic.gdx.math.Vector2(650, 700));

        // Create labels for each world
        for (Map.Entry<String, com.badlogic.gdx.math.Vector2> entry : worldPositions.entrySet()) {
            World world = WorldManager.getWorld(entry.getKey());
            if (world != null) {
                String worldName = world.getName();
                Label.LabelStyle style = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
                Label label = new Label(worldName, style);
                label.setPosition(entry.getValue().x, entry.getValue().y);
                getStage().addActor(label);
                worldLabels.put(worldName, label);
            }
        }
    }

    public void update() {
        // Highlight the current world
        World currentWorld = WorldManager.getCurrentWorld();
        if (currentWorld != null) {
            String currentWorldName = currentWorld.getName();
            for (Map.Entry<String, Label> entry : worldLabels.entrySet()) {
                if (entry.getKey().equals(currentWorldName)) {
                    entry.getValue().getStyle().fontColor = Color.YELLOW;
                } else {
                    entry.getValue().getStyle().fontColor = Color.WHITE;
                }
            }
        }
    }
    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}
