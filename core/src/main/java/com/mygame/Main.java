package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class Main extends ApplicationAdapter {
    private Player player;
    Texture texture;
    private SpriteBatch batch;

    private World world;
    private Stage stage;
    private Skin skin;

    private OrthographicCamera camera;
    private Viewport viewport;

    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;

    @Override
    public void create() {
        batch = new SpriteBatch();
        texture = new Texture("ui/zoe.png");

        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);

        world = new World();

        player = new Player(500,100,100, 200,200, texture, world);
        stage = new Stage(new FitViewport(2000, 1000));


        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(stage);
            TextButton leftButton = new TextButton("LEFT", skin);
            leftButton.setSize(150, 150);
            leftButton.setPosition(1450, 50);

            leftButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveLeftPressed = true;
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveLeftPressed = false;
                }
            });
            stage.addActor(leftButton);


            TextButton downButton = new TextButton("DOWN", skin);
            downButton.setSize(150, 150);
            downButton.setPosition(1625, 50);

            downButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveDownPressed = true;
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveDownPressed = false;
                }
            });
            stage.addActor(downButton);


            TextButton rightButton = new TextButton("RIGHT", skin);
            rightButton.setSize(150, 150);
            rightButton.setPosition(1800, 50);

            rightButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveRightPressed = true;
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveRightPressed = false;
                }
            });
            stage.addActor(rightButton);


            TextButton upButton = new TextButton("UP", skin);
            upButton.setSize(150, 150);
            upButton.setPosition(1625, 225);

            upButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveUpPressed = true;
                    return true;
                }
                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    player.moveUpPressed = false;
                }
            });
            stage.addActor(upButton);
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
        player.update(delta, WORLD_WIDTH, WORLD_HEIGHT);

        // Центр камери має йти за гравцем,
        // але враховуємо, що координати гравця — з лівого нижнього кута
        float targetX = player.x + player.width / 2f;
        float targetY = player.y + player.height / 2f;

        // Обмежуємо камеру, щоб не вийшла за межі карти
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
        batch.end();

        if (stage != null) {
            stage.act(delta);
            stage.draw();
        }
    }

    @Override
    public void dispose() {
        if (player != null && player.texture != null) {
            player.texture.dispose();
        }
        if (batch != null) {
            batch.dispose();
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
    }
}
