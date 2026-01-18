package com.mygame.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.action.ActionRegistry;
import com.mygame.assets.Assets;

import java.util.HashMap;
import java.util.Map;

public class DialogueRegistry {
    private final Map<String, JsonValue> npcDialogueData = new HashMap<>();
    private final Map<String, DialogueNode> builtNodes = new HashMap<>();
    private ActionRegistry actionRegistry;

    public DialogueRegistry() {
        JsonReader jsonReader = new JsonReader();
        JsonValue base = jsonReader.parse(Gdx.files.internal("data/dialogues/dialogues.json"));
        for (JsonValue npcData : base) {
            npcDialogueData.put(npcData.name(), npcData);
        }
    }

    public void init(ActionRegistry actionRegistry) {
        this.actionRegistry = actionRegistry;
    }

    public DialogueNode getInitialDialogue(String npcId) {
        return getDialogue(npcId, "start");
    }

    public DialogueNode getDialogue(String npcId, String nodeName) {
        String fullNodeId = npcId + "." + nodeName;
        if (builtNodes.containsKey(fullNodeId)) {
            return builtNodes.get(fullNodeId);
        }

        DialogueNode node = buildNode(npcId, nodeName);
        builtNodes.put(fullNodeId, node);
        return node;
    }

    private DialogueNode buildNode(String npcId, String nodeName) {
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

        // Node redirection
        if (nodeData.isString()) {
            return getDialogue(npcId, nodeData.asString());
        }

        if (nodeData.isObject()) {
            return createNodeFromObject(nodeData, npcId);
        } else {
            // Default to simple node with just text
            return createSimpleNode(nodeData);
        }
    }

    private DialogueNode createNodeFromObject(JsonValue nodeData, String npcId) {
        Runnable onFinish = null;
        if (nodeData.has("onFinish")) {
            String actionName = nodeData.getString("onFinish");
            onFinish = actionRegistry.getAction(actionName);
        }

        String[] textKeys = nodeData.get("texts").asStringArray();
        boolean isForced = nodeData.getBoolean("isForced", false);

        DialogueNode node = new DialogueNode(onFinish, isForced, textKeys);

        if (nodeData.has("choices")) {
            addChoicesToNode(node, nodeData.get("choices"), npcId);
        }

        return node;
    }

    private void addChoicesToNode(DialogueNode node, JsonValue choicesData, String npcId) {
        for (JsonValue choiceData : choicesData) {
            String choiceTextKey;
            DialogueNode nextNode = null;
            Runnable choiceAction = null;

            if (choiceData.isString()) {
                choiceTextKey = choiceData.asString();
            } else {
                choiceTextKey = choiceData.getString("text");
                if (choiceData.has("next")) {
                    // Recursive call to get or build the next node
                    nextNode = getDialogue(npcId, choiceData.getString("next"));
                }
                if (choiceData.has("action")) {
                    choiceAction = actionRegistry.getAction(choiceData.getString("action"));
                }
            }
            String choiceText = Assets.dialogues.get(choiceTextKey);
            node.addChoice(choiceText, nextNode, choiceAction);
        }
    }

    private DialogueNode createSimpleNode(JsonValue nodeData) {
        String[] textKeys = nodeData.asStringArray();
        return new DialogueNode(null, false, textKeys);
    }
}
