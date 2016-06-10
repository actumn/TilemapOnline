package com.mygdx.game.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.mygdx.game.XmlDataLoader;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lee on 2016-06-06.
 */
public class Assets {

    private static Assets instance;


    private Texture titleBackground;
    //private Texture titleGameLogo;

    private HashMap<String, Texture> sheetMap;

    public static Assets getInstance() {
        if (instance == null) instance = new Assets();
        return instance;
    }

    private Assets() {
        titleBackground = new Texture(Gdx.files.internal("img/Titles/TITLE.png"));
        //titleGameLogo = new Texture(Gdx.files.internal("img/Titles/GAME_LOGO.png"));

        loadSheets();

    }

    public void loadSheets() {
        try {
            this.sheetMap = XmlDataLoader.getInstance().loadSheets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawTitleScreen(SpriteBatch batch, float a) {
        batch.begin();
        batch.setColor(1.0f, 1.0f, 1.0f, a);
        batch.draw(titleBackground, 0, 0);
        //batch.draw(instance.titleGameLogo, Gdx.graphics.getWidth()/2 - instance.titleGameLogo.getWidth()/2, 550f);
        batch.end();
    }

    public Texture getSheet(String key) {
        return sheetMap.get(key);
    }
}