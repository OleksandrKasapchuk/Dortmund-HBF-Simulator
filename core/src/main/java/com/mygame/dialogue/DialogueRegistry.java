package com.mygame.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.assets.Assets;

import java.util.HashMap;
import java.util.Map;

public class DialogueRegistry {
    private static Map<String, Runnable> actions = new HashMap<>();
    private static Map<String, JsonValue> npcDialogueData = new HashMap<>();
    private static Map<String, DialogueNode> builtNodes = new HashMap<>();

    public static void init() {
        JsonReader jsonReader = new JsonReader();
        JsonValue base = jsonReader.parse(Gdx.files.internal("data/dialogues/dialogues.json"));
        for (JsonValue npcData : base) {
            npcDialogueData.put(npcData.name(), npcData);
        }
    }

    public static void reset() {
        actions.clear();
        npcDialogueData.clear();
        builtNodes.clear();
    }

    public static void registerAction(String name, Runnable action) {
        if (actions.containsKey(name)) {
            System.err.println("DialogueRegistry: Overwriting action '" + name + "'");
        }
        actions.put(name, action);
    }

    public static DialogueNode getDialogue(String npcId, String nodeName) {
        String fullNodeId = npcId + "." + nodeName;
        if (builtNodes.containsKey(fullNodeId)) {
            return builtNodes.get(fullNodeId);
        }

        JsonValue npcData = npcDialogueData.get(npcId);
        if (npcData == null) {
            System.err.println("DialogueRegistry: NPC '" + npcId + "' not found in dialogues.json");
            return new DialogueNode("Error: NPC '" + npcId + "' not found.");
        }

        JsonValue nodes = npcData.get("nodes");
        if (nodes == null) {
            System.err.println("DialogueRegistry: NPC '" + npcId + "' has no 'nodes' in dialogues.json");
            return new DialogueNode("Error: NPC '" + npcId + "' has no nodes.");
        }

        JsonValue nodeData = nodes.get(nodeName);
        if (nodeData == null) {
            System.err.println("DialogueRegistry: Node '" + nodeName + "' not found for NPC '" + npcId + "'");
            return new DialogueNode("Error: Node '" + nodeName + "' not found.");
        }

        Runnable onFinish = null;
        String[] textKeys;
        boolean isForced = false;

        DialogueNode node;

        if (nodeData.isObject()) {
            if (nodeData.has("onFinish")) {
                String actionName = nodeData.getString("onFinish");
                onFinish = actions.get(actionName);
                if (onFinish == null) {
                    System.err.println("DialogueRegistry: onFinish action '" + actionName + "' not registered.");
                }
            }
            textKeys = nodeData.get("texts").asStringArray();
            if (nodeData.has("isForced")) {
                isForced = nodeData.getBoolean("isForced");
            }
            node = new DialogueNode(onFinish, isForced, textKeys);

            if (nodeData.has("choices")) {
                for (JsonValue choiceData : nodeData.get("choices")) {
                    String choiceTextKey;
                    DialogueNode nextNode = null;
                    Runnable choiceAction = null;

                    if (choiceData.isString()) {
                        choiceTextKey = choiceData.asString();
                    } else { // isObject
                        choiceTextKey = choiceData.getString("text");
                        if (choiceData.has("next")) {
                            String nextNodeName = choiceData.getString("next");
                            nextNode = getDialogue(npcId, nextNodeName);
                        }
                        if (choiceData.has("action")) {
                            String actionName = choiceData.getString("action");
                            choiceAction = actions.get(actionName);
                            if (choiceAction == null) {
                                System.err.println("DialogueRegistry: choice action '" + actionName + "' not registered.");
                            }
                        }
                    }
                    String choiceText = Assets.bundle.get(choiceTextKey);
                    node.addChoice(choiceText, nextNode, choiceAction);
                }
            }
        } else { // isArray (simplified node)
            textKeys = nodeData.asStringArray();
            node = new DialogueNode(null, false, textKeys);
        }

        builtNodes.put(fullNodeId, node);
        return node;
    }

    public static DialogueNode getInitialDialogue(String npcId) {
        JsonValue npcData = npcDialogueData.get(npcId);
        if (npcData == null) {
            System.err.println("DialogueRegistry: NPC '" + npcId + "' not found in dialogues.json");
            return new DialogueNode("Error: NPC '" + npcId + "' not found.");
        }
        String startNodeName = npcData.getString("startNode", "start"); // Default to "start"
        return getDialogue(npcId, startNodeName);
    }
}
