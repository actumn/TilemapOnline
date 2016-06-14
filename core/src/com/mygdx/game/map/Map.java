package com.mygdx.game.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.manager.ObjectManager;
import com.mygdx.game.object.Player;
import com.mygdx.game.object.Effect;
import com.mygdx.game.object.Entity;

import java.util.HashMap;

/**
 * Created by Lee on 2016-05-20.
 */
public class Map {
    // Managers
    private ObjectManager objectManager;

    private int mapId;
    private String mapName;
    private TiledMap tiledMap;
    private HashMap<MapMovePoint, MapMoveDestination> moves;


    public Map(String mapName, String mapFileName) {
        this.mapName = mapName;
        this.objectManager = new ObjectManager();


        this.tiledMap = new TmxMapLoader().load(mapFileName);
        for (MapLayer layer : tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer)layer;
                for(int y=0; y<tileLayer.getHeight(); y++) {
                    for(int x=0; x<tileLayer.getWidth(); x++) {
                        if(tileLayer.getCell(x, y) == null) continue;
                        this.objectManager.add(new DrawTile(tileLayer, x, y));
                    }
                }
            }
        }

        this.moves = new HashMap<>();

        objectManager.setMapRectangle(0,0, this.getMapWidth(), this.getMapHeight());
    }
    public Map mapId(int mapId) {
        this.mapId = mapId;
        return this;
    }

    public void update() {
        if (tiledMap == null) {

        }
        objectManager.update();
        objectManager.draw();
    }

    public void addMovePoint(MapMovePoint point, MapMoveDestination destination) {
        this.moves.put(point, destination);
    }

    public void add(Entity entity) {
        objectManager.add(entity);
    }
    public void add(Effect e) {
        objectManager.add(e);
    }
    public void remove(Entity entity) { objectManager.remove(entity); }

    public boolean checkCollision(int x, int y) {
        int mapWidth = this.getMapWidth();
        int mapHeight = this.getMapHeight();

        if(x < 0 || y < 0 ||
                x >= mapWidth ||
                y >= mapHeight) return true;

        for(MapLayer layer : this.tiledMap.getLayers()) {
            if (layer instanceof TiledMapTileLayer) {
                TiledMapTileLayer tileLayer = (TiledMapTileLayer)layer;

                int tileX = x/(int)tileLayer.getTileWidth();
                int tileY = y/(int)tileLayer.getTileHeight();

                TiledMapTileLayer.Cell cell = tileLayer.getCell(tileX, tileY);
                if(cell == null) continue;

                if(Integer.parseInt((String)cell.getTile().getProperties().get("passage")) != 0) return true;
            }
        }

        return false;
    }

    public Entity getEntityById(long entityId) {
        return objectManager.getEntityById(entityId);
    }
    public String getMapName() {
        return mapName;
    }

    public int getMapWidth() {
        return (Integer) this.tiledMap.getProperties().get("width") * (Integer) this.tiledMap.getProperties().get("tilewidth");
    }
    public int getMapHeight() {
        return (Integer) this.tiledMap.getProperties().get("height") * (Integer) this.tiledMap.getProperties().get("tileheight");
    }

    public int getTilePosX(int x) {
        return (Integer) this.tiledMap.getProperties().get("tilewidth") * x;
    }
    public int getTilePosY(int y) {
        return (Integer) this.tiledMap.getProperties().get("tileheight") * y;
    }
    public void setCenterCharacter(Player centerPlayer) {
        objectManager.setCenterPlayer(centerPlayer);
    }

    public int getMapId() {
        return mapId;
    }

    public HashMap<MapMovePoint, MapMoveDestination> getMoves() {
        return moves;
    }
}
