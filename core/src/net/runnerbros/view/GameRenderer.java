package net.runnerbros.view;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import net.runnerbros.entities.Player;
import net.runnerbros.entities.Slime;
import net.runnerbros.entities.Trampoline;

public class GameRenderer {
    private final FitViewport   view;
    private final DecimalFormat decimalFormat;
    private final Sprite        buttonPauseSprite;
    private final Sprite        buttonPausePressedSprite;


    private Level currentLevel;

    private Sprite buttonPauseSp;

    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera         camera;
    private float                      buttonDiam;
    private boolean isSmokeTurnPlaying = false;
    private boolean isSmokeHaltPlaying = false;
    private boolean isSmokeJumpPlaying = false;
    private boolean isHaltLeft         = false;
    private boolean isRunningLeft      = false;


    private final float SLIME_FRAME_DURATION      = 0.25f;
    private final float TRAMPOLINE_FRAME_DURATION = 0.09f;
    private final float SMOKE_HALT_DURATION       = 0.12f;
    private final float SMOKE_TURN_DURATION       = 0.1f;

    private TextureAtlas objectAtlas;
    private TextureAtlas atlas;

    private Player player;

    private SpriteBatch batch;


    private PlayerAnimation kennyAnimation;
    private PlayerAnimation lennyAnimation;
    private PlayerAnimation currentPlayerAnimation;

    private int[] layers = new int[]{ 0, 1 };
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
    private Texture        coin;
    private TextureRegion  playerFrame;

    private TextureRegion smokeHaltFrame;
    private TextureRegion smokeTurnFrame;
    private TextureRegion smokeJumpLeftFrame;
    private TextureRegion smokeJumpRightFrame;

    private Animation smokeHaltRightAnimation;
    private Animation smokeTurnRightAnimation;
    private Animation smokeHaltLeftAnimation;
    private Animation smokeTurnLeftAnimation;
    private Animation smokeJumpLeftAnimation;
    private Animation smokeJumpRightAnimation;


    private TextureRegion slimeFrame;
    private TextureRegion trampFrame;

    private Animation slimeBlueRightAnimation;
    private Animation slimeBlueLeftAnimation;
    private Animation slimeGreyLeftAnimation;
    private Animation slimeGreyRightAnimation;
    private Animation slimeRedLeftAnimation;
    private Animation slimeRedRightAnimation;
    private Animation slimeYellowLeftAnimation;
    private Animation slimeYellowRightAnimation;
    private Animation trampolineAnimation;

    private TextureRegion buttonLeft;
    private TextureRegion buttonRight;
    private TextureRegion buttonJump;
    private TextureRegion buttonSpeed;
    private TextureRegion buttonToggle;
    private TextureRegion buttonPause;

    private BitmapFont    font;

    //Debugging
    private ShapeRenderer sr;

    public GameRenderer(GameController gc) {
        sr = new ShapeRenderer();

        //Use this one everywhere to save resources!
        this.batch = new SpriteBatch();

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');

        decimalFormat = new DecimalFormat("#.###", otherSymbols);
        decimalFormat.setMinimumFractionDigits(3);

        this.gc = gc;
        this.font = Assets.getRockwellFont();
        //        this.font.scale(1.2f);
        this.font.setColor(Color.WHITE);
        this.font.setUseIntegerPositions(false);
        //        this.scale = Gdx.graphics.getWidth() / 768f;

        this.camera = new OrthographicCamera();
        //        camera.viewportWidth = 384f;
        //        camera.viewportHeight = 216f;
        //        camera.viewportWidth = 768f;
        //        camera.viewportHeight = 432f;
        //        camera.viewportWidth = Gdx.graphics.getWidth();
        //        camera.viewportHeight = Gdx.graphics.getHeight();
        this.buttonDiam = GameController.VIRTUAL_WIDTH / 10f;
        camera.update();
        Gdx.graphics.setVSync(false);
        this.player = gc.getPlayer();
        batch.enableBlending();
        loadAnimationsAndTextures();

        setButtonSpeed(false);
        setButtonJump(false);
        setButtonLeft(false);
        //        buttonWidth = buttonLeft.getRegionWidth() * (GameController.VIRTUAL_WIDTH / 875f);
        //        buttonHeight = buttonLeft.getRegionHeight() * (GameController.VIRTUAL_WIDTH / 875f);
        setButtonRight(false);

        //TODO: Use other graphics later
        TextureRegion tempPause = atlas.findRegion("btn_left_pressed");
        TextureRegion tempPause2 = atlas.findRegion("btn_left");
//        tempPause.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//        tempPause2.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        buttonPausePressedSprite = new Sprite(tempPause);
        buttonPauseSprite = new Sprite(tempPause2);
        buttonPausePressedSprite.setScale(0.55f);
        buttonPauseSprite.setScale(0.55f);
        setButtonPause(false);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT, camera);
        gc.setupPauseMenu(view, batch);
    }

    public void initRenderer() {
        this.currentLevel = gc.getCurrentLevel();
        this.renderer = new OrthogonalTiledMapRenderer(currentLevel.getMap(), batch);
    }

    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        drawParallaxBackground();
        renderer.setView(camera);
        //TODO: Remove layers and just user render() ??
        renderer.render(layers);
        batch.begin();
        drawSlimes();
        drawTampolines(delta);
        drawPlayer();
        drawTimer();
        //        drawGameStats();
        if (!gc.isGamePaused()) {
            drawSmoke(delta);
            drawButtons();
        }
        batch.end();
        if (gc.isGamePaused()) {
            gc.getPausedStage().act(delta);
            gc.getPausedStage().draw();
            //DRAW PAUSE MENU
        }
        updateCameraPosition();
    }

    public Batch getSpriteBatch() {
        return batch;
    }

    private void drawTimer() {
        float time = gc.getTimer();
        font.draw(batch, decimalFormat.format(time), camera.position.x - camera.viewportWidth / 2.25f, camera.position.y + camera.viewportHeight * 0.475f);
    }


    public void dispose() {
        if (currentLevel != null) {
            currentLevel.getMap().dispose();
        }
        batch.dispose();
        gc.getPausedStage().dispose();
        sr.dispose();
        //Assets dispoe this
//        font.dispose();
    }

    public void resize(int width, int height) {
        //        float aspectRatio = (float) width / (float) height;
        //        float scale = 1f;
        //        Vector2 crop = new Vector2(0f, 0f);
        //
        //        if (aspectRatio > ASPECT_RATIO) {
        //            scale = (float) height / (float) VIRTUAL_HEIGHT;
        //            crop.x = (width - VIRTUAL_WIDTH * scale) / 2f;
        //        }
        //        else if (aspectRatio < ASPECT_RATIO) {
        //            scale = (float) width / (float) VIRTUAL_WIDTH;
        //            crop.y = (height - VIRTUAL_HEIGHT * scale) / 2f;
        //        }
        //        else {
        //            scale = (float) width / (float) VIRTUAL_WIDTH;
        //        }
        //
        //        float w = (float) VIRTUAL_WIDTH * scale;
        //        float h = (float) VIRTUAL_HEIGHT * scale;
        //        viewport = new Rectangle(crop.x, crop.y, w, h);
        view.update(width, height);
    }

    private void drawParallaxBackground() {
        //paralaxx
        batch.begin();
        batch.draw(skystatic, camera.position.x - camera.viewportWidth / 2f, camera.position.y - camera.viewportHeight / 2f, camera.viewportWidth, camera.viewportHeight, 0, 1, 1, 0);

        batch.draw(buildingsGround, camera.position.x - camera.viewportWidth / 2f, camera.position.y / 1.4f - camera.viewportHeight / 3.5f, camera.viewportWidth, camera.viewportHeight, 0, 1, 1, 0);
        batch.draw(buildingsBlack, camera.position.x / 1.05f - camera.viewportWidth, camera.position.y / 1.1f - camera.viewportHeight / 2.4f, buildingsBlack.getWidth() * 4, buildingsBlack.getHeight() * 1, 0, 1, 4, 0);
        batch.draw(buildings, camera.position.x / 1.3f - camera.viewportWidth, camera.position.y / 1.4f - camera.viewportHeight / 3f, buildings.getWidth() * 4, buildings.getHeight(), 0, 1, 4, 0);

        batch.draw(skystaticFog, camera.position.x - camera.viewportWidth / 2f, camera.position.y - camera.viewportHeight / 2f, camera.viewportWidth, camera.viewportHeight, 0, 1, 1, 0);
        batch.end();
    }

    private void drawButtons() {
        setButtonLeft(gc.isLeftPressed());
        setButtonRight(gc.isRightPressed());
        setButtonJump(gc.isJumpPressed());
        setButtonSpeed(gc.isFirePressed());
        setButtonPause(gc.isPausePressed());
        setButtonToggle(gc.getPlayer().isJumpBootsOn());
        batch.draw(buttonLeft, camera.position.x - camera.viewportWidth * 0.46f, camera.position.y - camera.viewportHeight * 0.485f);
        batch.draw(buttonRight, camera.position.x - camera.viewportWidth * 0.3f, camera.position.y - camera.viewportHeight * 0.485f);
        batch.draw(buttonSpeed, camera.position.x + camera.viewportWidth * 0.31f - buttonSpeed.getRegionWidth(), camera.position.y - camera.viewportHeight * 0.485f);
        batch.draw(buttonJump, camera.position.x + camera.viewportWidth * 0.47f - buttonJump.getRegionWidth(), camera.position.y - camera.viewportHeight * 0.485f);
        batch.draw(buttonToggle, camera.position.x + camera.viewportWidth * 0.47f - buttonToggle.getRegionWidth(), camera.position.y - camera.viewportHeight * 0.26f);
//        batch.draw(buttonPauseSp, camera.position.x + camera.viewportWidth * 0.47f - buttonToggle.getRegionWidth(), camera.position.y + camera.viewportHeight * 0.26f);
        buttonPauseSp.setPosition(camera.position.x + camera.viewportWidth * 0.47f - buttonToggle.getRegionWidth(), camera.position.y + camera.viewportHeight * 0.22f);
        buttonPauseSp.draw(batch);

    }

    private void updateCameraPosition() {
//        float posX, posY;
//        posX = player.getBounds().x - player.getWidth() / 2;
//        posY = player.getBounds().y - camera.viewportHeight * 0.03f; // + player.getHeight() / 2 + camera.viewportHeight / 12f
//        camera.position.set(posX, posY, 0);
//        if (camera.position.x - camera.viewportWidth / 2 < tmt.getTileWidth() * 1) {
//            posX = tmt.getTileWidth() + camera.viewportWidth / 2;
//        }
//        else if (camera.position.x + camera.viewportWidth / 2 > (tmt.getWidth() - 1) * tmt.getTileWidth()) {
//            posX = tmt.getTileWidth() * (tmt.getWidth() - 1) - camera.viewportWidth / 2;
//            //		paraBG.render(delta, camera.position.x / 5, camera.position.y /2);
//        }
//        if (camera.position.y + camera.viewportHeight / 2 < tmt.getTileHeight() * 25) {
//            posY = tmt.getTileHeight() * 25 - camera.viewportHeight / 2;
//        }
//        camera.position.set(posX, posY, 0);
//        camera.update();
        float posX, posY;
        posX = player.getBounds().x - player.getWidth() / 2;
        posY = player.getBounds().y - camera.viewportHeight * 0.1f; // + player.getHeight() / 2 + camera.viewportHeight / 12f
//        posY = player.getBounds().y - camera.viewportHeight * 0.03f; // + player.getHeight() / 2 + camera.viewportHeight / 12f
        camera.position.set(posX, posY, 0);
        if (camera.position.x - camera.viewportWidth / 2 < currentLevel.getTileWidth() * 1) {
            posX = currentLevel.getTileWidth() + camera.viewportWidth / 2;
        }
        else if (camera.position.x + camera.viewportWidth / 2 > (currentLevel.getCollisionLayer().getWidth() - 1) * currentLevel.getTileWidth()) {
            posX = currentLevel.getTileWidth() * (currentLevel.getCollisionLayer().getWidth() - 1) - camera.viewportWidth / 2;
            //		paraBG.render(delta, camera.position.x / 5, camera.position.y /2);
        }
        if (camera.position.y + camera.viewportHeight / 2 < currentLevel.getTileHeight() * 14.4f) {
            posY = currentLevel.getTileHeight() * 14.4f - camera.viewportHeight / 2;
        }
        camera.position.set(posX, posY, 0);
        camera.update();
    }

    private void drawTampolines(float delta) {
        for (Trampoline t : gc.getTrampolines()) {
            if (!t.isHit()) {
                trampFrame = objectAtlas.findRegion("trampoline1");
            }
            else {
                trampolineAnimation.setPlayMode(Animation.PlayMode.NORMAL);
                trampFrame = trampolineAnimation.getKeyFrame(t.getStateTime(), false);
                if (trampolineAnimation.isAnimationFinished(t.getStateTime())) {
                    t.resetHit();
                    t.resetState();
                }
                t.updateState(delta);
            }
            batch.draw(trampFrame, t.getBounds().x, t.getBounds().y, trampFrame.getRegionWidth(), trampFrame.getRegionHeight());
        }
    }

    private void drawSlimes() {
        for (Slime s : gc.getSlimes()) {
            if (s.getType().equals(Slime.Type.BLUE)) {
                if (s.isAlive()) {
                    slimeFrame = s.isFacingLeft() ? slimeBlueLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeBlueRightAnimation.getKeyFrame(s.getStateTime(), true);
                }
                else { slimeFrame = objectAtlas.findRegion("slime-blue-smashed"); }
            }
            else if (s.getType().equals(Slime.Type.GREY)) {
                if (s.isAlive()) {
                    slimeFrame = s.isFacingLeft() ? slimeGreyLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeGreyRightAnimation.getKeyFrame(s.getStateTime(), true);
                }
                else { slimeFrame = objectAtlas.findRegion("slime-grey-smashed"); }
            }
            else if (s.getType().equals(Slime.Type.RED)) {
                if (s.isAlive()) {
                    slimeFrame = s.isFacingLeft() ? slimeRedLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeRedRightAnimation.getKeyFrame(s.getStateTime(), true);
                }
                else { slimeFrame = objectAtlas.findRegion("slime-red-smashed"); }
            }
            else if (s.getType().equals(Slime.Type.YELLOW)) {
                if (s.isAlive()) {
                    slimeFrame = s.isFacingLeft() ? slimeYellowLeftAnimation.getKeyFrame(s.getStateTime(), true) : slimeYellowRightAnimation.getKeyFrame(s.getStateTime(), true);
                }
                else { slimeFrame = objectAtlas.findRegion("slime-yellow-smashed"); }
            }
            batch.draw(slimeFrame, s.getBounds().x, s.getBounds().y, s.getWidth(), s.getHeight());
        }
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
        //Some adjustements since the flying frames are 80p in height instead of 64p
        if (player.getState().equals(Player.State.FLYING)) {
            height = 80f;
            yPos -= 3f;

        }
        batch.draw(playerFrame, player.getBounds().x - 25, yPos, player.getWidth() + 50, height);
    }

    public void drawSmoke(float delta) {
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
                //TODO: FIXA SÅ DET BLIR BRA
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
                //TODO: FIXA SÅ DET BLIR BRA
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


        //Slime animations
        //Only 2 frames animations
        TextureRegion[] slimeBlueLeftFrames = { objectAtlas.findRegion("slime-blue1"), objectAtlas.findRegion("slime-blue2") };
        TextureRegion[] slimeBlueRightFrames = new TextureRegion[2];

        TextureRegion[] slimeGreyLeftFrames = { objectAtlas.findRegion("slime-grey1"), objectAtlas.findRegion("slime-grey2") };
        TextureRegion[] slimeGreyRightFrames = new TextureRegion[2];

        TextureRegion[] slimeRedLeftFrames = { objectAtlas.findRegion("slime-red1"), objectAtlas.findRegion("slime-red2") };
        TextureRegion[] slimeRedRightFrames = new TextureRegion[2];

        TextureRegion[] slimeYellowLeftFrames = { objectAtlas.findRegion("slime-yellow1"), objectAtlas.findRegion("slime-yellow2") };
        TextureRegion[] slimeYellowRightFrames = new TextureRegion[2];

        slimeGreyLeftAnimation = new Animation(SLIME_FRAME_DURATION, slimeGreyLeftFrames);
        slimeBlueLeftAnimation = new Animation(SLIME_FRAME_DURATION, slimeBlueLeftFrames);
        slimeRedLeftAnimation = new Animation(SLIME_FRAME_DURATION, slimeRedLeftFrames);
        slimeYellowLeftAnimation = new Animation(SLIME_FRAME_DURATION, slimeYellowLeftFrames);
        for (int i = 0; i < 2; i++) {
            slimeBlueRightFrames[i] = new TextureRegion(slimeBlueLeftFrames[i]);
            slimeBlueRightFrames[i].flip(true, false);

            slimeGreyRightFrames[i] = new TextureRegion(slimeGreyLeftFrames[i]);
            slimeGreyRightFrames[i].flip(true, false);

            slimeRedRightFrames[i] = new TextureRegion(slimeRedLeftFrames[i]);
            slimeRedRightFrames[i].flip(true, false);

            slimeYellowRightFrames[i] = new TextureRegion(slimeYellowLeftFrames[i]);
            slimeYellowRightFrames[i].flip(true, false);
        }
        slimeGreyRightAnimation = new Animation(SLIME_FRAME_DURATION, slimeGreyRightFrames);
        slimeBlueRightAnimation = new Animation(SLIME_FRAME_DURATION, slimeBlueRightFrames);
        slimeRedRightAnimation = new Animation(SLIME_FRAME_DURATION, slimeRedRightFrames);
        slimeYellowRightAnimation = new Animation(SLIME_FRAME_DURATION, slimeYellowRightFrames);


        //Trampoline animations 3 frames
        TextureRegion[] trampolineFrames = { objectAtlas.findRegion("trampoline1"), objectAtlas.findRegion("trampoline2"),
                                             objectAtlas.findRegion("trampoline3"), objectAtlas.findRegion("trampoline2"), objectAtlas.findRegion("trampoline1")
        };
        trampolineAnimation = new Animation(TRAMPOLINE_FRAME_DURATION, trampolineFrames);


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
