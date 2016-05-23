package com.mygdx.game.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.object.DrawObject;
import com.mygdx.game.object.Effect;
import com.mygdx.game.object.Entity;

import java.util.LinkedList;

/**
 * Created by Lee on 2016-05-21.
 */
public class DrawTile implements DrawObject {
    private int x;
    private int y;
    private int z;
    private TextureRegion textureRegion;
    private LinkedList<Entity> objects;
    private LinkedList<Effect> effects;

    public DrawTile(TiledMapTileLayer tileLayer, int x, int y, int z) {

        this.x = x * (int) tileLayer.getTileWidth();
        this.y = y * (int) tileLayer.getTileHeight();
        this.z = this.y + 32 * z;
        this.textureRegion = tileLayer.getCell(x, y).getTile().getTextureRegion();
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(this.getTextureRegion(), this.getDrawX(), this.getDrawY());
    }

    public TextureRegion getTextureRegion() {
        return this.textureRegion;
    }

    public int getDrawX() {
        return this.x;
    }

    public int getDrawY() {
        return this.y;
    }



}
