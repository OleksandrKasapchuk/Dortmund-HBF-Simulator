package com.mygame.dialogue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
        System.out.println("DialogueRegistry INIT (Manifest Mode)");
        JsonReader jsonReader = new JsonReader();

        FileHandle manifestFile = Gdx.files.internal("data/dialogues.json");
        if (!manifestFile.exists()) {
            manifestFile = Gdx.files.internal("assets/data/dialogues.json");
        }

        if (!manifestFile.exists()) {
            throw new RuntimeException("DialogueRegistry: Manifest file not found. Expected at 'data/dialogues.json' or 'assets/data/dialogues.json'.");
        }

        JsonValue manifest = jsonReader.parse(manifestFile);
        String[] npcIds = manifest.asStringArray();

        for (String npcId : npcIds) {
            FileHandle dialogueFile = Gdx.files.internal("data/dialogues/" + npcId + ".json");
            if (!dialogueFile.exists()) {
                 dialogueFile = Gdx.files.internal("assets/data/dialogues/" + npcId + ".json");
            }

            if (!dialogueFile.exists()) {
                System.out.println("WARNING: Dialogue file for '" + npcId + "' not found, skipping.");
                continue;
            }

            JsonValue root = jsonReader.parse(dialogueFile);
            // The actual NPC data is nested under a key that matches the NPC's ID.
            // Get that nested object.
            JsonValue npcData = root.get(npcId);

            // If not found, maybe the file ISN'T nested. Let's try using the root itself as a fallback.
            if (npcData == null) {
                npcData = root;
            }

            if (!npcData.has("nodes")) {
                throw new RuntimeException(
                    "DialogueRegistry: file " + dialogueFile.name() + " has no 'nodes'"
                );
            }
            npcDialogueData.put(npcId, npcData);
        }

        System.out.println("DialogueRegistry loaded NPCs: " + npcDialogueData.keySet());
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
            throw new RuntimeException(
                "DialogueRegistry: NPC '" + npcId + "' not found"
            );
        }

        JsonValue nodes = npcData.get("nodes");
        JsonValue nodeData = nodes.get(nodeName);

        if (nodeData == null) {
            throw new RuntimeException(
                "DialogueRegistry: Node '" + nodeName + "' not found for NPC '" + npcId + "'"
            );
        }

        // Redirect
        if (nodeData.isString()) {
            return getDialogue(npcId, nodeData.asString());
        }

        // Simple node: ["text.key.1", "text.key.2"]
        if (nodeData.isArray()) {
            return createSimpleNode(nodeData);
        }

        // Full node object
        if (nodeData.isObject()) {
            return createNodeFromObject(nodeData, npcId);
        }

        throw new RuntimeException(
            "DialogueRegistry: Unsupported node type: " + nodeData
        );
    }

    private DialogueNode createSimpleNode(JsonValue nodeData) {
        String[] textKeys = nodeData.asStringArray();
        return new DialogueNode(null, false, textKeys);
    }

    private DialogueNode createNodeFromObject(JsonValue nodeData, String npcId) {
        String onFinish = null;
        boolean isForced = nodeData.getBoolean("isForced", false);

        if (nodeData.has("onFinish")) {
            onFinish = nodeData.getString("onFinish");
        }

        if (!nodeData.has("texts")) {
            throw new RuntimeException(
                "DialogueRegistry: node has no 'texts': " + nodeData
            );
        }

        String[] textKeys = nodeData.get("texts").asStringArray();
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
                    nextNode = getDialogue(
                        npcId,
                        choiceData.getString("next")
                    );
                }

                if (choiceData.has("action")) {
                    choiceAction = actionRegistry.getAction(
                        choiceData.getString("action")
                    );
                }
            }

            String choiceText = Assets.dialogues.get(choiceTextKey);
            node.addChoice(choiceText, nextNode, choiceAction);
        }
    }
}
