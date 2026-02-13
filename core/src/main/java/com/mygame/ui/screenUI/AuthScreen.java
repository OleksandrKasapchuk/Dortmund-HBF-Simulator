package com.mygame.ui.screenUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.assets.Assets;
import com.mygame.game.auth.AuthManager;

import java.util.Map;

public class AuthScreen extends Screen {

    public AuthScreen(Skin skin) {
        super();

        Image backgroundImage = new Image(Assets.getTexture("menuBlurBack"));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        backgroundImage.toBack();


        Table registerTable = new Table();

        TextField regUsername = new TextField("", skin);
        TextField regPassword = new TextField("", skin);
        TextField regConfirm = new TextField("", skin);

        regPassword.setPasswordMode(true);
        regConfirm.setPasswordMode(true);

        TextButton registerBtn = new TextButton("Register", skin);

        registerTable.add(new Label(Assets.ui.get("ui.auth.username"), skin)).pad(5);
        registerTable.add(regUsername).pad(5).row();

        registerTable.add(new Label(Assets.ui.get("ui.auth.password"), skin)).pad(5);
        registerTable.add(regPassword).pad(5).row();

        registerTable.add(new Label(Assets.ui.get("ui.auth.confirm"), skin)).pad(5);
        registerTable.add(regConfirm).pad(5).row();

        registerTable.add(registerBtn).colspan(2).padTop(10);

        // --- Логін форма ---
        TextField usernameField = new TextField("", skin);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton(Assets.ui.get("ui.auth.login"), skin);

        Table loginTable = new Table();
        loginTable.center();
        loginTable.add(new Label(Assets.ui.get("ui.auth.username"), skin)).pad(5);
        loginTable.add(usernameField).pad(5).row();
        loginTable.add(new Label(Assets.ui.get("ui.auth.password"), skin)).pad(5);
        loginTable.add(passwordField).pad(5).row();
        loginTable.add(loginButton).colspan(2).padTop(10);

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Login tried: ");
                String username = usernameField.getText();
                String password = passwordField.getText();

                if(username.isEmpty() || password.isEmpty()) return;

                AuthManager.login(username, password, new AuthManager.HttpCallback() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("Login success: " + response);

                        JsonReader reader = new JsonReader();
                        JsonValue jsonValue = reader.parse(response.trim());

                        String token = jsonValue.getString("token", null);
                        String username = jsonValue.getString("username", null);

                        if (token != null) {
                            AuthManager.setToken(token);

                            Preferences prefs = Gdx.app.getPreferences("MyGameSession");
                            prefs.putString("token", token);
                            prefs.flush();

                            System.out.println("Token saved: " + token + ", username: " + username);
                        } else {
                            System.out.println("Token not found in response!");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }

                    @Override
                    public void onCancelled() {
                        System.out.println("Login cancelled");
                    }
                });
            }
        });

        registerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                String username = regUsername.getText();
                String password = regPassword.getText();
                String confirm = regConfirm.getText();

                if(username.isEmpty() || password.isEmpty()) return;

                AuthManager.register(username, password, confirm,
                    new AuthManager.HttpCallback() {

                        @Override
                        public void onSuccess(String response) {
                            System.out.println("Register success: " + response);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            t.printStackTrace();
                        }

                        @Override
                        public void onCancelled() {
                            System.out.println("Register cancelled");
                        }
                    });
            }
        });


        Stack formStack = new Stack();
        formStack.add(loginTable);
        formStack.add(registerTable);

        registerTable.setVisible(false);

        TextButton switchBtn = new TextButton("Switch to Register", skin);

        switchBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                boolean loginVisible = loginTable.isVisible();

                loginTable.setVisible(!loginVisible);
                registerTable.setVisible(loginVisible);

                if(loginVisible)
                    switchBtn.setText("Switch to Login");
                else
                    switchBtn.setText("Switch to Register");
            }
        });

        root.add(formStack).center().padBottom(50).row();
        root.add(switchBtn).padBottom(30);
    }
}
