package com.mygame.entity.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.player.Player;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final Player player;

    // Direct references for special NPCs if needed
    private Police police;
    private NPC boss;
    private Police summonedPolice;

    public NpcManager(Player player) {
        this.player = player;
    }

    public void loadNpcsFromMap(World world) {
        MapLayer npcLayer = world.getMap().getLayers().get("npcs");
        if (npcLayer == null) return;

        for (MapObject object : npcLayer.getObjects()) {
            MapProperties props = object.getProperties();
            String npcId = props.get("name", String.class);
            if (npcId == null) continue;

            createNpcById(npcId, props, world);
        }
    }

    private void createNpcById(String npcId, MapProperties props, World world) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        Texture texture = Assets.getTexture(npcId.toLowerCase());

        if (texture == null) {
            System.err.println("Texture for '" + npcId + "' not found! Using fallback.");
            texture = Assets.getTexture("zoe");
        }

        NPC npc;
        GameSettings settings = SettingsManager.load();
        List<String> completedEvents = settings.completedDialogueEvents;

        DialogueNode initialDialogue = DialogueRegistry.getInitialDialogue(npcId.toLowerCase());

        String npcName;
        try {
            npcName = Assets.npcs.get("npc." + npcId.toLowerCase() + ".name");
        } catch (Exception e) {
            npcName = npcId; // Fallback to id
        }

        int directionX = props.get("directionX", 0, Integer.class);
        int directionY = props.get("directionY", 0, Integer.class);
        float pauseTime = props.get("pauseTime", 0f, Float.class);
        float moveTime = props.get("moveTime", 0f, Float.class);
        int speed = props.get("speed", 50, Integer.class);

        if (npcId.equalsIgnoreCase("police")) {
            this.police = new Police("police", npcName, 100, 100, x, y, texture, world, speed, initialDialogue);
            npc = this.police;
        } else {
            npc = new NPC(npcId.toLowerCase(), npcName, 100, 100, x, y, texture, world, directionX, directionY, pauseTime, moveTime, speed, initialDialogue);
        }

        // --- RESTORE NPC DIALOGUE STATE ---
        if (settings.npcDialogues != null && settings.npcDialogues.containsKey(npc.getId())) {
            String savedNode = settings.npcDialogues.get(npc.getId());
            npc.setDialogue(DialogueRegistry.getDialogue(npc.getId(), savedNode));
            npc.setCurrentDialogueNodeId(savedNode);
        }

        // --- SPECIFIC TEXTURE CHANGES (Still needed for now) ---
        if (npcId.equalsIgnoreCase("igo") && completedEvents.contains("igo_gave_vape")) {
            npc.setTexture(Assets.getTexture("igo2"));
        } else if (npcId.equalsIgnoreCase("boss")) {
            this.boss = npc;
        }

        npcs.add(npc);
        world.getNpcs().add(npc);
        System.out.println("SUCCESS: Loaded '" + npcId + "' from map in world '" + world.getName() + "' (Node: " + npc.getCurrentDialogueNodeId() + ")");
    }

    public void update(float delta) {
        World currentWorld = WorldManager.getCurrentWorld();
        if (currentWorld == null) return;
        for (NPC npc : new ArrayList<>(currentWorld.getNpcs())) {
            npc.update(delta);
        }
    }

    public NPC findNpcById(String id) {
        for (NPC npc : npcs) {
            if (npc.getId().equals(id)) return npc;
        }
        return null;
    }

    public void callPolice() {
        World currentWorld = WorldManager.getCurrentWorld();
        if (currentWorld == null) {
            System.err.println("NpcManager: Cannot call police, current world is null!");
            return;
        }

        summonedPolice = new Police("summoned_police", Assets.npcs.get("npc.police.name"),
            100, 100, player.getX(), player.getY() - 300, Assets.getTexture("police"),
            currentWorld, 200, DialogueRegistry.getDialogue("summoned_police", "beforeChase"));
        npcs.add(summonedPolice);
        currentWorld.getNpcs().add(summonedPolice);
    }

    public void moveSummonedPoliceToNewWorld(World newWorld) {
        if (summonedPolice != null && newWorld != null) {
            World oldWorld = summonedPolice.getWorld();
            if (oldWorld != null) {
                oldWorld.getNpcs().remove(summonedPolice);
            }
            summonedPolice.setWorld(newWorld);
            newWorld.getNpcs().add(summonedPolice);
        }
    }

    public NPC getBoss() { return boss; }
    public Police getPolice() { return police; }
    public Police getSummonedPolice(){ return summonedPolice; }

    public void kill(NPC npc) {
        if (npc == null) return;

        if (npc == summonedPolice) summonedPolice = null;
        if (npc == police) police = null;
        if (npc == boss) boss = null;

        if (npc.getWorld() != null) {
            npc.getWorld().getNpcs().remove(npc);
        }
        npcs.remove(npc);
    }
}
