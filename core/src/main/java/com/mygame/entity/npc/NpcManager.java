package com.mygame.entity.npc;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.player.Player;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final Player player;
    private final DialogueRegistry dialogueRegistry;
    private final WorldManager worldManager;

    public NpcManager(Player player, DialogueRegistry dialogueRegistry, WorldManager worldManager) {
        this.player = player;
        this.dialogueRegistry = dialogueRegistry;
        this.worldManager = worldManager;
    }

    public void loadNpcsFromMap(World world) {
        MapLayer npcLayer = world.getMap().getLayers().get("npcs");
        if (npcLayer == null) return;

        GameSettings settings = SettingsManager.load();
        for (MapObject object : npcLayer.getObjects()) {
            MapProperties props = object.getProperties();
            String npcId = props.get("name", String.class);
            if (npcId != null) {
                createNpcFromMap(npcId, props, world, settings);
            }
        }
    }

    private void createNpcFromMap(String npcId, MapProperties props, World world, GameSettings settings) {
        NPC npc = createNpcInstance(npcId, props, world);
        restoreNpcState(npc, settings);
        addNpcToGame(npc, world);
        System.out.println("SUCCESS: Loaded '" + npcId + "' from map (Node: " + npc.getCurrentDialogueNodeId() + ", Tex: " + npc.getCurrentTextureKey() + ")");
    }

    private NPC createNpcInstance(String npcId, MapProperties props, World world) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        Texture texture = getNpcTexture(npcId);
        String npcName = getNpcName(npcId);
        DialogueNode initialDialogue = dialogueRegistry.getInitialDialogue(npcId.toLowerCase());
        int speed = props.get("speed", 50, Integer.class);

        if ("police".equalsIgnoreCase(npcId)) {
            return new Police("police", npcName, 100, 100, x, y, texture, world, speed, initialDialogue);
        } else {
            int directionX = props.get("directionX", 0, Integer.class);
            int directionY = props.get("directionY", 0, Integer.class);
            float pauseTime = props.get("pauseTime", 0f, Float.class);
            float moveTime = props.get("moveTime", 0f, Float.class);
            return new NPC(npcId.toLowerCase(), npcName, 100, 100, x, y, texture, world, directionX, directionY, pauseTime, moveTime, speed, initialDialogue);
        }
    }

    private void restoreNpcState(NPC npc, GameSettings settings) {
        if (settings.npcStates != null && settings.npcStates.containsKey(npc.getId())) {
            GameSettings.NpcSaveData state = settings.npcStates.get(npc.getId());

            if (state.currentNode != null) {
                npc.setDialogue(dialogueRegistry.getDialogue(npc.getId(), state.currentNode));
                npc.setCurrentDialogueNodeId(state.currentNode);
            }

            if (state.currentTexture != null) {
                npc.setTexture(state.currentTexture);
            }
        }
    }

    private String getNpcName(String npcId) {
        try {
            return Assets.npcs.get("npc." + npcId.toLowerCase() + ".name");
        } catch (Exception e) {
            return npcId; // Fallback to id
        }
    }

    private Texture getNpcTexture(String npcId) {
        Texture texture = Assets.getTexture(npcId.toLowerCase());
        if (texture == null) {
            System.err.println("Texture for '" + npcId + "' not found! Using fallback.");
            texture = Assets.getTexture("zoe");
        }
        return texture;
    }

    private void addNpcToGame(NPC npc, World world) {
        npcs.add(npc);
        world.getNpcs().add(npc);
    }


    public void update(float delta) {
        World currentWorld = worldManager.getCurrentWorld();
        if (currentWorld == null) return;
        for (NPC npc : currentWorld.getNpcs()) {
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
        World currentWorld = worldManager.getCurrentWorld();
        if (currentWorld == null) return;

        Police summonedPolice = new Police("summoned_police", Assets.npcs.get("npc.police.name"),
                100, 100, player.getX(), player.getY() - 300, Assets.getTexture("police"),
                currentWorld, 200, dialogueRegistry.getDialogue("summoned_police", "chase.offer"));
        addNpcToGame(summonedPolice, currentWorld);
    }

    public void moveSummonedPoliceToNewWorld(World newWorld) {
        Police summonedPolice = getSummonedPolice();
        if (summonedPolice != null && newWorld != null) {
            World oldWorld = summonedPolice.getWorld();
            if (oldWorld != null) {
                oldWorld.getNpcs().remove(summonedPolice);
            }
            summonedPolice.setWorld(newWorld);
            newWorld.getNpcs().add(summonedPolice);
        }
    }

    public Police getSummonedPolice() {
        return (Police) findNpcById("summoned_police");
    }

    public void kill(NPC npc) {
        if (npc == null) return;

        if (npc.getWorld() != null) {
            npc.getWorld().getNpcs().remove(npc);
        }
        npcs.remove(npc);
    }
    public void teleportNpc(String npcId, World targetWorld, float x, float y) {
        NPC npc = findNpcById(npcId);
        if (npc == null || targetWorld == null) return;

        World oldWorld = npc.getWorld();

        if (oldWorld != null) {
            oldWorld.getNpcs().remove(npc);
        }

        npc.setWorld(targetWorld);
        npc.setX(x);
        npc.setY(y);

        if (!targetWorld.getNpcs().contains(npc)) {
            targetWorld.getNpcs().add(npc);
        }
    }
}
