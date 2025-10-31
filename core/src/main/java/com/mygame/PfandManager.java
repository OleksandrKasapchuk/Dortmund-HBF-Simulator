package com.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PfandManager {
    private ArrayList<Item> pfands = new ArrayList<>();
    private float spawnTimer = 0;
    private Random random = new Random();

    private ArrayList<float[]> spawnPoints = new ArrayList<>();

    public PfandManager() {
        spawnPoints.add(new float[]{100, 600});
        spawnPoints.add(new float[]{900, 400});
        spawnPoints.add(new float[]{1300, 600});
        spawnPoints.add(new float[]{1800, 250});
        spawnPoints.add(new float[]{3500, 700});
    }

    public void update(float delta, Player player, World world) {
        spawnTimer += delta;

        // Спавн раз на кілька секунд
        if (spawnTimer > 10f && pfands.size() <= 2) {
            spawnRandomPfand(world);
            spawnTimer = 0;
        }

        // Перевірка взаємодії з гравцем
        for (Iterator<Item> it = pfands.iterator(); it.hasNext();) {
            Item p = it.next();
            if (p.isPlayerNear(player)) {
                player.getInventory().addItem(p.getName(), 1);
                it.remove();
            }
        }
    }

    public void spawnRandomPfand(World world) {
        if (spawnPoints.isEmpty()) return;
        float[] pos = spawnPoints.get(random.nextInt(spawnPoints.size()));
        pfands.add(new Item("pfand", 75, 75, pos[0], pos[1], Assets.pfand, world, true));
    }

    public void draw(SpriteBatch batch) {
        for (Item p : pfands)
            p.draw(batch);
    }
}

