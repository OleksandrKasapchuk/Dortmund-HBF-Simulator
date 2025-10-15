package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Main extends ApplicationAdapter {
    private Player player;
    private NPC npc;
    Texture texture;
    private SpriteBatch batch;
    private BitmapFont font;

    private World world;
    private Stage stage;
    private Skin skin;

    private OrthographicCamera camera;
    private Viewport viewport;

    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;

    private Texture knobTexture;
    private Texture bgTexture;

    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = new Texture("ui/zoe.png");

        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);

        world = new World();

        player = new Player(500,100,100, 200,200, texture, world);
        npc = new NPC(100, 100, 500, 300, texture, world);
        stage = new Stage(new FitViewport(2000, 1000));

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(stage);

            // Create textures for the touchpad
            Pixmap knobPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
            knobPixmap.setColor(Color.WHITE);
            knobPixmap.fillCircle(25, 25, 25);
            knobTexture = new Texture(knobPixmap);
            knobPixmap.dispose();

            Pixmap bgPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            bgPixmap.setColor(new Color(0.3f,0.3f,0.3f,0.5f));
            bgPixmap.fillCircle(50, 50, 50);
            bgTexture = new Texture(bgPixmap);
            bgPixmap.dispose();

            // Create the touchpad style
            Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
            touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
            touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

            // Create the touchpad, set its bounds, and add it to the stage
            Touchpad touchpad = new Touchpad(10, touchpadStyle);
            touchpad.setBounds(150, 150, 200, 200);
            stage.addActor(touchpad);
            player.touchpad = touchpad; // Assign the touchpad to the player

            TextButton actButton = new TextButton("ACT", skin);
            actButton.setSize(150, 150);
            actButton.setPosition(1800, 150);
            actButton.getLabel().setFontScale(4f);

            actButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    player.actPressed = true;
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    player.actPressed = false;
                }
            });
            stage.addActor(actButton);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta);
        npc.update(delta);

        // Move camera logic before rendering
        float targetX = player.x + player.width / 2f;
        float targetY = player.y + player.height / 2f;

        float cameraX = Math.max(camera.viewportWidth / 2f,
            Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f,
            Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));

        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        world.draw(batch);
        player.draw(batch);
        npc.draw(batch);

        if (npc.isPlayerNear(player)) {
            font.draw(batch, "Press E/ACT to interact", npc.x - 100, npc.y + npc.height + 40);
            if (Gdx.input.isKeyPressed(Input.Keys.E) || player.actPressed) {
                npc.interacted = true;
            }
            if (npc.interacted)
                font.draw(batch, "HELLO PISIUNCHYK", 800, 100);
        } else {
            npc.interacted = false;
        }
        batch.end();

        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
        if (skin != null) {
            skin.dispose();
        }
        if (world != null) {
            world.dispose();
        }
        if (knobTexture != null) {
            knobTexture.dispose();
        }
        if (bgTexture != null) {
            bgTexture.dispose();
        }
    }
}
