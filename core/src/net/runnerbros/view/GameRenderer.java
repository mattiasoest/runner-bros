package net.runnerbros.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.controller.Level;
import net.runnerbros.entities.Benny;
import net.runnerbros.entities.Player;
import net.runnerbros.entities.Slime;
import net.runnerbros.entities.Snowman;

public class GameRenderer {

    private static final float CAMERA_STATIC_TIMER = 1.5f;
    //Hover camera properties
    private float cameraStaticTimer = 0f;
    private float cameraHoverSpeed = 50f;

    private final FitViewport        view;
    private final Sprite             buttonPauseSprite;
    private final Sprite             buttonPausePressedSprite;

    private float hoverDifferenceX = 0f;

    private Level currentLevel;

    private Sprite buttonPauseSp;
    private CameraManager cameraManager;

    private OrthogonalTiledMapRenderer renderer;
    private float                      buttonDiam;
    private boolean isSmokeTurnPlaying = false;
    private boolean isSmokeHaltPlaying = false;
    private boolean isSmokeJumpPlaying = false;
    private boolean isHaltLeft         = false;
    private boolean isRunningLeft      = false;

    // Original duration multiplied by the slime speed multiplier
    private static final float SLIME_FRAME_DURATION = 0.2f;


    private final float TRAMPOLINE_FRAME_DURATION = 0.09f;
    private final float SMOKE_HALT_DURATION       = 0.12f;
    private final float SMOKE_TURN_DURATION       = 0.1f;
    private final float SNOWMAN_IDLE_DURATION     = 0.3f;
    private final float BENNY_IDLE_DURATION       = 1f;
    private final float BENNY_HAPPY_DURATION      = 0.75f;

    private TextureAtlas objectAtlas;
    private TextureAtlas atlas;

    private Player player;

    private SpriteBatch batch;


    private PlayerAnimation kennyAnimation;
    private PlayerAnimation lennyAnimation;
    private PlayerAnimation currentPlayerAnimation;

    private float startYpositionHaltSmokeAanimation;
    private float startYpositionTurnSmokeAnimation;
    private float beforeJumpXpositionTurnSmokeAnimation;
    private float beforeJumpXpositionHaltSmokeAnimation;

    private GameController gc;
    private Texture        buildingsBlack;
    private Texture        buildings;
    private Texture        buildingsGround;
    private Texture        skystaticFog;
    private Texture        skystatic;

    private TextureRegion smokeHaltFrame;
    private TextureRegion smokeTurnFrame;
    private TextureRegion smokeJumpLeftFrame;
    private TextureRegion smokeJumpRightFrame;
    private TextureRegion slimeFrame;
    private TextureRegion trampFrame;
    private TextureRegion playerFrame;
    private TextureRegion snowmanFrame;
    private TextureRegion bennyFrame;

    private Animation smokeHaltRightAnimation;
    private Animation smokeTurnRightAnimation;
    private Animation smokeHaltLeftAnimation;
    private Animation smokeTurnLeftAnimation;
    private Animation smokeJumpLeftAnimation;
    private Animation smokeJumpRightAnimation;


    private Animation slimeBlueRightAnimation;
    private Animation slimeBlueLeftAnimation;
    private Animation slimePinkLeftAnimation;
    private Animation slimePinkRightAnimation;
    private Animation slimeRedLeftAnimation;
    private Animation slimeRedRightAnimation;
    private Animation slimeGreenLeftAnimation;
    private Animation slimeGreenRightAnimation;


    private Animation trampolineAnimation;
    private Animation snowmanLeftAnimation;
    private Animation bennyIdleAnimation;
    private Animation bennyHappyAnimation;

    private TextureRegion buttonLeft;
    private TextureRegion buttonRight;
    private TextureRegion buttonJump;
    private TextureRegion buttonSpeed;
    private TextureRegion buttonToggle;

    private BitmapFont font;

    //Debugging
    private ShapeRenderer sr;

    public GameRenderer(GameController gc) {
        sr = new ShapeRenderer();

        //Use this one everywhere to save resources!
        this.batch = new SpriteBatch();

        this.gc = gc;
        this.font = Assets.getRockwellFont();
        this.font.setColor(Color.WHITE);
        this.font.setUseIntegerPositions(false);

        this.cameraManager = new CameraManager();
        this.buttonDiam = GameController.VIRTUAL_WIDTH / 10f;
        cameraManager.getGameCamera().update();
        Gdx.graphics.setVSync(false);
        this.player = gc.getPlayer();
        loadAnimationsAndTextures();

        setButtonSpeed(false);
        setButtonJump(false);
        setButtonLeft(false);
        setButtonRight(false);

        TextureRegion tempPause = atlas.findRegion("btn_pause_pressed");
        TextureRegion tempPause2 = atlas.findRegion("btn_pause");
        buttonPausePressedSprite = new Sprite(tempPause);
        buttonPauseSprite = new Sprite(tempPause2);
        buttonPausePressedSprite.setScale(0.55f);
        buttonPauseSprite.setScale(0.55f);
        setButtonPause(false);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT, cameraManager.getGameCamera());
        gc.setupFinishMenu(view, batch);
        gc.setupPauseMenu(view, batch);
    }

    public void initRenderer() {
        this.currentLevel = gc.getCurrentLevel();
        resetHoverProperties();
        this.renderer = new OrthogonalTiledMapRenderer(currentLevel.getMap(), batch);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gc.getCurrentState().equals(GameController.GameState.CAM_INITIALIZATION)) {
            batch.setProjectionMatrix(cameraManager.getHoverCamera().combined);
            // Just keep it static for now by using game camera
            drawParallaxBackground(cameraManager.getHoverCamera());
            renderer.setView(cameraManager.getHoverCamera());
        }
        else {
            batch.setProjectionMatrix(cameraManager.getGameCamera().combined);
            drawParallaxBackground(cameraManager.getGameCamera());
            renderer.setView(cameraManager.getGameCamera());
        }
        renderer.render();
        batch.begin();
        drawPlayer();
        drawBenny();
        drawSnowmen();
        switch (gc.getCurrentState()) {
            case RUNNING:
                drawTimer();
                drawSlimes();
                drawSmoke(delta);
                drawButtons();
                batch.end();
                break;
            case READY:
                drawTimer();
                drawButtons();
                batch.end();
                break;
            case PAUSED:
                //Special case for when the app gets interrupted and its goes into auto pause while
                // using the hovering camera
                resetHoverProperties();

                //DRAW PAUSE MENU
                drawTimer();
                drawSlimes();
                batch.end();
                gc.getPausedStage().act(delta);
                gc.getPausedStage().draw();
                break;
            case FINISHED:
                //DRAW PAUSE MENU
                drawTimer();
                drawSlimes();
                batch.end();
                gc.getFinishStage().act(delta);
                gc.getFinishStage().draw();
                break;
            case CAM_INITIALIZATION:
                hoverCameraOverMap();
                batch.end();
                break;
        }
        // Update if we're not showing the map for the first time.
        if (!gc.getCurrentState().equals(GameController.GameState.CAM_INITIALIZATION)) {
            updateCameraPosition();
        }
    }

    private void resetHoverProperties() {
        hoverDifferenceX = 0f;
        cameraStaticTimer = 0;
    }

    public Batch getSpriteBatch() {
        return batch;
    }

    public void dispose() {
        if (currentLevel != null) {
            currentLevel.getMap().dispose();
        }
        batch.dispose();
        gc.getPausedStage().dispose();
        gc.getFinishStage().dispose();
        sr.dispose();
    }


    public void resize(int width, int height) {
        view.update(width, height);
    }

    private void drawParallaxBackground(Camera cam) {
        //paralaxx
        batch.begin();
        batch.draw(skystatic, cam.position.x - cam.viewportWidth / 2f, cam.position.y - cam.viewportHeight / 2f, cam.viewportWidth, cam.viewportHeight, 0, 1, 1, 0);

        batch.draw(buildingsGround, cam.position.x - cam.viewportWidth / 2f, cam.position.y / 1.4f - cam.viewportHeight / 3.5f, cam.viewportWidth, cam.viewportHeight, 0, 1, 1, 0);
        batch.draw(buildingsBlack, cam.position.x / 1.05f - cam.viewportWidth, cam.position.y / 1.1f - cam.viewportHeight / 2.1f, buildingsBlack.getWidth() * 4, buildingsBlack.getHeight() * 1, 0, 1, 4, 0);
        batch.draw(buildings, cam.position.x / 1.3f - cam.viewportWidth, cam.position.y / 1.4f - cam.viewportHeight / 3f, buildings.getWidth() * 4, buildings.getHeight(), 0, 1, 4, 0);

        batch.draw(skystaticFog, cam.position.x - cam.viewportWidth / 2f, cam.position.y - cam.viewportHeight / 2f, cam.viewportWidth, cam.viewportHeight, 0, 1, 1, 0);
        batch.end();
    }

    private void drawTimer() {
        float time = gc.getTimer();
        font.draw(batch, gc.getDecimalFormat().format(time), cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth / 3.35f, cameraManager.getGameCamera().position.y + cameraManager.getGameCamera().viewportHeight * 0.475f);
    }

    private void drawButtons() {
        setButtonLeft(gc.isLeftPressed());
        setButtonRight(gc.isRightPressed());
        setButtonJump(gc.isJumpPressed());
        setButtonSpeed(gc.isFirePressed());
        setButtonPause(gc.isPausePressed());
        setButtonToggle(gc.getPlayer().isJumpBootsOn());
        batch.draw(buttonLeft, cameraManager.getGameCamera().position.x - cameraManager.getGameCamera().viewportWidth * 0.46f, cameraManager.getGameCamera().position.y - cameraManager.getGameCamera().viewportHeight * 0.485f);
        batch.draw(buttonRight, cameraManager.getGameCamera().position.x - cameraManager.getGameCamera().viewportWidth * 0.3f, cameraManager.getGameCamera().position.y - cameraManager.getGameCamera().viewportHeight * 0.485f);
        batch.draw(buttonSpeed, cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth * 0.31f - buttonSpeed.getRegionWidth(), cameraManager.getGameCamera().position.y - cameraManager.getGameCamera().viewportHeight * 0.485f);
        batch.draw(buttonJump, cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth * 0.47f - buttonJump.getRegionWidth(), cameraManager.getGameCamera().position.y - cameraManager.getGameCamera().viewportHeight * 0.485f);
        batch.draw(buttonToggle, cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth * 0.47f - buttonToggle.getRegionWidth(), cameraManager.getGameCamera().position.y - cameraManager.getGameCamera().viewportHeight * 0.26f);
        buttonPauseSp.setPosition(cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth * 0.47f - buttonToggle.getRegionWidth(), cameraManager.getGameCamera().position.y + cameraManager.getGameCamera().viewportHeight * 0.22f);
        buttonPauseSp.draw(batch);

    }

    // Used once at the beginning of each map.
    private void hoverCameraOverMap() {
        float posX, posY;

        // The end of the map (hovercam start) pause for CAMERA_STATIC_TIMER sec and then start hovering
        if (cameraStaticTimer < CAMERA_STATIC_TIMER) {
            cameraStaticTimer += Gdx.graphics.getDeltaTime();
        }
        else {
            if (cameraManager.getHoverCamera().position.x + cameraManager.getHoverCamera().viewportWidth / 2 < currentLevel.getTileWidth() * (currentLevel.getCollisionLayer().getWidth() / 2)) {
                cameraHoverSpeed -= CameraManager.CAMERA_SPEED_INCREASER;
            }
            else {
                cameraHoverSpeed += CameraManager.CAMERA_SPEED_INCREASER;
            }
            if (cameraHoverSpeed > CameraManager.CAMERA_HOVERSPEED_MAX) {
                cameraHoverSpeed = CameraManager.CAMERA_HOVERSPEED_MAX;
            }
            else if (cameraHoverSpeed < CameraManager.CAMERA_HOVERSPEED_MIN) {
                cameraHoverSpeed = CameraManager.CAMERA_HOVERSPEED_MIN;
            }
            hoverDifferenceX += cameraHoverSpeed * Gdx.graphics.getDeltaTime();
        }
        // USE STATIC Y
        posX = currentLevel.getTileWidth() * (currentLevel.getCollisionLayer().getWidth() - 1) - cameraManager.getHoverCamera().viewportWidth / 2 - hoverDifferenceX;
        posY = currentLevel.getTileHeight() * 14.4f * CameraManager.HOVER_CAMERA_SIZE_MULTIPLIER - cameraManager.getHoverCamera().viewportHeight / 2;
        cameraManager.getHoverCamera().position.set(posX, posY, 0);
        // The beginning of the map
        if (cameraManager.getHoverCamera().position.x - cameraManager.getHoverCamera().viewportWidth / 2 < currentLevel.getTileWidth() * 1) {

            posX = currentLevel.getTileWidth() + cameraManager.getHoverCamera().viewportWidth / 2;
            cameraManager.getHoverCamera().position.set(posX, posY, 0);
            if (cameraStaticTimer < CAMERA_STATIC_TIMER * 2) {
                // Count another CAMERA_STATIC_TIMER by adding delta values to the current
                // cameraStaticTimer which is equal to CAMERA_STATIC_TIMER in the beginning.
                cameraStaticTimer += Gdx.graphics.getDeltaTime();
            }
            else {
                    gc.setGameState(GameController.GameState.READY);
                    // Reset the pos for next time
                    resetHoverProperties();
            }
        }

        cameraManager.getHoverCamera().update();
    }


    private void updateCameraPosition() {
        float posX, posY;
        posX = player.getBounds().x - player.getWidth() / 2;
        posY = player.getBounds().y - cameraManager.getGameCamera().viewportHeight * 0.1f; // + player.getHeight() / 2 + camera.viewportHeight / 12f
        cameraManager.getGameCamera().position.set(posX, posY, 0);
        if (cameraManager.getGameCamera().position.x - cameraManager.getGameCamera().viewportWidth / 2 < currentLevel.getTileWidth() * 1) {
            posX = currentLevel.getTileWidth() + cameraManager.getGameCamera().viewportWidth / 2;
        }
        else if (cameraManager.getGameCamera().position.x + cameraManager.getGameCamera().viewportWidth / 2 > (currentLevel.getCollisionLayer().getWidth() - 1) * currentLevel.getTileWidth()) {
            posX = currentLevel.getTileWidth() * (currentLevel.getCollisionLayer().getWidth() - 1) - cameraManager.getGameCamera().viewportWidth / 2;
        }
        if (cameraManager.getGameCamera().position.y + cameraManager.getGameCamera().viewportHeight / 2 < currentLevel.getTileHeight() * 14.4f) {
            posY = currentLevel.getTileHeight() * 14.4f - cameraManager.getGameCamera().viewportHeight / 2;
        }
        cameraManager.getGameCamera().position.set(posX, posY, 0);
        cameraManager.getGameCamera().update();
    }

//    private void drawTampolines(float delta) {
//        for (Trampoline t : gc.getTrampolines()) {
//            if (!t.isHit()) {
//                trampFrame = objectAtlas.findRegion("trampoline1");
//            }
//            else {
//                trampolineAnimation.setPlayMode(Animation.PlayMode.NORMAL);
//                trampFrame = trampolineAnimation.getKeyFrame(t.getStateTime(), false);
//                if (trampolineAnimation.isAnimationFinished(t.getStateTime())) {
//                    t.resetHit();
//                    t.resetState();
//                }
//                t.updateState(delta);
//            }
//            batch.draw(trampFrame, t.getBounds().x, t.getBounds().y, trampFrame.getRegionWidth(), trampFrame.getRegionHeight());
//        }
//    }

    private void drawSnowmen() {
        for (Snowman man : gc.getSnowmen()) {
            snowmanFrame = snowmanLeftAnimation.getKeyFrame(man.getStateTime(), true);
            batch.draw(snowmanFrame, man.getBounds().x, man.getBounds().y - 1f, man.getWidth(), man.getHeight());
        }
    }

    private void drawSlimes() {
        for (Slime s : gc.getSlimes()) {
            if (s.getType().equals(Slime.Type.BLUE)) {
                    slimeFrame = s.isFacingLeft() ? slimeBlueLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeBlueRightAnimation.getKeyFrame(s.getStateTime(), true);
            }
            else if (s.getType().equals(Slime.Type.PINK)) {
                    slimeFrame = s.isFacingLeft() ? slimePinkLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimePinkRightAnimation.getKeyFrame(s.getStateTime(), true);
            }
            else if (s.getType().equals(Slime.Type.GREEN)) {
                slimeFrame = s.isFacingLeft() ? slimeGreenLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeGreenRightAnimation.getKeyFrame(s.getStateTime(), true);
            }
            else if (s.getType().equals(Slime.Type.RED)) {
                slimeFrame = s.isFacingLeft() ? slimeRedLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeRedRightAnimation.getKeyFrame(s.getStateTime(), true);
            }

            float renderExtaWidth = 4;
            float renderExtaHeight = 8;
            float slimeHeight = s.getHeight();
            if (!s.isAlive()) {
                slimeHeight = s.getHeight() / 3f;

            }

            batch.draw(slimeFrame, s.getBounds().x - (renderExtaWidth / 2), s.getBounds().y - 2.2f, s.getWidth() + renderExtaWidth, slimeHeight + renderExtaHeight);
        }
    }

    private void drawBenny() {
        final Benny benny = gc.getBenny();
        float groundOffset= -3f;

        if (benny.isHappy()) {
            bennyFrame = bennyHappyAnimation.getKeyFrame(benny.getStateTime(), true);
            if (bennyHappyAnimation.getKeyFrameIndex(benny.getStateTime()) != 1) {
                // Middle index
                groundOffset = -1f;
            }
        }
        else {
            bennyFrame = bennyIdleAnimation.getKeyFrame(benny.getStateTime(), true);
        }
        batch.draw(bennyFrame, benny.getBounds().x, benny.getBounds().y + groundOffset, benny.getWidth(), benny.getHeight());
    }

    private void drawPlayer() {

        currentPlayerAnimation = player.isKenny() ? kennyAnimation : lennyAnimation;


        if (player.getState().equals(Player.State.WALKING)) {
            if (player.isSpeedRunning()) {
                playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getRunLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getRunRightAnimation().getKeyFrame(player.getStateTime(), true);
            }
            else {
                playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getWalkLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getWalkRightAnimation().getKeyFrame(player.getStateTime(), true);
            }
        }
        else if (player.getState().equals(Player.State.JUMPING)) {
            playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getJumpLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getJumpRightAnimation().getKeyFrame(player.getStateTime(), true);
        }
        else if (player.getState().equals(Player.State.FALLING)) {
            playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getFallLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getFallRightAnimation().getKeyFrame(player.getStateTime(), true);
        }
        else if (player.getState().equals(Player.State.FLYING)) {
            playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getFlyLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getFlyRightAnimation().getKeyFrame(player.getStateTime(), true);

        }
        else if (player.getState().equals(Player.State.HALT)) {
            if (Math.abs(player.getVelocity().x) > 0.9f && smokeHaltFrame == null && !gc.isRightPressed() && !gc.isLeftPressed() && player.getCanJump()) {
                isSmokeHaltPlaying = true;
                startYpositionHaltSmokeAanimation = player.getBounds().y - 5f;
                isHaltLeft = player.isFacingLeft();
            }
            playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getHaltLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getHaltRightAnimation().getKeyFrame(player.getStateTime(), true);
        }
        else {
            playerFrame = player.isFacingLeft() ? currentPlayerAnimation.getIdleLeftAnimation().getKeyFrame(player.getStateTime(), true) : currentPlayerAnimation.getIdleRightAnimation().getKeyFrame(player.getStateTime(), true);
        }


        float height = player.getHeight() + 6;
        float yPos = player.getBounds().y;
        //Small hack since the flying frames are 80p in height instead of 64p
        if (player.getState().equals(Player.State.FLYING)) {
            height = 80f;
            yPos -= 3f;

        }
        batch.draw(playerFrame, player.getBounds().x - 25, yPos, player.getWidth() + 50, height);
    }

    private void drawSmoke(float delta) {
        smokeHaltFrame = null;
        smokeTurnFrame = null;
        smokeJumpLeftFrame = null;
        smokeJumpRightFrame = null;


        if (((gc.isLeftPressed() && player.getVelocity().x > 1f) || (gc.isRightPressed() && player.getVelocity().x < - 1f)) && player.getCanJump()) {
            isSmokeTurnPlaying = true;
            startYpositionTurnSmokeAnimation = player.getBounds().y - 5f;
            isRunningLeft = player.isFacingLeft();
        }

        if (gc.isSmokeJumpTrigged()) {
            isSmokeJumpPlaying = true;
            gc.setSmokeJumpTrigged(false);
        }

        if (isSmokeHaltPlaying) {
            player.updateHaltSmokeState(delta);
            smokeHaltFrame = player.isFacingLeft() ? smokeHaltRightAnimation.getKeyFrame(player.getHaltSmokeState(), false) : smokeHaltLeftAnimation.getKeyFrame(player.getHaltSmokeState(), false);
            if (isHaltLeft) {
                if (smokeHaltRightAnimation.isAnimationFinished(player.getHaltSmokeState())){
                    smokeHaltFrame = null;
                    player.resetHaltSmokeState();
                    isSmokeHaltPlaying = false;
                }
            }
            else {
                if (smokeHaltLeftAnimation.isAnimationFinished(player.getHaltSmokeState())) {
                    smokeHaltFrame = null;
                    player.resetHaltSmokeState();
                    isSmokeHaltPlaying = false;
                }
            }
        }
        if (isSmokeTurnPlaying) {
            player.updateTurnSmokeState(delta);
            smokeTurnFrame = player.isFacingLeft() ? smokeTurnRightAnimation.getKeyFrame(player.getTurnSmokeState(), false) : smokeTurnLeftAnimation.getKeyFrame(player.getTurnSmokeState(), false);
            if (isRunningLeft) {
                if (smokeTurnRightAnimation.isAnimationFinished(player.getTurnSmokeState())){
                    smokeTurnFrame = null;
                    player.resetTurnSmokeState();
                    isSmokeTurnPlaying = false;
                }
            }
            else {
                if (smokeTurnLeftAnimation.isAnimationFinished(player.getTurnSmokeState())) {
                    smokeTurnFrame = null;
                    player.resetTurnSmokeState();
                    isSmokeTurnPlaying = false;
                }
            }
        }

        if (isSmokeJumpPlaying) {
            player.updateJumpSmokeState(delta);
            smokeJumpLeftFrame = smokeJumpLeftAnimation.getKeyFrame(player.getJumpSmokeState(), false);
            smokeJumpRightFrame = smokeJumpRightAnimation.getKeyFrame(player.getJumpSmokeState(), false);

            //Only one animation has to finnish since they started the at the same time.
            if (smokeJumpLeftAnimation.isAnimationFinished(player.getJumpSmokeState())){
                smokeJumpLeftFrame = null;
                smokeJumpRightFrame = null;
                player.resetJumpSmokeState();
                isSmokeJumpPlaying = false;
            }
        }

        if (smokeJumpRightFrame != null && smokeJumpLeftFrame != null) {

            Vector2 pos = gc.getTriggerJumpSmokePosition();

            batch.draw(smokeJumpRightFrame, pos.x - 2f , pos.y, player.getWidth() + 14f, player.getHeight() * 0.7f);
            batch.draw(smokeJumpLeftFrame, pos.x - 10f, pos.y, player.getWidth() + 14f, player.getHeight() * 0.7f);
        }

        if (smokeHaltFrame != null) {
            if (player.isFacingLeft()) {
                if (player.getCanJump()) {
                    beforeJumpXpositionHaltSmokeAnimation = player.getBounds().x - 2f;
                }
            }
            else {
                if (player.getCanJump()) {
                    beforeJumpXpositionHaltSmokeAnimation = player.getBounds().x - 10;
                }
            }
            batch.draw(smokeHaltFrame, beforeJumpXpositionHaltSmokeAnimation, startYpositionHaltSmokeAanimation, player.getWidth() + 14f, player.getHeight() * 0.7f);
        }

        if (smokeTurnFrame != null) {
            if (player.isFacingLeft()) {
                if (player.getCanJump()) {
                    beforeJumpXpositionTurnSmokeAnimation = player.getBounds().x - 2f;
                }
            }
            else {
                if (player.getCanJump()) {
                    beforeJumpXpositionTurnSmokeAnimation = player.getBounds().x - 10f;
                }
            }

            batch.draw(smokeTurnFrame, beforeJumpXpositionTurnSmokeAnimation, startYpositionTurnSmokeAnimation , player.getWidth() + 14f, player.getHeight() * 0.7f);
        }
    }

    private void loadAnimationsAndTextures() {

        objectAtlas = Assets.manager.get(Assets.OBJECT_ATLAS, TextureAtlas.class);
        atlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);
        buildingsBlack = Assets.manager.get(Assets.BG_CITY_BUILDINGS_2, Texture.class);
        buildings = Assets.manager.get(Assets.BG_CITY_BUILDINGS_1, Texture.class);
        buildingsGround = Assets.manager.get(Assets.BG_CITY_GROUND, Texture.class);
        skystaticFog = Assets.manager.get(Assets.BG_CITY_FOG, Texture.class);
        skystatic = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);
        buildingsBlack.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        buildings.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

        //Player animations
        kennyAnimation = new PlayerAnimation(objectAtlas);
        lennyAnimation = new PlayerAnimation(objectAtlas, true);


        createSlimeAnimations();

        //Trampoline animations 3 frames
        TextureRegion[] trampolineFrames = { objectAtlas.findRegion("trampoline1"), objectAtlas.findRegion("trampoline2"),
                                             objectAtlas.findRegion("trampoline3"), objectAtlas.findRegion("trampoline2"), objectAtlas.findRegion("trampoline1")
        };
        trampolineAnimation = new Animation(TRAMPOLINE_FRAME_DURATION, trampolineFrames);


        //SNOWMAN
        TextureRegion[] snowmanLeftFrames = new TextureRegion[3];
        for (int i = 0; i < snowmanLeftFrames.length; i++) {
            snowmanLeftFrames[i] = objectAtlas.findRegion("snowman-" + (i + 1));
        }
        snowmanLeftAnimation = new Animation(SNOWMAN_IDLE_DURATION, snowmanLeftFrames);
        snowmanLeftAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


        //SMOKE
        TextureRegion[] smokeRightFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            smokeRightFrames[i] = objectAtlas.findRegion("smoke" + (i + 1));
        }
        smokeHaltRightAnimation = new Animation(SMOKE_HALT_DURATION, smokeRightFrames);
        smokeTurnRightAnimation = new Animation(SMOKE_TURN_DURATION, smokeRightFrames);
        smokeJumpRightAnimation = new Animation(SMOKE_TURN_DURATION, smokeRightFrames);

        smokeJumpRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        smokeHaltRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        smokeTurnRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] smokeLeftFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            smokeLeftFrames[i] = new TextureRegion(smokeRightFrames[i]);
            smokeLeftFrames[i].flip(true, false);
        }
        smokeHaltLeftAnimation = new Animation(SMOKE_HALT_DURATION, smokeLeftFrames);
        smokeTurnLeftAnimation = new Animation(SMOKE_TURN_DURATION, smokeLeftFrames);
        smokeJumpLeftAnimation = new Animation(SMOKE_TURN_DURATION, smokeLeftFrames);

        smokeJumpLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        smokeHaltLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);
        smokeTurnLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);


        // BENNY (level finnish brother)
        TextureRegion[] bennyIdleFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            bennyIdleFrames[i] = objectAtlas.findRegion("benny_idle" + (i + 1));
        }

        bennyIdleAnimation = new Animation(BENNY_IDLE_DURATION, bennyIdleFrames);
        bennyIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        TextureRegion[] bennyHappyFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            bennyHappyFrames[i] = objectAtlas.findRegion("benny_happy" + (i + 1));
        }
        bennyHappyAnimation = new Animation(BENNY_HAPPY_DURATION, bennyHappyFrames);
        bennyHappyAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    private void createSlimeAnimations() {
        //Slime animations
        //Only 3 frames animations

        // Pink
        TextureRegion[] slimePinkLeftFrames = { objectAtlas.findRegion("slime_pink1"), objectAtlas.findRegion("slime_pink2"), objectAtlas.findRegion("slime_pink3") };
        TextureRegion[] slimePinkRightFrames = new TextureRegion[3];

        slimePinkLeftAnimation = new Animation(SLIME_FRAME_DURATION, slimePinkLeftFrames);
        slimePinkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        for (int i = 0; i < 3; i++) {
            slimePinkRightFrames[i] = new TextureRegion(slimePinkLeftFrames[i]);
            slimePinkRightFrames[i].flip(true, false);

        }
        slimePinkRightAnimation = new Animation(SLIME_FRAME_DURATION, slimePinkRightFrames);
        slimePinkRightAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


        //Green
        TextureRegion[] slimeGreenLeftFrames = { objectAtlas.findRegion("slime_green1"), objectAtlas.findRegion("slime_green2"), objectAtlas.findRegion("slime_green3") };
        TextureRegion[] slimeGreenRightFrames = new TextureRegion[3];

        slimeGreenLeftAnimation = new Animation(SLIME_FRAME_DURATION / GameController.GREEN_VELOCITY_MULTIPLIER * 2, slimeGreenLeftFrames);
        slimeGreenLeftAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        for (int i = 0; i < 3; i++) {
            slimeGreenRightFrames[i] = new TextureRegion(slimeGreenLeftFrames[i]);
            slimeGreenRightFrames[i].flip(true, false);

        }
        slimeGreenRightAnimation = new Animation(SLIME_FRAME_DURATION / GameController.GREEN_VELOCITY_MULTIPLIER  * 2, slimeGreenRightFrames);
        slimeGreenRightAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);



        //Red
        TextureRegion[] slimeRedLeftFrames = { objectAtlas.findRegion("slime_red1"), objectAtlas.findRegion("slime_red2"), objectAtlas.findRegion("slime_red3") };
        TextureRegion[] slimeRedRightFrames = new TextureRegion[3];

        slimeRedLeftAnimation = new Animation(SLIME_FRAME_DURATION / GameController.RED_VELOCITY_MULTIPLIER * 2, slimeRedLeftFrames);
        slimeRedLeftAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        for (int i = 0; i < 3; i++) {
            slimeRedRightFrames[i] = new TextureRegion(slimeRedLeftFrames[i]);
            slimeRedRightFrames[i].flip(true, false);

        }
        slimeRedRightAnimation = new Animation(SLIME_FRAME_DURATION / GameController.RED_VELOCITY_MULTIPLIER  * 2, slimeRedRightFrames);
        slimeRedRightAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


        //Blue
        TextureRegion[] slimeBlueLeftFrames = { objectAtlas.findRegion("slime_blue1"), objectAtlas.findRegion("slime_blue2"), objectAtlas.findRegion("slime_blue3") };
        TextureRegion[] slimeBlueRightFrames = new TextureRegion[3];

        slimeBlueLeftAnimation = new Animation(SLIME_FRAME_DURATION / GameController.BLUE_VELOCITY_MULTIPLIER* 2, slimeBlueLeftFrames);
        slimeBlueLeftAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        for (int i = 0; i < 3; i++) {
            slimeBlueRightFrames[i] = new TextureRegion(slimeBlueLeftFrames[i]);
            slimeBlueRightFrames[i].flip(true, false);

        }
        slimeBlueRightAnimation = new Animation(SLIME_FRAME_DURATION / GameController.BLUE_VELOCITY_MULTIPLIER  * 2, slimeBlueRightFrames);
        slimeBlueRightAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
    }

    private void setButtonLeft(boolean pressed) {
        if (pressed) { buttonLeft = atlas.findRegion("btn_left_pressed"); }
        else { buttonLeft = atlas.findRegion("btn_left"); }
    }

    private void setButtonRight(boolean pressed) {
        if (pressed) { buttonRight = atlas.findRegion("btn_right_pressed"); }
        else { buttonRight = atlas.findRegion("btn_right"); }
    }

    private void setButtonJump(boolean pressed) {
        if (pressed) { buttonJump = atlas.findRegion("btn_jump_pressed"); }
        else { buttonJump = atlas.findRegion("btn_jump"); }
    }

    private void setButtonSpeed(boolean pressed) {
        if (pressed) { buttonSpeed = atlas.findRegion("btn_fast_pressed"); }
        else { buttonSpeed = atlas.findRegion("btn_fast"); }
    }

    private void setButtonPause(boolean pressed) {
        if (pressed) { buttonPauseSp = buttonPausePressedSprite; }
        else { buttonPauseSp = buttonPauseSprite; }
    }

    private void setButtonToggle(boolean pressed) {
        if (pressed) {
            buttonToggle = gc.isToggleButtonPressed() ? atlas.findRegion("btn_copterCap_pressed") : atlas.findRegion("btn_doublejump");
        }
        else {
            buttonToggle = gc.isToggleButtonPressed() ?  atlas.findRegion("btn_doublejump_pressed") :  atlas.findRegion("btn_copterCap");
        }
    }
}
