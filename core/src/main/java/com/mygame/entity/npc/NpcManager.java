package com.mygame.entity.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
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
import java.util.Random;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final ArrayList<NPC> pendingRemoval = new ArrayList<>();
    private final Player player;
    private final DialogueRegistry dialogueRegistry;
    private final WorldManager worldManager;
    private final ItemManager itemManager;
    private final Random random = new Random();

    public NpcManager(Player player, DialogueRegistry dialogueRegistry, WorldManager worldManager, ItemManager itemManager) {
        this.player = player;
        this.dialogueRegistry = dialogueRegistry;
        this.worldManager = worldManager;
        this.itemManager = itemManager;
    }

    public void loadNpcsFromMap(World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        Gdx.app.log("NpcManager", "loadNpcsFromMap() called for world: " + world.getName());

        // 1. Завантаження основних NPC з шару 'npcs'
        MapLayer npcLayer = world.getMap().getLayers().get("npcs");
        if (npcLayer != null) {
            for (MapObject object : npcLayer.getObjects()) {
                MapProperties props = object.getProperties();
                String npcId = object.getName();
                if (npcId != null && findNpcById(npcId.toLowerCase()) == null) {
                    createNpcFromMap(npcId, props, world, npcStates);
                }
            }
        }

        // 2. Завантаження зон масовки з шару 'ambient_zones'
        MapLayer zoneLayer = world.getMap().getLayers().get("ambient_zones");
        if (zoneLayer != null) {
            Gdx.app.error("am", "ambient_zones isnt null");
            for (MapObject object : zoneLayer.getObjects()) {
                if (object instanceof RectangleMapObject rectObject) {
                    Rectangle rect = rectObject.getRectangle();
                    int count = rectObject.getProperties().get("count", 1, Integer.class);
                    spawnCrowdInZone(world, rect, count);
                }
            }
        }

        restoreDynamicNpcs(npcStates);
    }

    private void spawnCrowdInZone(World world, Rectangle zone, int count) {
        String[] crowdTextures = {"crowd_1", "crowd_2", "crowd_3"};
        for (int i = 0; i < count; i++) {
            float x = zone.x + random.nextFloat() * zone.width;
            float y = zone.y + random.nextFloat() * zone.height;

            String textureKey = crowdTextures[random.nextInt(crowdTextures.length)];
            String id = "ambient_" + world.getName() + "_" + random.nextInt(10000);

            // Створюємо статичного NPC (без руху)
            NPC ambient = new NPC(id, "", 80, 170, x, y, textureKey, world);
            npcs.add(ambient);
        }
        Gdx.app.log("NpcManager", "Spawned " + count + " ambient NPCs in zone at " + zone.x + "," + zone.y);
    }

    private void restoreDynamicNpcs(Map<String, ServerSaveData.NpcSaveData> npcStates) {
        if (npcStates == null) return;
        if (npcStates.containsKey("summoned_police") && findNpcById("summoned_police") == null) {
            ServerSaveData.NpcSaveData state = npcStates.get("summoned_police");
            World targetWorld = state.currentWorld != null ? worldManager.getWorld(state.currentWorld) : worldManager.getCurrentWorld();
            if (targetWorld != null) {
                callPoliceAt(targetWorld, state.x, state.y);
                NPC police = findNpcById("summoned_police");
                if (police != null) restoreNpcState(police, npcStates);
            }
        }
    }

    private void createNpcFromMap(String npcId, MapProperties props, World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        NPC npc = createNpcInstance(npcId, props, world, npcStates);
        restoreNpcState(npc, npcStates);
        npcs.add(npc);
    }

    private NPC createNpcInstance(String npcId, MapProperties props, World world, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        World targetWorld = world;
        String npcIdLower = npcId.toLowerCase();
        String npcType = props.get("type", npcId, String.class).toLowerCase();

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

        String textureKey = props.get("textureKey", null, String.class);
        if (textureKey == null) {
            textureKey = (Assets.getTexture(npcType + ".3d") != null) ? npcType + ".3d" : npcType;
        }

        String npcName = getNpcName(npcId);
        DialogueNode initialDialogue = dialogueRegistry.getInitialDialogue(npcType);
        int speed = props.get("speed", 50, Integer.class);
        float width = props.get("width", 100f, Float.class);
        float height = props.get("height", 100f, Float.class);

        if ("police".equalsIgnoreCase(npcType)) {
            return new Police(npcId, npcName, (int) width, (int) height, x, y, textureKey, npcType, targetWorld, speed, initialDialogue, itemManager);
        } else {
            int directionX = props.get("directionX", 0, Integer.class);
            int directionY = props.get("directionY", 0, Integer.class);
            float pauseTime = props.get("pauseTime", 0f, Float.class);
            float moveTime = props.get("moveTime", 0f, Float.class);
            int distance = props.get("distance", 150, Integer.class);
            return new NPC(npcIdLower, npcName, npcType, (int) width, (int) height, x, y, textureKey, npcId, targetWorld, directionX, directionY, pauseTime, moveTime, speed, distance, initialDialogue, itemManager);
        }
    }

    private void restoreNpcState(NPC npc, Map<String, ServerSaveData.NpcSaveData> npcStates) {
        if (npcStates != null && npcStates.containsKey(npc.getId())) {
            ServerSaveData.NpcSaveData state = npcStates.get(npc.getId());
            if (state.currentNode != null) {
                npc.setDialogue(dialogueRegistry.getDialogue(npc.getType(), state.currentNode));
                npc.setCurrentDialogueNodeId(state.currentNode);
            }
            if (state.currentTexture != null) npc.setTexture(state.currentTexture);

            if (npc.getId().equals("summoned_police") && npc instanceof Police police) {
                police.startChase(player);
            }
        }
    }

    private String getNpcName(String npcId) {
        try {
            return Assets.npcs.get("npc." + npcId.toLowerCase() + ".name");
        } catch (Exception e) {
            return npcId;
        }
    }

    public void update(float delta) {
        if (!pendingRemoval.isEmpty()) {
            npcs.removeAll(pendingRemoval);
            pendingRemoval.clear();
        }

        World currentWorld = worldManager.getCurrentWorld();
        if (currentWorld == null) return;

        for (int i = 0; i < npcs.size(); i++) {
            NPC npc = npcs.get(i);
            if (npc.getWorld() == currentWorld) {
                npc.update(delta);
            }
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
        callPoliceAt(currentWorld, player.getX(), player.getY() - 300);
    }

    private void callPoliceAt(World world, float x, float y) {
        if (findNpcById("summoned_police") != null) return;
        String textureKey = (Assets.getTexture("police.3d") != null) ? "police.3d" : "police";
        Police summonedPolice = new Police("summoned_police", Assets.npcs.get("npc.police.name"),
                100, 100, x, y, textureKey, "police",
                world, 200, dialogueRegistry.getDialogue("police", "chase.offer"), itemManager);
        npcs.add(summonedPolice);
    }

    public void moveSummonedPoliceToNewWorld(World newWorld) {
        Police p = getSummonedPolice();
        if (p != null) p.setWorld(newWorld);
    }

    public Police getSummonedPolice() {
        return (Police) findNpcById("summoned_police");
    }

    public void kill(NPC npc) {
        if (npc == null) return;
        pendingRemoval.add(npc);
        Gdx.app.log("NpcManager", "NPC marked for removal: " + npc.getId());
    }

    public void teleportNpc(String npcId, World targetWorld, float x, float y) {
        NPC npc = findNpcById(npcId);
        if (npc != null && targetWorld != null) {
            npc.setWorld(targetWorld);
            npc.setX(x);
            npc.setY(y);
        }
    }
    public ArrayList<NPC> getNpcs() { return npcs; }
}
