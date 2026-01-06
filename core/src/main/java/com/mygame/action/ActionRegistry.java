package com.mygame.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

    private interface ActionCreator {
        Runnable create(GameContext ctx, JsonValue data);
    }

    private final Map<String, ActionCreator> creators = new HashMap<>();
    private final Map<String, Runnable> registeredActions = new HashMap<>();


    public void init(GameContext ctx) {
        setupCreators();
        loadActionsFromJson(ctx);
    }

    private void setupCreators() {
        creators.put("inventory.trade", (ctx, data) -> () -> new TradeAction(ctx,
            data.getString("from"), data.getString("to"),
            data.getInt("fromAmount"), data.getInt("toAmount")).execute());

        creators.put("quest.add", (ctx, data) -> () -> ctx.questManager.startQuest(data.getString("id")));

        creators.put("quest.complete", (ctx, data) -> () -> ctx.questManager.completeQuest(data.getString("id")));

        creators.put("dialogue.set", (ctx, data) -> () -> new SetDialogueAction(ctx,
            data.getString("npc"), data.getString("node")).execute());

        creators.put("inventory.add", (ctx, data) -> () -> ctx.getInventory().addItemAndNotify(
            ctx.itemRegistry.get(data.getString("id")),
            data.getInt("amount", 1)));

        creators.put("inventory.remove", (ctx, data) -> () -> {
            if (data.has("items")) {
                for (String itemId : data.get("items").asStringArray()) {
                    ctx.getInventory().removeItem(ctx.itemRegistry.get(itemId), data.getInt("amount", 9999));
                }
            } else {
                ctx.getInventory().removeItem(
                    ctx.itemRegistry.get(data.getString("id")),
                    data.getInt("amount", 1));
            }
        });

        creators.put("audio.playSound", (ctx, data) -> () -> SoundManager.playSound(Assets.getSound(data.getString("id"))));
        creators.put("audio.playMusic", (ctx, data) -> () -> MusicManager.playMusic(Assets.getMusic(data.getString("id"))));
        creators.put("player.die", (ctx, data) -> ctx.gsm::playerDied);

        creators.put("system.composite", (ctx, data) -> () -> {
            for (JsonValue subAction : data.get("actions")) {
                createAction(ctx, subAction).run();
            }
        });

        creators.put("system.timer", (ctx, data) -> () -> {
            float delay = data.getFloat("delay", 1f);
            JsonValue actionData = data.get("action");
            TimerManager.setAction(() -> createAction(ctx, actionData).run(), delay);
        });

        creators.put("item.startCooldown", (ctx, data) -> () -> {
            Item item = ctx.itemManager.getItem(data.getString("id"));
            if (item != null) item.startCooldown(data.getFloat("seconds", 1f));
        });

        creators.put("npc.setTexture", (ctx, data) -> () -> {
            var npc = ctx.npcManager.findNpcById((data.getString("npc")));
            if (npc != null) npc.setTexture(Assets.getTexture(data.getString("texture")));
        });

        creators.put("inventory.conditionalTrade", (ctx, data) -> () -> {
            if (ctx.getInventory().trade(ctx.itemRegistry.get(data.getString("from")),
                ctx.itemRegistry.get(data.getString("to")),
                data.getInt("fromAmount"), data.getInt("toAmount"))) {
                if (data.has("onSuccess")) {
                    createAction(ctx, data.get("onSuccess")).run();
                }
            } else {
                if (data.has("onFail")) {
                    createAction(ctx, data.get("onFail")).run();
                }
            }
        });

        creators.put("inventory.check", (ctx, data) -> () -> {
            boolean conditionMet = false;
            if (data.has("items")) {
                for (String itemId : data.get("items").asStringArray()) {
                    if (ctx.getInventory().getAmount(ctx.itemRegistry.get(itemId)) > 0) {
                        conditionMet = true;
                        break;
                    }
                }
            } else if (data.has("itemId")) {
                int amount = data.getInt("amount", 1);
                conditionMet = ctx.getInventory().getAmount(ctx.itemRegistry.get(data.getString("itemId"))) >= amount;
            }

            if (conditionMet) {
                if (data.has("action")) createAction(ctx, data.get("action")).run();
                if (data.has("onSuccess")) createAction(ctx, data.get("onSuccess")).run();
            } else {
                if (data.has("onFail")) createAction(ctx, data.get("onFail")).run();
            }
        });

        creators.put("player.checkState", (ctx, data) -> () -> {
            String requiredState = data.getString("state").toUpperCase();
            if (ctx.player.getState().name().equals(requiredState)) {
                if (data.has("action")) {
                    createAction(ctx, data.get("action")).run();
                }
            } else {
                if (data.has("onFail")) {
                    createAction(ctx, data.get("onFail")).run();
                }
            }
        });

        creators.put("ui.notEnoughMessage", (ctx, data) -> () -> EventBus.fire(new Events.NotEnoughMessageEvent(ctx.itemRegistry.get(data.getString("item")))));
        creators.put("ui.message", (ctx, data) -> () -> ctx.ui.getGameScreen().showInfoMessage(Assets.messages.get(data.getString("key")), data.getFloat("duration", 2f)));

        creators.put("player.setState", (ctx, data) -> () -> {
            String stateName = data.getString("key").toUpperCase();
            try {
                Player.State state = Player.State.valueOf(stateName);
                ctx.player.setState(state);
            } catch (IllegalArgumentException e) {
                Gdx.app.log("ActionRegistry", "Unknown player state: " + stateName);
            }
        });

        creators.put("player.lockMovement", (ctx, data) -> () -> ctx.player.setMovementLocked(data.getBoolean("locked")));

        creators.put("npc.remove", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null && npc.getWorld() != null) {
                npc.getWorld().getNpcs().remove(npc);
            }
        });

        creators.put("npc.spawnNearPlayer", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) {
                WorldManager.getCurrentWorld().getNpcs().add(npc);
                npc.setX(ctx.player.getX() + data.getFloat("offsetX", 0f));
                npc.setY(ctx.player.getY() + data.getFloat("offsetY", 0f));
            }
        });

        creators.put("npc.callPolice", (ctx, data) -> ctx.npcManager::callPolice);

        creators.put("dialogue.force", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) ctx.ui.getDialogueManager().startForcedDialogue(npc);
        });

        creators.put("custom.startChase", (ctx, data) -> () -> {
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) police.startChase(ctx.player);
        });
    }

    private void loadActionsFromJson(GameContext ctx) {
        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(Gdx.files.internal("data/actions/actions.json"));
            for (JsonValue entry : root) {
                registeredActions.put(entry.name(), createAction(ctx, entry));
            }
        } catch (Exception e) {
            Gdx.app.log("ActionRegistry", "Could not load actions.json: " + e.getMessage());
        }
    }

    public Runnable createAction(GameContext ctx, JsonValue data) {
        if (data == null) return () -> {};
        if (data.isString()) {
            return registeredActions.getOrDefault(data.asString(), () -> {});
        }
        String type = data.getString("type", "");
        ActionCreator creator = creators.get(type);
        if (creator != null) {
            return creator.create(ctx, data);
        }
        return () -> {};
    }

    public Runnable getAction(String name) {
        return registeredActions.get(name);
    }

    public void executeAction(String name) {
        Runnable action = registeredActions.get(name);
        if (action != null) {
            action.run();
        } else {
            Gdx.app.log("ActionRegistry", "Action '" + name + "' not found!");
        }
    }
}
