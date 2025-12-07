package com.mygame.entity.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.Assets;
import com.mygame.dialogue.DialogueActionRegistry;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.player.Player;
import com.mygame.managers.global.save.GameSettings;
import com.mygame.managers.global.save.SettingsManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.ui.UIManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final Player player;
    private final DialogueRegistry dialogueRegistry;

    // Direct references for special NPCs if needed
    private Police police;
    private NPC boss;
    private Police summonedPolice;

    public NpcManager(Player player, UIManager uiManager) {
        this.player = player;
        this.dialogueRegistry = new DialogueRegistry();
        DialogueActionRegistry.registerAll(dialogueRegistry, player, uiManager, this);
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

        DialogueNode initialDialogue = dialogueRegistry.getInitialDialogue(npcId.toLowerCase());

        String npcName;
        try {
            npcName = Assets.bundle.get("npc." + npcId.toLowerCase() + ".name");
        } catch (Exception e) {
            npcName = npcId; // Fallback to id
        }

        int directionX = props.get("directionX", 0, Integer.class);
        int directionY = props.get("directionY", 0, Integer.class);
        float pauseTime = props.get("pauseTime", 0f, Float.class);
        float moveTime = props.get("moveTime", 0f, Float.class);
        int speed = props.get("speed", 50, Integer.class);
        int distance = props.get("distance", 150, Integer.class);

        if (npcId.equalsIgnoreCase("police")) {
            this.police = new Police(npcName, 100, 100, x, y, texture, world, 0, 100, initialDialogue);
            npc = this.police;
        } else {
            npc = new NPC(npcName, 100, 100, x, y, texture, world, directionX, directionY, pauseTime, moveTime, speed, distance, initialDialogue);
        }

        if (npcId.equalsIgnoreCase("igo") && completedEvents.contains("igo_gave_vape")) {
            npc.setDialogue(dialogueRegistry.getDialogue("igo", "thanks"));
            npc.setTexture(Assets.getTexture("igo2"));
        }
        if (npcId.equalsIgnoreCase("ryzhyi") && completedEvents.contains("ryzhyi_gave_money")) {
            npc.setDialogue(dialogueRegistry.getDialogue("ryzhyi", "after"));
        }
        if (npcId.equalsIgnoreCase("boss")) {
            this.boss = npc;
            if (completedEvents.contains("boss_gave_quest")) {
                npc.setDialogue(dialogueRegistry.getDialogue("boss", "after"));
            }
        }
        if (npcId.equalsIgnoreCase("jason") && completedEvents.contains("jason_gave_money")) {
            npc.setDialogue(dialogueRegistry.getDialogue("jason", "after"));
        }

        npcs.add(npc);
        world.getNpcs().add(npc);
        System.out.println("SUCCESS: Loaded '" + npcId + "' from map in world '" + world.getName() + "'");
    }

    public void update(float delta) {
        for (NPC npc : new ArrayList<>(WorldManager.getCurrentWorld().getNpcs())) {
            npc.update(delta);
        }
    }

    public NPC findNpcByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equals(name)) return npc;
        }
        return null;
    }

    public void callPolice() {
        summonedPolice = new Police(Assets.bundle.get("npc.police.name"), 100, 100, player.getX(), player.getY() - 300, Assets.getTexture("police"), WorldManager.getCurrentWorld(), 200, 100, new DialogueNode("dialogue.police.called"));
        npcs.add(summonedPolice);
        WorldManager.getCurrentWorld().getNpcs().add(summonedPolice);
    }

    public void moveSummonedPoliceToNewWorld(World newWorld) {
        if (summonedPolice != null) {
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
