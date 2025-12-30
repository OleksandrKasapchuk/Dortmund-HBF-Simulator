package com.mygame.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.action.custom.ChikitaCraftJointAction;
import com.mygame.action.custom.PoliceCheckAction;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

    private interface ActionCreator {
        Runnable create(GameContext ctx, JsonValue data);
    }

    private static final Map<String, ActionCreator> creators = new HashMap<>();
    private static final Map<String, Runnable> registeredActions = new HashMap<>();

    static {
        creators.put("trade", (ctx, data) -> () -> new TradeAction(ctx,
            data.getString("from"), data.getString("to"),
            data.getInt("fromAmount"), data.getInt("toAmount")).execute());

        creators.put("add_quest", (ctx, data) -> () -> new AddQuestAction(
            data.getString("id")).execute());

        creators.put("complete_quest", (ctx, data) -> () -> QuestManager.completeQuest(data.getString("id")));

        creators.put("set_dialogue", (ctx, data) -> () -> new SetDialogueAction(ctx,
            data.getString("npc"), data.getString("node")).execute());

        creators.put("add_item", (ctx, data) -> () -> ctx.getInventory().addItemAndNotify(
            ItemRegistry.get(data.getString("id")),
            data.getInt("amount", 1)));

        creators.put("remove_item", (ctx, data) -> () -> ctx.getInventory().removeItem(
            ItemRegistry.get(data.getString("id")),
            data.getInt("amount", 1)));

        creators.put("play_sound", (ctx, data) -> () -> SoundManager.playSound(Assets.getSound(data.getString("id"))));

        creators.put("play_music", (ctx, data) -> () -> MusicManager.playMusic(Assets.getMusic(data.getString("id"))));

        creators.put("player_died", (ctx, data) -> ctx.gsm::playerDied);

        creators.put("composite", (ctx, data) -> () -> {
            for (JsonValue subAction : data.get("actions")) {
                createAction(ctx, subAction).run();
            }
        });

        creators.put("timer", (ctx, data) -> () -> {
            float delay = data.getFloat("delay", 1f);
            JsonValue actionData = data.get("action");
            TimerManager.setAction(() -> createAction(ctx, actionData).run(), delay);
        });

        creators.put("start_cooldown", (ctx, data) -> () -> {
            Item item = ctx.itemManager.getItem(data.getString("id"));
            if (item != null) item.startCooldown(data.getFloat("seconds", 1f));
        });

        creators.put("set_texture", (ctx, data) -> () -> {
            var npc = ctx.npcManager.findNpcById((data.getString("npc")));
            if (npc != null) npc.setTexture(Assets.getTexture(data.getString("texture")));
        });

        creators.put("conditional_trade", (ctx, data) -> () -> {
            if (ctx.getInventory().trade(ItemRegistry.get(data.getString("from")),
                ItemRegistry.get(data.getString("to")),
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

        creators.put("if_has_item", (ctx, data) -> () -> {
            int amount = data.getInt("amount", 1);
            if (ctx.getInventory().getAmount(ItemRegistry.get(data.getString("itemId"))) >= amount) {
                if (data.has("action")) {
                    createAction(ctx, data.get("action")).run();
                }
            } else {
                if (data.has("onFail")) {
                    createAction(ctx, data.get("onFail")).run();
                }
            }
        });

        creators.put("not_enough_message", (ctx, data) -> () -> EventBus.fire(new Events.NotEnoughMessageEvent(ItemRegistry.get(data.getString("item")))));

        creators.put("message", (ctx, data) -> () -> ctx.ui.getGameScreen().showInfoMessage(Assets.messages.get(data.getString("key")), data.getFloat("duration", 2f)));

        creators.put("player.set_state", (ctx, data) -> () -> {
            String stateName = data.getString("key").toUpperCase();
            try {
                Player.State state = Player.State.valueOf(stateName);
                ctx.player.setState(state);
            } catch (IllegalArgumentException e) {
                Gdx.app.log("ActionRegistry", "Unknown player state: " + stateName);
            }
        });

        creators.put("remove_npc", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) ctx.npcManager.kill(npc);
        });

        creators.put("spawn_npc_near_player", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) {
                WorldManager.getCurrentWorld().getNpcs().add(npc);
                npc.setX(ctx.player.getX() + data.getFloat("offsetX", 0f));
                npc.setY(ctx.player.getY() + data.getFloat("offsetY", 0f));
            }
        });

        creators.put("call_police", (ctx, data) -> ctx.npcManager::callPolice);

        creators.put("force_dialogue", (ctx, data) -> () -> {
            NPC npc = ctx.npcManager.findNpcById(data.getString("npc"));
            if (npc != null) ctx.ui.getDialogueManager().startForcedDialogue(npc);
        });

        creators.put("chikita_craft", (ctx, data) -> () -> new ChikitaCraftJointAction(ctx).execute());

        creators.put("police_check", (ctx, data) -> () -> new PoliceCheckAction(ctx).execute());

        creators.put("start_chase", (ctx, data) -> () -> {
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) police.startChase(ctx.player);
        });
    }

    public static void registerAll(GameContext ctx) {
        registeredActions.clear();
        loadActionsFromJson(ctx);
    }

    private static void loadActionsFromJson(GameContext ctx) {
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

    public static Runnable createAction(GameContext ctx, JsonValue data) {
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

    public static Runnable getAction(String name) {
        return registeredActions.get(name);
    }

    public static void executeAction(String name) {
        Runnable action = registeredActions.get(name);
        if (action != null) {
            action.run();
        } else {
            Gdx.app.log("ActionRegistry", "Action '" + name + "' not found!");
        }
    }
}
