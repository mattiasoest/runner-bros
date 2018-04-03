package net.runnerbros.controller;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

/**
 * Created by mattiasosth on 16/07/2014.
 */
public class Level {

    private static final String OBJECTS = "objects";

    private final static String MAP_PATH = "maps/";

    private final float  TILEWIDTH;
    private final float  TILEHEIGHT;
    private final String key;

    private static int worldNo;
    private static int levelNo;

    private final String levelName;

    private TiledMap map;

    public Level(final String key, final String name) {
        levelName = name;
        //Key like, world_1-1
        this.key = key;
        String worldInfo = key.split("_")[1];
        worldNo = Integer.parseInt(worldInfo.split("-")[0]);
        levelNo = Integer.parseInt(worldInfo.split("-")[1]);
        loadMap(key);
        TILEWIDTH = getCollisionLayer().getTileWidth();
        TILEHEIGHT = getCollisionLayer().getTileHeight();
    }

    public int getWorldNo() {
        return worldNo;
    }

    public int getLevelNo() {
        return levelNo;
    }

    public TiledMap getMap() {
        return map;
    }

    public String getLevelName() {
        return levelName;
    }

    public MapLayer getObjectLayer() {
        return this.map.getLayers().get(OBJECTS);
    }
    public TiledMapTileLayer getCollisionLayer() {
        return (TiledMapTileLayer) this.map.getLayers().get(0);
    }

    public float getTileWidth() {
        return TILEWIDTH;
    }

    public float getTileHeight() {
        return TILEHEIGHT;
    }

    private void loadMap(final String key) {
        this.map = new TmxMapLoader().load(MAP_PATH + key + ".tmx");
        System.out.println("TMX MAP LOADED: " + MAP_PATH + key + ".tmx");
    }

    public String getKey() {
        return key;
    }
}
