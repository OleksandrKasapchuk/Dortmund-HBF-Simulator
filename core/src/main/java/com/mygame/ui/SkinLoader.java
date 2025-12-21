package com.mygame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.assets.Assets;

public class SkinLoader {
    public static Skin loadSkin() {
        Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        BitmapFont cyrillicFont = Assets.myFont;

        // Прив'язка шрифту до всіх стилів, де є текст
        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextField.TextFieldStyle style : skin.getAll(TextField.TextFieldStyle.class).values()) {
            style.font = cyrillicFont;
            if (style.messageFont != null) style.messageFont = cyrillicFont;
        }
        for (SelectBox.SelectBoxStyle style : skin.getAll(SelectBox.SelectBoxStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (List.ListStyle style : skin.getAll(List.ListStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (Window.WindowStyle style : skin.getAll(Window.WindowStyle.class).values()) {
            style.titleFont = cyrillicFont;
        }
        return skin;
    }
}
