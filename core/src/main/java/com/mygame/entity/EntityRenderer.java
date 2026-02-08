package com.mygame.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityRenderer {

    private final WorldManager worldManager;
    private final List<Renderable> renderables = new ArrayList<>();
    private final Comparator<Renderable> comparator = Comparator.comparing(Renderable::getY).reversed();
    private final OrthographicCamera camera;

    public EntityRenderer(WorldManager worldManager, OrthographicCamera camera) {
        this.worldManager = worldManager;
        this.camera = camera;
    }

    public void collectRenderableEntities(World world, Player player, List<NPC> npcs, List<Item> items) {
        renderables.clear();
        renderables.add(player);
        for (var npc : npcs)
            if (npc.getWorld() == world) renderables.add(npc);
        for (var item : items)
            if (item.getWorld() == world) renderables.add(item);
        renderables.sort(comparator);
    }


    public void renderUnsorted(SpriteBatch batch, List<? extends Renderable> entities) {
        if (worldManager.getCurrentWorld() == null) return;
        for (Renderable entity : entities) {
            if (entity.getWorld() != worldManager.getCurrentWorld()) continue;
            entity.draw(batch);
        }
    }

    public void renderWithSortedEntities(SpriteBatch batch, World world) {
        long startTime = System.nanoTime(); // старт таймера

        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camRight = camera.position.x + camera.viewportWidth / 2f;
        float camBottom = camera.position.y - camera.viewportHeight / 2f;
        float camTop = camera.position.y + camera.viewportHeight / 2f;

        List<TiledMapTileLayer> topLayers = world.getTopLayers();

        for (TiledMapTileLayer layer : topLayers) {
            int layerHeight = layer.getHeight();
            int layerWidth = layer.getWidth();

            int entityIndex = 0;

            for (int row = layerHeight - 1; row >= 0; row--) {
                // Малюємо ентіті на цьому рядку, якщо вони в зоні камери
                while (entityIndex < renderables.size()) {
                    Renderable entity = renderables.get(entityIndex);
                    int entityRow = (int) Math.floor(entity.getY() / world.tileHeight);
                    if (entityRow != row) break;

                    float ex = entity.getX();
                    float ey = entity.getY();
                    float ew = entity.getWidth();
                    float eh = entity.getHeight();

                    // Перевірка, чи ентіті в камері
                    if (ex + ew >= camLeft && ex <= camRight && ey + eh >= camBottom && ey <= camTop) {
                        entity.draw(batch);
                    }

                    entityIndex++;
                }

                // Малюємо тайли шару, тільки ті, що в камері
                int startCol = Math.max(0, (int) Math.floor(camLeft / world.tileWidth));
                int endCol = Math.min(layerWidth, (int) Math.ceil(camRight / world.tileWidth));

                for (int col = startCol; col < endCol; col++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                    if (cell != null) {
                        float x = col * world.tileWidth;
                        float y = row * world.tileHeight;

                        // Додаткова перевірка на видимість, на всяк випадок
                        if (x + world.tileWidth >= camLeft && x <= camRight &&
                            y + world.tileHeight >= camBottom && y <= camTop) {
                            batch.draw(cell.getTile().getTextureRegion(), x, y);
                        }
                    }
                }
            }
        }
        long endTime = System.nanoTime(); // кінець таймера
        float ms = (endTime - startTime) / 1_000_000f; // конвертація в мс
        Gdx.app.log("RenderDebug", "Frame render time: " + ms + " ms");
    }
}
