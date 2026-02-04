package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.assets.Assets;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.quest.QuestManager;
import com.mygame.world.WorldManager;

public class InWorldUIRenderer {
    private final SpriteBatch batch;
    private final Player player;
    private final QuestManager questManager;
    private final WorldManager worldManager;
    private final NpcManager npcManager;
    private final ItemManager itemManager;
    private final GlyphLayout layout = new GlyphLayout();
    public InWorldUIRenderer(SpriteBatch batch, Player player, QuestManager questManager, WorldManager worldManager, NpcManager npcManager, ItemManager itemManager) {
        this.batch = batch;
        this.player = player;
        this.questManager = questManager;
        this.worldManager = worldManager;
        this.npcManager = npcManager;
        this.itemManager = itemManager;
    }

    public void renderWorldElements() {
        // NPC
        for (NPC npc : npcManager.getNpcs()) {
            if (npc.getWorld() != worldManager.getCurrentWorld()) continue;
            if (npc.isPlayerNear(player)) {
                Assets.myFont.draw(batch, Assets.ui.get("interact"), npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }

        for (Item item : itemManager.getAllItems()) {
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;

            boolean nearPlayer = item.isPlayerNear(player, item.getDistance());
            boolean questActive = item.getQuestId() != null && !questManager.hasQuest(item.getQuestId());

            // Перевіряємо наявність data
            boolean hasSearchData = item.getSearchData() != null && !item.getSearchData().isSearched();

            // Якщо немає жодної data або не пройдені умови — пропускаємо
            if (questActive || !nearPlayer || !item.isInteractable()) continue;

            // Вибір тексту в залежності від того, яка data є
            if (hasSearchData) {
                drawText(Assets.ui.get("interact.search"), item.getCenterX(), item.getCenterY() + 20);
            } else if (item.getInteractionData() != null){ // тільки InteractionData
                drawText(Assets.ui.get("interact"), item.getCenterX(), item.getCenterY() + 20);
            }
        }
    }

    public void drawText(String text, float x, float y) {
        layout.setText(Assets.myFont, text);
        Assets.myFont.draw(batch, text, x - layout.width / 2f, y + 60);
    }
}
