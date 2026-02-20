package com.mygame.entity.npc;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.player.Player;
import com.mygame.game.save.data.ServerSaveData;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.Map;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final Player player;
    private final DialogueRegistry dialogueRegistry;
    private final WorldManager worldManager;
    private final ItemManager itemManager;

    public NpcManager(Player player, DialogueRegistry dialogueRegistry, WorldManager worldManager, ItemManager itemManager) {
        this.player = player;
        this.dialogueRegistry = dialogueRegistry;
        this.worldManager = worldManager;
        this.itemManager = itemManager;
    }

    public void loadNpcsFromMap(World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        MapLayer npcLayer = world.getMap().getLayers().get("npcs");
        if (npcLayer == null) return;

        for (MapObject object : npcLayer.getObjects()) {
            MapProperties props = object.getProperties();
            String npcId = object.getName();
            if (npcId != null && findNpcById(npcId.toLowerCase()) == null) {
                createNpcFromMap(npcId, props, world, npcStates);
            }
        }
    }

    private void createNpcFromMap(String npcId, MapProperties props, World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        NPC npc = createNpcInstance(npcId, props, world, npcStates);
        restoreNpcState(npc, npcStates);
        npcs.add(npc);
        System.out.println("SUCCESS: Loaded '" + npcId + "' from map (Node: " + npc.getCurrentDialogueNodeId() + ", Tex: " + npc.getCurrentTextureKey() + ")");
    }

    private NPC createNpcInstance(String npcId, MapProperties props, World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        World targetWorld = world;
        String npcIdLower = npcId.toLowerCase();

        if (npcStates != null && npcStates.containsKey(npcIdLower)) {
            ServerSaveData.NpcSaveData state = npcStates.get(npcIdLower);
            if (state.currentWorld != null) {
                World savedWorld = worldManager.getWorld(state.currentWorld);
                if (savedWorld != null) {
                    targetWorld = savedWorld;
                    x = state.x;
                    y = state.y;
                }
            }
        }

        String textureKey = (Assets.getTexture(npcId + ".3d") != null) ? npcId + ".3d" : npcId;

        String npcName = getNpcName(npcId);
        DialogueNode initialDialogue = dialogueRegistry.getInitialDialogue(npcIdLower);
        int speed = props.get("speed", 50, Integer.class);
        float width = props.get("width", 100f, Float.class);
        float height = props.get("height", 100f, Float.class);

        if ("police".equalsIgnoreCase(npcId)) {
            return new Police("police", npcName, (int) width, (int) height, x, y, textureKey, npcId, targetWorld, speed, initialDialogue, itemManager);
        } else {
            int directionX = props.get("directionX", 0, Integer.class);
            int directionY = props.get("directionY", 0, Integer.class);
            float pauseTime = props.get("pauseTime", 0f, Float.class);
            float moveTime = props.get("moveTime", 0f, Float.class);

            return new NPC(npcIdLower, npcName, (int) width, (int) height, x, y, textureKey, npcId, targetWorld, directionX, directionY, pauseTime, moveTime, speed, initialDialogue, itemManager);
        }
    }

    private void restoreNpcState(NPC npc, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        if (npcStates != null && npcStates.containsKey(npc.getId())) {
            ServerSaveData.NpcSaveData state = npcStates.get(npc.getId());

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

    public void update(float delta) {
        World currentWorld = worldManager.getCurrentWorld();
        if (currentWorld == null) return;
        for (NPC npc : npcs) {
            if (npc.getWorld() != worldManager.getCurrentWorld()) continue;
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
                100, 100, player.getX(), player.getY() - 300, "police.3d", "police",
                currentWorld, 200, dialogueRegistry.getDialogue("summoned_police", "chase.offer"), itemManager);
        npcs.add(summonedPolice);
    }

    public void moveSummonedPoliceToNewWorld(World newWorld) {
        getSummonedPolice().setWorld(newWorld);
    }

    public Police getSummonedPolice() {
        return (Police) findNpcById("summoned_police");
    }

    public void kill(NPC npc) {
        if (npc == null) return;
        npcs.remove(npc);
    }

    public void teleportNpc(String npcId, World targetWorld, float x, float y) {
        NPC npc = findNpcById(npcId);
        if (npc == null || targetWorld == null) return;
        npc.setWorld(targetWorld);
        npc.setX(x);
        npc.setY(y);
    }
    public ArrayList<NPC> getNpcs() { return npcs; }
}
