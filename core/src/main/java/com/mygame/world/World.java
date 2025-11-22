package com.mygame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.Assets;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.item.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class World {
    public int tileSize = 100;
    private Block[][] blocks;
    private String name;

    private ArrayList<Transition> transitions = new ArrayList<>();
    private ArrayList<NPC> npcs = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Item> pfands = new ArrayList<>();


    public World(String name, String pathToMapFile) {
        this.name = name;
        int[][] map = readMapFromFile(pathToMapFile);
        blocks = new Block[map.length][map[0].length];

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 1) {
                    blocks[y][x] = new Block(true, Assets.brick);
                } else if (map[y][x] == 2) {
                    blocks[y][x] = new Block(false, Assets.bush);
                }
            }
        }
    }

    private int[][] readMapFromFile(String filePath) {
        ArrayList<int[]> tempMap = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Gdx.files.internal(filePath).read()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.trim().split(",\\s*");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }
                tempMap.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int[][] map = new int[tempMap.size()][];
        for (int i = 0; i < tempMap.size(); i++) {
            map[i] = tempMap.get(i);
        }
        return map;
    }


    public boolean isSolid(float x, float y) {
        int tileX = (int) (x / tileSize);
        int tileY = (int) ((blocks.length - 1) - (y / tileSize));

        if (tileY < 0 || tileY >= blocks.length || tileX < 0 || tileX >= blocks[0].length)
            return true;

        Block block = blocks[tileY][tileX];
        return block != null && block.issolid;
    }

    public void draw(SpriteBatch batch, BitmapFont font, Player player) {
        for (int y = 0; y < blocks.length; y++) {
            for (int x = 0; x < blocks[0].length; x++) {
                Block block = blocks[y][x];
                if (block != null) {
                    batch.draw(block.texture, x * tileSize, (blocks.length - 1 - y) * tileSize, tileSize, tileSize);
                }
            }
        }
        for (NPC npc : npcs) {
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, Assets.bundle.get("interact.npc"), npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }
        for (Item item : items)
            item.draw(batch);
    }

    public void drawTransitions(ShapeRenderer shapeRenderer) {
        for (Transition t : transitions) {
            t.drawDebug(shapeRenderer);
        }
    }

    public Block[][] getBlocks() {return blocks;}
    public String getName(){return name;}
    public ArrayList<Transition> getTransitions(){return transitions;}
    public void addTransition(Transition transition){transitions.add(transition);}
    public ArrayList<NPC> getNpcs() { return npcs; }
    public ArrayList<Item> getItems(){ return items; }
    public ArrayList<Item> getPfands(){ return pfands; }
}
