package net.runnerbros.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.entities.Benny;
import net.runnerbros.entities.Coin;
import net.runnerbros.entities.ItemBox;
import net.runnerbros.entities.MoveableEntity;
import net.runnerbros.entities.Player;
import net.runnerbros.entities.Player.State;
import net.runnerbros.entities.Slime;
import net.runnerbros.entities.Snowman;
import net.runnerbros.entities.Trampoline;

public class GameController implements InputProcessor {

    private static final boolean IS_CAM_INIT = true;

    public static final float GREEN_VELOCITY_MULTIPLIER = 4f;
    public static final float RED_VELOCITY_MULTIPLIER = 6f;
    public static final float BLUE_VELOCITY_MULTIPLIER = 2.5f;
    public static final float PINK_VELOCITY_MULTIPLIER = 1f;


    public static final int THE_CITY  = 1;
    public static final int SNOW_CITY = 2;
    public static final int CITY_PARK = 3;

    public static final int THE_CITY_NO_LVLS   = 8;
    public static final int SNOW_CITY_NO_LVLS  = 5;
    public static final int CITY_PARK_NO_LVLS  = 5;


    public static final float VIRTUAL_WIDTH  = 800f;
    public static final float VIRTUAL_HEIGHT = 460f;

    private static final long  LONG_JUMP_PRESS       = 170l;
    private static final float GRAVITY               = 22f;
    private static final float MAX_FALL_SPEED_FLYING = 1.5f;
    private static final float MAX_FALL_SPEED_ORG    = 12.5f;
    private static final float JUMP_FORCE            = 5f;
    private static final float JUMP_FORCE_CONTINOUS  = 31f;
    private static final float BOUNCE_FORCE          = 5f;
    private static final float DEAD_BOUNCE_FORCE     = 6f;
    private static final float TRAMPOLINE_FORCE      = 14f;
    private static final float ENEMY_COLLISION_FORCE = 18f;
    private static final float DAMP                  = 0.85f;
    private static final float TURN_DAMP             = 0.79f;
    private static final float SLIME_MAX_VEL         = 18f;
    private static final float ACCELERATION          = 7.5f;
    private static final float MAX_ACCELERATION      = 40f;
    private static final float AIR_ACCELERATION      = 24f;
    private static       float MAX_FALL_SPEED        = 12.5f;
    private              float MAX_VEL               = 4.6f;
    private final static float MAX_VEL_ORG           = 4.6f;
    private final static float MAX_VEL_SPEEDRUN      = 6.2f;

    private final Preferences mapScores;
    private final RunnerBros  game;

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    private final DecimalFormat decimalFormat;

    public void setGameState(GameState gameState) {
        currentGameState = gameState;
    }

    public enum GameState {
        RUNNING, PAUSED, READY, FINISHED, CAM_INITIALIZATION,
    }


    private static GameState currentGameState;

    private float timer;
    private float tempVel;
    private float tempAcc;
    private float readyDelay;

    private static final String SPIKES_KEY  = "spikes";
    private static final String BLOCKED_KEY = "blocked";
    private TiledMapTileLayer collisionLayer;
    private Player            player;

    private long    jumpPressedTime;
    private boolean jumpPressed;

    private boolean isPlayingCopterSound = false;

    private boolean isTimerOn          = false;
    private boolean buttonLeftPressed  = false;
    private boolean buttonRightPressed = false;
    private boolean buttonJumpPressed  = false;
    private boolean buttonSpeedPressed = false;
    private boolean buttonPausePressed = false;
    private boolean toggleButtonPressed;

    private boolean jumpYCollision;

    private Random rand;

    private Array<Coin>       coins;
    private Array<Slime>      slimes;
    private Array<MoveableEntity> enemyStartpositions;
    private Array<Snowman>    snowmen;
    private Array<Trampoline> trampolines;
    private Array<ItemBox>    boxes;

    private ItemBox currentItemBox;
    private Vector2 spawnPoint;
    private float   TILEWIDTH;
    private float   TILEHEIGHT;
    private Vector2 triggerJumpSmokePosition = new Vector2();
    private boolean isSmokeJumpTrigged;
    private boolean isGamePaused;
    private Stage   stagePause;
    private Stage   stageFinishMenu;
    private Stage   stageGameStart;
    private Table   pauseTable;
    private Table   finishTable;
    private Table   gameStartTable;

    private long startCopterTime = 0l;

    private Level     currentLevel;
    private Rectangle levelGoal;
    private Music     cityWorldMusic;
    private Benny     benny;

    public GameController(RunnerBros game) {

        this.game = game;
        Gdx.input.setCatchBackKey(true);

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');

        decimalFormat = new DecimalFormat("#.###", otherSymbols);
        decimalFormat.setMinimumFractionDigits(3);


        this.mapScores = Gdx.app.getPreferences("runner_bros_12FG93F5GAJB529");
        this.coins = new Array<Coin>();
        this.slimes = new Array<Slime>();
        this.snowmen = new Array<Snowman>();
        this.trampolines = new Array<Trampoline>();
        this.enemyStartpositions = new Array<MoveableEntity>();
        this.boxes = new Array<ItemBox>();
        this.rand = new Random();

        this.player = new Player(0, 0, 12f, 58f);
    }

    public void initMap() {
        if (IS_CAM_INIT) {
            currentGameState = GameState.CAM_INITIALIZATION;
        }
    }

    public void loadLevel(final String key, final String name) {
        this.currentLevel = new Level(key, name);
        System.out.println("Loaded level: " + name);
        this.collisionLayer = currentLevel.getCollisionLayer();
        this.TILEWIDTH = currentLevel.getTileWidth();
        this.TILEHEIGHT = currentLevel.getTileHeight();
        clearObjects();
        player.resetPlayer();
        parseMapObjects();
        player.randomizeBrother();
        player.setPos(spawnPoint.x, spawnPoint.y);
    }

    public String getLevelName(int world, String levelNumber) {

        String levelName = "";
        switch (world) {
            case THE_CITY:
                levelName = "The City " + levelNumber;
                break;
            case SNOW_CITY:
                levelName = "Snow City " + levelNumber;
                break;
            case CITY_PARK:
                levelName = "City Park " + levelNumber;
                break;
            default:
                throw new RuntimeException("Unknown world: " + world);

        }
        return levelName;
    }

    public Preferences getMapScores() {
        return mapScores;
    }

    public Stage getPausedStage() {
        return stagePause;
    }

    public Stage getFinishStage() {
        return stageFinishMenu;
    }

    public Stage getgameStartStage() {
        return stageGameStart;
    }

    private void clearEnemies() {
        slimes.clear();
    }

    public void update(float delta) {


        switch (currentGameState) {
            case RUNNING:
                SoundManager.INSTANCE.manageGameMusic(delta);
                if (player.isAlive()) {
                    processInput();
                }
                else {
                    resetPlayerStatesAndButtons();
                    player.setState(State.FALLING);
                }
                updateTimer(delta);
                updateKenny(delta);
                updateBenny(delta);
                updateSnowmen(delta);
                updateSlimes(delta);
                break;
            case PAUSED:
                // Let the renderer handle it with its menu.
                // Dont update anything since its paused.
                break;
            case FINISHED:
                // Upadate the cam for finish table if
                // we finished while jumping to make the stage
                // follow us while falling down again.
                SoundManager.INSTANCE.manageGameMusic(delta);
                Camera cam = stageFinishMenu.getCamera();
                finishTable.setPosition(cam.position.x - cam.viewportWidth / 2f, cam.position.y - cam.viewportHeight / 2f);
                updateKenny(delta);
                updateSnowmen(delta);
                updateBenny(delta);
                break;
            case READY:
                SoundManager.INSTANCE.manageGameMusic(delta);
                // -- IMPORTANT --  HACK
                addDelay(delta);
                // RETURN so it keeps track of the delay NOT reseting it.
                return;
            case CAM_INITIALIZATION:
                updateKenny(delta);
                updateSnowmen(delta);
                updateBenny(delta);
                processStartInput();
                break;
            default:
                throw new RuntimeException("WTF unknown state:" + currentGameState.toString());
        }
        resetDelay();
    }

    private void clearObjects() {
        // Reset level objects
        enemyStartpositions.clear();
        snowmen.clear();
        trampolines.clear();
        clearEnemies();
    }

    private void addDelay(float delta) {
        updateKenny(delta);
        updateSnowmen(delta);
        updateBenny(delta);
        if (readyDelay < 0.7f) {
            readyDelay += delta;
        }
        else {
            // Let the user start the game, this is to avoid skipping hovering and start the game with 1 click.
            // And avoid thread sleeps.
            processStartInput();
        }
    }

    private void resetDelay() {
        readyDelay = 0f;
    }

    private void updateBenny(float delta) {
        float oldY;
        benny.updateState(delta);

        oldY = benny.getBounds().y;
        benny.getVelocity().y -= GRAVITY * delta;
        benny.updateState(delta);
        benny.getBounds().y += benny.getVelocity().y;
        checkCollisionsY(benny, oldY);
    }

    public void startTimer() {
        isTimerOn = true;
    }

    public void stopTimer() {
        isTimerOn = false;
    }

    public void resetTimer() {
        timer = 0;
    }

    public float getTimer() {
        return timer;
    }

    public boolean isLeftPressed() {
        return buttonLeftPressed;
    }

    public boolean isRightPressed() {
        return buttonRightPressed;
    }

    public boolean isJumpPressed() {
        return buttonJumpPressed;
    }

    public boolean isFirePressed() {
        return buttonSpeedPressed;
    }

    public boolean isPausePressed() {
        return buttonPausePressed;
    }

    public boolean isToggleButtonPressed() {
        return toggleButtonPressed;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public Player getPlayer() {
        return player;
    }

    public Benny getBenny() {
        return benny;
    }

    public Array<Slime> getSlimes() {
        return slimes;
    }

    public Array<Snowman> getSnowmen() {
        return snowmen;
    }

    public Array<Trampoline> getTrampolines() {
        return trampolines;
    }

    public Vector2 getTriggerJumpSmokePosition() {
        return this.triggerJumpSmokePosition;
    }

    public void initriggerJumpSmoke(float triggerPosX, float triggerPosY) {
        this.isSmokeJumpTrigged = true;
        this.triggerJumpSmokePosition.set(triggerPosX, triggerPosY);
    }

    public boolean isSmokeJumpTrigged() {
        return this.isSmokeJumpTrigged;
    }

    public void setSmokeJumpTrigged(boolean trigged) {
        this.isSmokeJumpTrigged = trigged;
    }

    public GameState getCurrentState() {
        return currentGameState;
    }

    public void pauseGame(boolean isPaused) {
        if (currentGameState == GameState.READY || currentGameState == GameState.CAM_INITIALIZATION || currentGameState == GameState.FINISHED) {
            // Just return if pause is called during the ready state.
            return;
        }
        if (isPaused) {
            System.out.println("PAUSED");
            SoundManager.INSTANCE.pauseGameMusic();
            currentGameState = GameState.PAUSED;
            stagePause.addAction(Actions.alpha(1));
            Gdx.input.setInputProcessor(stagePause);
            Camera cam = stagePause.getCamera();
            pauseTable.setPosition(cam.position.x - cam.viewportWidth / 2f, cam.position.y - cam.viewportHeight / 2f);
        }
        else {
            SoundManager.INSTANCE.playGameMusic();
            currentGameState = GameState.RUNNING;
            Gdx.input.setInputProcessor(this);
        }
    }

    private void finishGame() {
            player.setState(State.IDLE);
            currentGameState = GameState.FINISHED;
            stageFinishMenu.addAction(Actions.alpha(1));
            Gdx.input.setInputProcessor(stageFinishMenu);
            Camera cam = stageFinishMenu.getCamera();
            finishTable.setPosition(cam.position.x - cam.viewportWidth / 2f, cam.position.y - cam.viewportHeight / 2f);
    }

    private void parseMapObjects() {
        MapLayer objectLayer = currentLevel.getObjectLayer();
        Rectangle rectSpawn = ((RectangleMapObject) objectLayer.getObjects().get("spawnpoint")).getRectangle();
        this.levelGoal = ((RectangleMapObject) objectLayer.getObjects().get("goal")).getRectangle();
        spawnPoint = new Vector2(rectSpawn.x, rectSpawn.y);

        Rectangle bennyPos = ((RectangleMapObject) objectLayer.getObjects().get("benny")).getRectangle();
        this.benny = new Benny(bennyPos.x, bennyPos.y, 32f, 48f, true);


        boolean up = false;

        for (MapObject ob : objectLayer.getObjects()) {
            if (ob instanceof RectangleMapObject) {
                if (ob.getName().equals("coin")) {
                    Rectangle coinPos = ((RectangleMapObject) ob).getRectangle();
                    coins.add(new Coin(coinPos.x, coinPos.y, 10f, 10f, up));
                    up = !up;
                }
                else if (ob.getName().equals("slime")) {
                    if (ob.getProperties().containsKey("pink")) {
                        Rectangle slimePos = ((RectangleMapObject) ob).getRectangle();
                        final Slime slime = new Slime(slimePos.x, slimePos.y, 36f, 24f, PINK_VELOCITY_MULTIPLIER, Slime.Type.PINK);
                        slimes.add(slime);
                        enemyStartpositions.add(slime.copy());
                    }
                    else if (ob.getProperties().containsKey("green")) {
                        Rectangle slimePos = ((RectangleMapObject) ob).getRectangle();
                        final Slime slime = new Slime(slimePos.x, slimePos.y, 36f, 24f, GREEN_VELOCITY_MULTIPLIER, Slime.Type.GREEN);
                        slimes.add(slime);
                        enemyStartpositions.add(slime.copy());

                    }
                    else if (ob.getProperties().containsKey("red")) {
                        Rectangle slimePos = ((RectangleMapObject) ob).getRectangle();
                        final Slime slime = new Slime(slimePos.x, slimePos.y, 36f, 24f, RED_VELOCITY_MULTIPLIER, Slime.Type.RED);
                        slimes.add(slime);
                        enemyStartpositions.add(slime.copy());
                    }
                    else if (ob.getProperties().containsKey("blue")) {
                        Rectangle slimePos = ((RectangleMapObject) ob).getRectangle();
                        final Slime slime = new Slime(slimePos.x, slimePos.y, 36f, 24f, BLUE_VELOCITY_MULTIPLIER, Slime.Type.BLUE);
                        slimes.add(slime);
                        enemyStartpositions.add(slime.copy());
                    }
                }
                else if (ob.getName().equals("snowman")) {
                    Rectangle snowmanPos = ((RectangleMapObject) ob).getRectangle();
                    snowmen.add(new Snowman(snowmanPos.x, snowmanPos.y, 32f, 64f));
                }


//                else if (ob.getName().equals("Trampoline")) {
//                    Rectangle trampPos = ((RectangleMapObject) ob).getRectangle();
//                    trampolines.add(new Trampoline(trampPos.x, trampPos.y, 15f, 6f, TRAMPOLINE_FORCE));
//                }

                //Using tiles and creating boxes during runtime..
                //				 else if(ob.getName().equals("ItemBox")){
                //					 if(ob.getProperties().get("type").equals("yellow")){
                //						 Rectangle boxPos = ((RectangleMapObject) ob).getRectangle();
                //						 boxes.add(new ItemBox(boxPos.x, boxPos.y+7, 16f, 16f, ItemBox.Type.YELLOW));
                //					 }else if(ob.getProperties().get("type").equals("grey")){
                //						 Rectangle boxPos = ((RectangleMapObject) ob).getRectangle();
                //						 boxes.add(new ItemBox(boxPos.x, boxPos.y, 16f, 16f, ItemBox.Type.GREY));
                //					 }
//                				 }
            }
        }

        // TODO: Other enemies!
        // Get the start positions of the enemies.
//        for (Slime slime : slimes) {
//            enemyStartpositions.add(new Slime(slime.getBounds().x, slime.getBounds().y, slime.getWidth(), slime.getHeight(), slime.getType()));
//        }
    }

    private void updateSnowmen(float delta) {
        float oldY;
        for (Snowman man : snowmen) {
            oldY = man.getBounds().y;
            man.getVelocity().y -= GRAVITY * delta;
            man.updateState(delta);
            man.getBounds().y += man.getVelocity().y;
            checkCollisionsY(man, oldY);
        }
    }

    private void updateSlimes(float delta) {
        float oldX, oldY;
        for (Slime s : slimes) {
            oldX = s.getBounds().x;
            oldY = s.getBounds().y;
            s.getVelocity().y -= GRAVITY * delta;

            if (s.getVelocity().y < -MAX_FALL_SPEED) { s.getVelocity().y = -MAX_FALL_SPEED; }

            if (s.isAlive()) {
                if (s.isFacingLeft()) {
                    s.getVelocity().x = -s.getSlimeVelocityMultiplier() * SLIME_MAX_VEL * delta;
                }
                else {
                    s.getVelocity().x = s.getSlimeVelocityMultiplier() * SLIME_MAX_VEL * delta;
                }
                s.getBounds().x += s.getVelocity().x;
                checkCollisionsX(s, oldX);
                s.getBounds().y += s.getVelocity().y;
                checkCollisionsY(s, oldY);
            }
            else {
                //Dubbelkod, genom att konstiga buggar uppst�r om man inte k�r collisiondetection
                //innan man updatear men l�r trycka ner fienden om han �r d�d.
                s.getBounds().y += s.getVelocity().y;
            }
            s.updateState(delta);
            if (s.getBounds().y < -50) { slimes.removeValue(s, true); }

            if (player.getBounds().overlaps(s.getBounds()) && s.isAlive() && player.isAlive()) {

                killPlayer();
            }
        }
    }

    private void updateTimer(float delta) {
        if (isTimerOn) {
            timer += delta;
        }
    }

    private void updateKenny(float delta) {
        float oldX = player.getBounds().x;
        float oldY = player.getBounds().y;

        player.getVelocity().x += player.getAcceleration().x * delta;
        player.getVelocity().y -= GRAVITY * delta;

        boolean isSpeedAdjusted = false;

        //Cap the flying speed...
        if (player.getState() == State.FLYING) {
            setSpeedRunning(false);
        }

        if (Math.abs(player.getVelocity().x) > MAX_VEL_ORG) {
            if (player.isSpeedRunning()) {
                if (player.getVelocity().x > MAX_VEL_SPEEDRUN) {
                    player.getVelocity().x = MAX_VEL_SPEEDRUN;
                }
                else if (player.getVelocity().x < -MAX_VEL_SPEEDRUN) {
                    player.getVelocity().x = -MAX_VEL_SPEEDRUN;
                }
            }
            else {
                isSpeedAdjusted = true;
                if (player.getVelocity().x > 0.1f) {
                    player.getVelocity().x -= ACCELERATION * 1.6f * delta;
                }
                else {
                    player.getVelocity().x += ACCELERATION * 1.6f * delta;
                }
            }
            if (!isSpeedAdjusted && !player.isSpeedRunning()) {
                //RESET
                if (player.getVelocity().x < -MAX_VEL_ORG) {
                    player.getVelocity().x = -MAX_VEL_ORG;
                }
                else if (player.getVelocity().x > MAX_VEL_ORG) {
                    player.getVelocity().x = MAX_VEL_ORG;
                }
            }
        }

        if (player.getVelocity().y < -MAX_FALL_SPEED) {
            player.getVelocity().y = -MAX_FALL_SPEED;
        }

        if (player.getCanJump()) {
            player.setFacingLeft(player.getVelocity().x < 0);
        }
        else {

            if (buttonLeftPressed) {
                player.setFacingLeft(true);
            }
            else if (buttonRightPressed) {
                player.setFacingLeft(false);
            }
        }

        if (player.isAlive()) {
            //Save if we get a jumpcollision on the head and use these values to not slow down the velocity
            tempVel = player.getVelocity().x;
            tempAcc = player.getAcceleration().x;

            player.getBounds().x += player.getVelocity().x;
            checkCollisionsX(player, oldX);
            player.getBounds().y += player.getVelocity().y;
            checkCollisionsY(player, oldY);

            checkStaticObjectCollisions(delta);

            if (!buttonLeftPressed && !buttonRightPressed) {
                player.getAcceleration().x *= DAMP;
                player.getVelocity().x *= DAMP;
            }
            else if (player.getState().equals(State.HALT)) {
                player.getAcceleration().x *= TURN_DAMP;
                player.getVelocity().x *= TURN_DAMP;
            }
        }
        else {
            player.getBounds().y += player.getVelocity().y;
        }

        player.updateState(delta);

        //Outside map respawn player..
        if (player.getBounds().y < -150) {
            resetCurrentGame();
        }

        if (player.getState() == State.FLYING) {
            if (System.currentTimeMillis() - startCopterTime >= 110l) {
                startCopterTime = System.currentTimeMillis();
                SoundManager.INSTANCE.playCopterCap();
            }
        }
        levelFinishedCheck();
        if (currentGameState == GameState.FINISHED) {
            player.getVelocity().x = 0f;
            player.getBounds().x = oldX;
        }
    }

    private void levelFinishedCheck() {
        if (currentGameState == GameState.FINISHED) {
            // Keep updating kenny animation etc, but dont keep file I/O in a loop.
            return;
        }
        if (player.getBounds().overlaps(levelGoal)) {
            benny.setHappy();
            stopTimer();
            finishGame();
            if (mapScores.contains(Base64Coder.encodeString(currentLevel.getKey()))) {
                float mapValue = Float.valueOf(Base64Coder.decodeString(mapScores.getString(Base64Coder.encodeString(currentLevel.getKey()))));
                System.out.println("comparing " + timer + " < " + mapValue);
                if (timer < mapValue) {
                    System.out.println("New highscore on existing map, saving: " + mapValue);
                    mapScores.putString(Base64Coder.encodeString(currentLevel.getKey()), Base64Coder.encodeString(Float.toString(timer)));
                    mapScores.flush();
                }
                else {
                    System.out.println("Too bad time, not saving..");
                }
            }
            else {
                System.out.println("First highscore on current map, storing: " + timer);
                mapScores.putString(Base64Coder.encodeString(currentLevel.getKey()), Base64Coder.encodeString(Float.toString(timer)));
                mapScores.flush();
            }
        }
    }

    private void checkStaticObjectCollisions(float delta) {

        //Trampolines
        for (Trampoline t : trampolines) {
            if (player.getBounds().overlaps(t.getBounds())) {
                t.hit();
                player.getVelocity().y = t.getForce();
                player.getBounds().y = t.getBounds().y + t.getHeight();
                SoundManager.INSTANCE.playTrampoline();
            }
        }
    }


    private void checkCollisionsX(MoveableEntity movEnt, float oldX) {
        boolean collisionX = false;

        currentItemBox = null;
        for (ItemBox ib : boxes) {
            if (movEnt.getBounds().overlaps(ib.getBounds())) {
                currentItemBox = ib;
                break;
            }
        }

        boolean top = false;
        if (movEnt.getVelocity().x > 0) { // going right
            if (currentItemBox != null) {
                collisionX = true;
            }

            // right top
            if (!collisionX) {
                collisionX = isCellBlocked(collisionLayer.getCell((int) ((movEnt.getBounds().x + movEnt.getWidth()) / TILEWIDTH), (int) ((movEnt.getBounds().y + movEnt.getHeight()) / TILEHEIGHT)));
                top = collisionX;
            }
            // right middle
            if (!collisionX) {
                collisionX = isCellBlocked(collisionLayer.getCell((int) ((movEnt.getBounds().x + movEnt.getWidth()) / TILEWIDTH),
                                                                  (int) ((movEnt.getBounds().y + movEnt.getHeight() / 2) / TILEHEIGHT)));
            }
            // right bottom
            if (!collisionX && movEnt.getVelocity().y != 0) {
                collisionX = isCellBlocked(collisionLayer.getCell((int) ((movEnt.getBounds().x + movEnt.getWidth()) / TILEWIDTH), (int) (movEnt.getBounds().y / TILEHEIGHT)));
            }
        }

        if (movEnt.getVelocity().x < 0) { // going left
            if (currentItemBox != null) {
                collisionX = true;
            }


            // left top
            if (!collisionX) {

                collisionX = isCellBlocked(collisionLayer.getCell((int) (movEnt.getBounds().x / TILEWIDTH), (int) ((movEnt.getBounds().y + movEnt.getHeight()) / TILEHEIGHT)));
                    top = collisionX;
            }
            // left middle
            if (!collisionX) {
                collisionX = isCellBlocked(collisionLayer.getCell((int) (movEnt.getBounds().x / TILEWIDTH),
                                                                  (int) ((movEnt.getBounds().y + movEnt.getHeight() / 2) / TILEHEIGHT)));
            }
            //                 left bottom
            if (!collisionX && movEnt.getVelocity().y != 0) {
                collisionX = isCellBlocked(collisionLayer.getCell((int) (movEnt.getBounds().x / TILEWIDTH), (int) (movEnt.getBounds().y / TILEHEIGHT)));
            }
        }

        if (collisionX) {
            if (movEnt instanceof Player) {
                if (!jumpYCollision) {
                    //Just so we keep the faciing/direciton of the character on collisions
                    player.getVelocity().x =  player.getVelocity().x * 0.1f;
                }
                if (player.getCanJump()) {
                    player.setState(State.IDLE);
                }
            }
            else if (movEnt instanceof Slime) {
                movEnt.setFacingLeft(!movEnt.isFacingLeft());
            }
            movEnt.getBounds().x = oldX;
            if (!(movEnt instanceof Player) && !top) {
                movEnt.getVelocity().x = 0;
            }
//            else if (movEnt instanceof Player) {
//                if (!top || player.getCanJump()) {
//                    player.getVelocity().x = 0;
//                System.out.println("RESET " + player.getVelocity().x);
//                    player.getAcceleration().x = 0;
//                }
//            }
        }
    }

    private void checkCollisionsY(MoveableEntity movEnt, float oldY) {
        boolean collisionY = false;
        boolean killedEnemy = false;
        jumpYCollision = false;
        if (movEnt instanceof Player) {
            ((Player) movEnt).setCanJump(false);
        }

        currentItemBox = null;
        for (ItemBox ib : boxes) {
            if (movEnt.getBounds().overlaps(ib.getBounds())) {
                currentItemBox = ib;
                break;
            }
        }

        if (movEnt.getVelocity().y < 0) { // going down
            if (currentItemBox != null) {
                collisionY = true;
            }
            else if (movEnt instanceof Player) {
                for (Slime s : slimes) {
                    if (movEnt.getBounds().overlaps(s.getBounds()) && s.isAlive() && movEnt.getBounds().y > s.getBounds().y + s.getHeight() * 0.15f) {
                        s.setHeight(s.getHeight() / 2f);
                        s.getVelocity().x = 0;
                        s.setAlive(false);
                        killedEnemy = true;
                        collisionY = true;
                        SoundManager.INSTANCE.playSlimeSmash();
                    }
                }
            }

            // bottom left
            if (!collisionY) {
                collisionY = isCellBlocked(collisionLayer.getCell((int) (movEnt.getBounds().x / TILEWIDTH), (int) (movEnt.getBounds().y / TILEHEIGHT)));
            }

            // bottom middle
            if (!collisionY) {
                collisionY = isCellBlocked(collisionLayer.getCell((int) ((movEnt.getBounds().x + movEnt.getWidth() / 2) / TILEWIDTH),
                                                                  (int) (movEnt.getBounds().y / TILEHEIGHT)));
            }
            // bottom right
            if (!collisionY) {
                collisionY = isCellBlocked(collisionLayer.getCell((int) ((movEnt.getBounds().x + movEnt.getWidth()) / TILEWIDTH), (int) (movEnt.getBounds().y / TILEHEIGHT)));
            }

            if (collisionY && movEnt instanceof Player) {
                player.setCanJump(true);
                flyModeOff();
                //Weird calculation but this gets the true position of the tile.
                player.getBounds().y = (int) (player.getBounds().y / TILEHEIGHT) * TILEHEIGHT + TILEHEIGHT;
            }
        }

        if (movEnt.getVelocity().y > 0) { // going up

            // top left
            if (!collisionY) {
                collisionY = isCellBlocked((int) (movEnt.getBounds().x / TILEWIDTH), (int) ((movEnt.getBounds().y + movEnt.getHeight()) / TILEHEIGHT));
            }

            //Top middle
            if (!collisionY) {
                collisionY = isCellBlocked((int) ((movEnt.getBounds().x + movEnt.getWidth() / 2) / TILEWIDTH), (int) ((movEnt.getBounds().y + movEnt.getHeight()) / TILEHEIGHT));
            }

            // top right
            if (!collisionY) {
                collisionY = isCellBlocked((int) ((movEnt.getBounds().x + movEnt.getWidth()) / TILEWIDTH), (int) ((movEnt.getBounds().y + movEnt.getHeight()) / TILEHEIGHT));
            }
            if (collisionY && movEnt instanceof Player) {
                //Weird calculation but this gets the true position of the tile.
                player.getBounds().y =  (int) ((player.getBounds().y + movEnt.getHeight()) / TILEHEIGHT) * TILEHEIGHT - player.getHeight();
            }
        }

        if (collisionY) {
            if (movEnt instanceof Player) {
                this.jumpPressed = false;
                player.getVelocity().y = 0;
                if (!player.getCanJump()) {
                    jumpYCollision = true;
                    player.getVelocity().x = tempVel;
                    player.getAcceleration().x = tempAcc;
                    player.getBounds().y = oldY;
                }
            }
            if (movEnt instanceof Player && killedEnemy) {
                movEnt.getVelocity().y = BOUNCE_FORCE;
            }
            else if (!(movEnt instanceof Player)) {
                movEnt.getBounds().y = oldY;
                movEnt.getVelocity().y = 0;
            }
        }
    }

    private void respawnPlayer() {
        player.setPos(spawnPoint.x, spawnPoint.y);
        player.setAlive(true);
        player.getVelocity().x = 0;
        player.getAcceleration().x = 0;
        player.resetHp();
        //    	player.setImmune(true);
    }

    private boolean isCellBlocked(Cell cell) {
        if (cell != null && cell.getTile() != null) {
            if (cell.getTile().getProperties().containsKey(SPIKES_KEY)) {
                if (player.isAlive()) {
                    killPlayer();
                }
                return false;
            }
            else {
                return cell.getTile().getProperties().containsKey(BLOCKED_KEY);
            }
        }
        return false;
    }

    private void killPlayer() {
        MAX_FALL_SPEED = MAX_FALL_SPEED_ORG;
        player.setAlive(false);
        player.getAcceleration().x = 0;
        // This just to keep the direction of the player.
        player.getVelocity().x *= 0.01;
        player.getVelocity().y = DEAD_BOUNCE_FORCE;
        SoundManager.INSTANCE.playPlayedDied();
    }

    private boolean isCellBlocked(int x, int y) {
        Cell c = collisionLayer.getCell(x, y);
        return c != null && c.getTile() != null && c.getTile().getProperties().containsKey(BLOCKED_KEY);
    }

    private void processInput() {
        Gdx.input.setInputProcessor(this);

        resetPlayerStatesAndButtons();

        int togglePressed = -1;

        int flyButtonPressed = -1;

        //Reset speedrun
        setSpeedRunning(false);
        //Reset the jump force.
        player.getAcceleration().y = 0;
        for (int i = 0; i < 3; i++) {

            int xPoint = Gdx.input.getX(i);
            int yPoint = Gdx.input.getY(i);
            if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.UP)
                    || (Gdx.input.isTouched(i) && (xPoint > Gdx.graphics.getWidth() * 0.81f) && yPoint > Gdx.graphics.getHeight() * 0.77f)) {
                buttonJumpPressed = true;
                flyButtonPressed = i;
                if (player.getCanJump()) {
//                    GRAVITY = ORG_GRAVITY;
                    MAX_FALL_SPEED = MAX_FALL_SPEED_ORG;
                    SoundManager.INSTANCE.playJump();
                    player.getVelocity().y = JUMP_FORCE;
                    player.setCanJump(false);
                    jumpPressedTime = System.currentTimeMillis();
                    jumpPressed = true;
                    player.setCanDoubleJump(true);
                }
                else {
                    if (!player.isJumpBootsOn() && player.isFlying()) {
                        jumpPressed = false;
                    }
                    else if (System.currentTimeMillis() - jumpPressedTime >= LONG_JUMP_PRESS * 1.1f && jumpPressed) {
                        jumpPressed = false;
                    }
                    else if (jumpPressed && !player.getState().equals(State.FLYING)) {
                        float jumpForce = JUMP_FORCE * 1.45f * 58 * (Gdx.graphics.getDeltaTime());
                        //This one resets the force if the device is lagging heavily.
                        if (jumpForce > JUMP_FORCE * 1.45f * 1.1f) {
                            jumpForce = JUMP_FORCE * 1.45f * 1.1f;
                        }
                        player.getVelocity().y = jumpForce;
                    }
                }
            }
            //Either u jump or pause ising the right area of the screen
            else if (Gdx.input.isTouched(i) && (xPoint > Gdx.graphics.getWidth() * 0.81f) && yPoint < Gdx.graphics.getHeight() * 0.26f) {
                buttonPausePressed = true;
                SoundManager.INSTANCE.playPaused();
                pauseGame(true);
            }
            //TODO FIXA BRA LÖSNING
            if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isTouched(i) && xPoint < Gdx.graphics.getWidth() / 5.8f &&
                                                     yPoint > Gdx.graphics.getHeight() * 0.77f) {
                buttonLeftPressed = true;
                player.getAcceleration().x = -ACCELERATION;

            }
            else if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isTouched(i) && xPoint >= Gdx.graphics.getWidth() / 5.8f &&
                                                           xPoint < Gdx.graphics.getWidth() / 2f && yPoint > Gdx.graphics.getHeight() * 0.77f) {
                buttonRightPressed = true;
                player.getAcceleration().x = ACCELERATION;
            }

            if (((Gdx.input.isTouched(i) && xPoint > Gdx.graphics.getWidth() * 0.55f)
                && xPoint < Gdx.graphics.getWidth() * 0.81f && yPoint > Gdx.graphics.getHeight() * 0.77f) || Gdx.input.isKeyPressed(Keys.Q)) {
                buttonSpeedPressed = true;
                setSpeedRunning(true);
            }

            if (!player.isFlying() && Math.abs(player.getVelocity().x) > 0.32f) {
                player.setState(State.WALKING);
            }

            if (((Gdx.input.isTouched(i) && xPoint > Gdx.graphics.getWidth() * 0.78f) && yPoint > Gdx.graphics.getHeight() * 0.55f && yPoint < Gdx.graphics.getHeight() * 0.77f)) {
                togglePressed = i;
            }

        }
        //this relates to the flying bug while dragging away the finger.
        if (flyButtonPressed == -1) {
            flyModeOff();
        }
        if (togglePressed == -1) {
            toggleButtonPressed = false;
        }

        if (player.getVelocity().y > 0.1f && !player.getCanJump() && !player.isFlying()) {
            player.setState(State.JUMPING);
        }
        else if (player.getVelocity().y < - 0.05f && !player.getCanJump() && !player.isFlying()) {
            player.setState(State.FALLING);
        }
        else if (Math.abs(player.getVelocity().x) > 0.35f && !buttonRightPressed && !buttonLeftPressed && player.getCanJump()) {
            player.setState(State.HALT);
        }
        else if (((player.getVelocity().x < -0.01f && buttonRightPressed) || (player.getVelocity().x > 0.01f && (buttonLeftPressed))) && player.getCanJump()) {
            player.setState(State.HALT);
        }
        else if (player.isFlying() && !player.getCanJump()) {
            player.setState(State.FLYING);
        }
    }

    private void processStartInput() {
        if (currentGameState == GameState.READY && (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isTouched())) {
            SoundManager.INSTANCE.switchToGameMusic();
            currentGameState = GameState.RUNNING;
            startTimer();
            finishGame();
        }
        // Touch or keypress duriong the hovering, just go to the ready state and wait for another keypress/click
        else if (currentGameState == GameState.CAM_INITIALIZATION && (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isTouched())) {
            currentGameState = GameState.READY;

        }
    }

    private void setSpeedRunning(boolean isSpeedRunning) {
        if (isSpeedRunning) {
            this.MAX_VEL = MAX_VEL_SPEEDRUN;
            player.setSpeedRunning(true);
        }
        else {
            this.MAX_VEL = MAX_VEL_ORG;
            player.setSpeedRunning(false);

        }
    }

    public void resetCurrentGame() {
        resetStartPositions();
        resetPlayerStatesAndButtons();
        respawnPlayer();
        resetTimer();
        currentGameState = GameState.READY;
    }

    private void resetStartPositions() {
        clearEnemies();
        for (MoveableEntity mov :  enemyStartpositions) {
            if (mov instanceof Slime) {
                slimes.add(((Slime) mov).copy());
            }
//            else if (mov instanceof Snowman) {
//                System.out.println("snoman new addd.");
//                snowmen.add((Snowman) mov);
//            }
            // TODO: Other enemies.
        }
    }


    // Not a real screen, just an overlay.
    public void setupPauseMenu(FitViewport view, Batch batch) {

        TextureAtlas menuAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);
        Skin buttonSkin = new Skin(menuAtlas);

//        this.stage = new Stage();
        this.stagePause = new Stage(view, batch);
        this.pauseTable = new Table();
        this.pauseTable.setSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        Label.LabelStyle lStyle = new Label.LabelStyle(Assets.getRockwellFont(), Color.WHITE);
        Label label = new Label("Paused", lStyle);

        Image pauseHeader = new Image(menuAtlas.findRegion("pause_title2"));

        ImageButton.ImageButtonStyle resumeStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle retryButtonStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();


        resumeStyle.up = buttonSkin.getDrawable("btn_play_bigblue");
        resumeStyle.down = buttonSkin.getDrawable("btn_play_bigblue_pressed");
        retryButtonStyle.up = buttonSkin.getDrawable("btn_replay");
        retryButtonStyle.down = buttonSkin.getDrawable("btn_replay_pressed");
        backStyle.up = buttonSkin.getDrawable("btn_menu");
        backStyle.down = buttonSkin.getDrawable("btn_menu_pressed");

        ImageButton resumeButton = new ImageButton(resumeStyle);
        ImageButton retryButton = new ImageButton(retryButtonStyle);
        ImageButton backButton = new ImageButton(backStyle);

        resumeButton.setTransform(true);
//        resumeButton.setScale(0.5f);

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                pauseGame(false);
            }
        });

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                pauseGame(false);
                resetCurrentGame();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToLevelScreen();
            }
        });

        label.setWidth(label.getPrefWidth());
        Table miniTable = new Table();

        miniTable.add(resumeButton).width(retryButton.getWidth()).height(retryButton.getHeight()).padRight(30);
        miniTable.add(retryButton).padLeft(30).padRight(30);
        miniTable.add(backButton).padLeft(30);

        pauseTable.add(pauseHeader).padBottom(60).row();
        pauseTable.add(miniTable);
        stagePause.addActor(pauseTable);
    }

    // Not a real screen, just an overlay.
    public void setupFinishMenu(FitViewport view, Batch batch) {

        TextureAtlas menuAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);
        Skin buttonSkin = new Skin(menuAtlas);

//        this.stage = new Stage();
        this.stageFinishMenu = new Stage(view, batch);
        this.finishTable = new Table();
        this.finishTable.setSize(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        Label.LabelStyle lStyle = new Label.LabelStyle(Assets.getRockwellFont(), Color.WHITE);
        Label label = new Label("Finish", lStyle);

        ImageButton.ImageButtonStyle nextMapStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle retryButtonStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();

        nextMapStyle.up = buttonSkin.getDrawable("btn_play_bigblue");
        nextMapStyle.down = buttonSkin.getDrawable("btn_play_bigblue_pressed");
        retryButtonStyle.up = buttonSkin.getDrawable("btn_replay");
        retryButtonStyle.down = buttonSkin.getDrawable("btn_replay_pressed");
        backStyle.up = buttonSkin.getDrawable("btn_menu");
        backStyle.down = buttonSkin.getDrawable("btn_menu_pressed");

        ImageButton nextMapButton = new ImageButton(nextMapStyle);
        ImageButton retryButton = new ImageButton(retryButtonStyle);
        ImageButton backButton = new ImageButton(backStyle);


        nextMapButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                int currentLevelNumber = currentLevel.getLevelNo();
                // Ex. world_1-1
                String nextLevel;
                switch (currentLevel.getWorldNo()) {
                    case THE_CITY:
                        if (currentLevelNumber < THE_CITY_NO_LVLS) {
                            nextLevel = String.valueOf(currentLevelNumber + 1);
                            loadLevel(currentLevel.getKey().split("-")[0] + "-" + nextLevel,
                                    getLevelName(THE_CITY, nextLevel));
                        }
                        else {
                            nextLevel = String.valueOf(currentLevelNumber + 1);
                            // Load 1st level of the 2nd world
                            loadLevel("world_" + SNOW_CITY + "-1",
                                    getLevelName(SNOW_CITY, "1"));
                        }
                        break;
                    case SNOW_CITY:
                        if (currentLevelNumber < SNOW_CITY_NO_LVLS) {
                            nextLevel = String.valueOf(currentLevelNumber + 1);
                            loadLevel(currentLevel.getKey().split("-")[0] + "-" + nextLevel,
                                    getLevelName(SNOW_CITY, nextLevel));
                        }
                        else {
                            nextLevel = String.valueOf(currentLevelNumber + 1);
                            // Load 1st level of the 3rd world
                            loadLevel("world_" + CITY_PARK + "-1",
                                    getLevelName(CITY_PARK, "1"));
                        }
                        break;
                    case CITY_PARK:
                        if (currentLevelNumber < CITY_PARK_NO_LVLS) {
                            nextLevel = String.valueOf(currentLevelNumber + 1);
                            loadLevel(currentLevel.getKey().split("-")[0] + "-" + nextLevel,
                                    getLevelName(CITY_PARK, nextLevel));
                        }
                        break;
                        default:
                            throw new RuntimeException("Unknown world:" + currentLevel.getWorldNo());

                }
                resetCurrentGame();
                game.getRenderer().initRenderer();
                initMap();
            }
        });

        retryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                Gdx.input.setInputProcessor(null);
                resetCurrentGame();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToLevelScreen();
            }
        });

        label.setWidth(label.getPrefWidth());
        Table miniTable = new Table();
        miniTable.add(nextMapButton).width(retryButton.getWidth()).height(retryButton.getHeight()).padRight(30);
        miniTable.add(retryButton).padLeft(30).padRight(30);
        miniTable.add(backButton).padLeft(30);

        finishTable.add(label).padBottom(60).row();
        finishTable.add(miniTable);
        stageFinishMenu.addActor(finishTable);
    }

    //Only for not be able to spamming jump and fire so we cant use polling
    @Override
    public boolean keyDown(int keycode) {
        if (!player.isAlive()) {
            return false;
        }
        switch (keycode) {
            case Keys.W:
                break;
            case Keys.D:
                break;
            case Keys.G:
                toggleAbility();
                break;
            case Keys.SPACE:
                //Fall through
            case Keys.UP:
                useAbility();
                break;
            case Keys.BACK:
                //Fall through and pause game
            case Keys.P:
                buttonPausePressed =  true;
                break;
        }
        return true;
    }


    @Override
    public boolean keyUp(int keycode) {
        if (!player.isAlive()) {
            return false;
        }
        switch (keycode) {
            case Keys.D:
                buttonSpeedPressed = false;
                break;
            case Keys.G:
                toggleButtonPressed = false;
                break;
            case Keys.SPACE:
                //Fall through
            case Keys.UP:
                if (!player.isJumpBootsOn()) {
                    flyModeOff();
                }
                break;
            case Keys.BACK:
                //Fall through and pause game
            case Keys.P:
                SoundManager.INSTANCE.playPaused();
                pauseGame(true);
                break;
        }
        return true;
    }


    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!player.isAlive()) {
            return false;
        }
//        if ((screenX > Gdx.graphics.getWidth() * 0.55f)
//            && screenX < Gdx.graphics.getWidth() * 0.72f && screenY > Gdx.graphics.getHeight() * 0.7f) {
//            fireEnergyBall();
//        }
        if ((screenX > Gdx.graphics.getWidth() * 0.78f) && screenY > Gdx.graphics.getHeight() * 0.55f && screenY < Gdx.graphics.getHeight() * 0.77f) {
            toggleAbility();
        }

        if ((screenX > Gdx.graphics.getWidth() * 0.78f) && screenY > Gdx.graphics.getHeight() * 0.77f) {
            useAbility();
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!player.isAlive()) {
            return false;
        }
//        if ((screenX > Gdx.graphics.getWidth() * 0.55f)
//            && screenX < Gdx.graphics.getWidth() * 0.72f && screenY > Gdx.graphics.getHeight() * 0.7f) {
//            buttonSpeedPressed = false;
//        }

        if ((screenX > Gdx.graphics.getWidth() * 0.78f) && screenY > Gdx.graphics.getHeight() * 0.55f && screenY < Gdx.graphics.getHeight() * 0.77f) {
            toggleButtonPressed = false;
        }
        
        if ((screenX > Gdx.graphics.getWidth() * 0.78f) && screenY > Gdx.graphics.getHeight() * 0.77f) {
            flyModeOff();
        }
        return true;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return true;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

    private void flyModeOn() {
        if (!player.getCanJump()) {
            jumpPressed = false;
            player.setFlying(true);
            player.setState(State.FLYING);
            if (player.getVelocity().y > 0) {
                player.getVelocity().y *= 0.6f;
                player.getAcceleration().y = 0;
            }
            MAX_FALL_SPEED = MAX_FALL_SPEED_FLYING;
        }
    }

    private void flyModeOff() {
        MAX_FALL_SPEED = MAX_FALL_SPEED_ORG;
        player.setFlying(false);
    }

    private void useAbility() {
        if (!player.isJumpBootsOn()) {
            flyModeOn();
        }
        // In air
        else if (player.isJumpBootsOn() && !player.getCanJump() && player.getCanDoubleJump()) {
            //                    jumpPressedTime = System.currentTimeMillis();
            SoundManager.INSTANCE.playDoubleJumpp();
            initriggerJumpSmoke(player.getBounds().x, player.getBounds().y);
            player.setCanDoubleJump(false);
            player.getVelocity().y = JUMP_FORCE * 1.4f;
        }
    }

    private void resetPlayerStatesAndButtons() {
        benny.resetBenny();
        player.setState(State.IDLE);
        buttonRightPressed = false;
        buttonLeftPressed = false;
        buttonJumpPressed = false;
        buttonSpeedPressed = false;
        buttonPausePressed = false;
    }

    private void toggleAbility() {
        SoundManager.INSTANCE.playToggleAbility();
        player.setJumpBootsOn(!player.isJumpBootsOn());
        toggleButtonPressed = true;
    }

    private Vector2 getStageLocation(Actor actor) {
        return actor.localToStageCoordinates(new Vector2(0, 0));
    }

    private void goToLevelScreen() {
        SoundManager.INSTANCE.playButtonClick();
        Gdx.input.setInputProcessor(null);
        SoundManager.INSTANCE.swtichToMenuMusic();
        game.setScreen(game.getLevelScreen());
    }
}