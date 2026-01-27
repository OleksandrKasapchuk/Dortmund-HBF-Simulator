package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.entity.Entity;
import com.mygame.entity.item.itemData.InteractionData;
import com.mygame.entity.item.itemData.SearchData;
import com.mygame.entity.player.Player;
import com.mygame.world.World;

/**
 * Represents a world item (e.g., spoon, pfand).
 */
public class Item extends Entity {

    private final String id;
    private ItemDefinition type;
    private boolean canBePickedUp;
    private boolean solid;
    private int distance;
    private String questId;
    private final boolean isDynamic;

    private InteractionData interactionData;
    private SearchData searchData;

    public Item(String id, ItemDefinition type, int width, int height, float x, float y, Texture texture, World world,
        boolean canBePickedUp, boolean solid,  String questId, boolean isDynamic) {
        super(width, height, x, y, texture, world);
        this.id = id;
        this.type = type;
        this.canBePickedUp = canBePickedUp;

        float baseDistance = 80f; // базова дистанція
        this.distance = (int)(baseDistance + (width + height) / 4f); // додаємо розмір об'єкта (половина ширини + половина висоти) / 2
        this.solid = solid;
        this.questId = questId;

        this.isDynamic = isDynamic;
    }

    @Override
    public void update(float delta) {}

    public void interact(Player player){
        if (interactionData != null){
            interactionData.interact(player);
        } else if(searchData != null){
            searchData.search(player);
        }
    }
    public int getDistance() {
        return distance;
    }
    public String getId() {
        return id;
    }

    public ItemDefinition getType() { return type; }
    public boolean canBePickedUp() { return canBePickedUp; }
    public boolean isSolid() { return solid; }

    public SearchData getSearchData(){return searchData;}
    public InteractionData getInteractionData(){return interactionData;}
    public void setInteractionData(InteractionData interactionData){this.interactionData = interactionData;}
    public void setSearchData(SearchData searchData){this.searchData = searchData;}

    public String getQuestId(){ return questId; }
    public boolean isDynamic() { return isDynamic; }
}
