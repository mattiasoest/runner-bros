package net.runnerbros.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by mattiasosth on 09/11/2014.
 */
public class PlayerAnimation {

    private final float PLAYER_FRAME_DURATION   = 0.07f;
    private final float SPEEDRUN_FRAME_DURATION = 0.05f;
    private final float JUMP_FALL_DURATION      = 0.1f;
    private final float HALT_DURATION           = 0.2f;
    private final float IDLE_FRAME_DURATION     = 0.6f;

    private Animation flyRightAnimation;
    private Animation flyLeftAnimation;
    private Animation idleRightAnimation;
    private Animation idleLeftAnimation;
    private Animation haltLeftAnimation;
    private Animation haltRightAnimation;
    private Animation walkLeftAnimation;
    private Animation runLeftAnimation;
    private Animation walkRightAnimation;
    private Animation runRightAnimation;
    private Animation jumpRightAnimation;
    private Animation jumpLeftAnimation;
    private Animation fallLeftAnimation;
    private Animation fallRightAnimation;


    public PlayerAnimation(TextureAtlas objectAtlas) {
        this(objectAtlas, false);
    }

    public PlayerAnimation(TextureAtlas objectAtlas, boolean loadLenny) {

        final String playerName = loadLenny ? "lenny_" : "";

        //IDLE
        TextureRegion[] idleRightFrames = new TextureRegion[4];
        for (int i = 0; i < 2; i++) {
            idleRightFrames[i] = objectAtlas.findRegion(playerName + "idle_breath_blink" + (i + 1));
        }
        idleRightFrames[2] = objectAtlas.findRegion(playerName + "idle_breath_blink1");
        //        idleRightFrames[3] = objectAtlas.findRegion("idle_breath_blink2");
        idleRightFrames[3] = objectAtlas.findRegion(playerName + "idle_breath_blink3");
        idleRightAnimation = new Animation(IDLE_FRAME_DURATION, idleRightFrames);
        idleRightAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        TextureRegion[] idleLeftFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            idleLeftFrames[i] = new TextureRegion(idleRightFrames[i]);
            idleLeftFrames[i].flip(true, false);
        }
        idleLeftAnimation = new Animation(IDLE_FRAME_DURATION, idleLeftFrames);

        //WALK
        TextureRegion[] walkRightFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            walkRightFrames[i] = objectAtlas.findRegion(playerName + "shoes" + (i + 1));
        }
        walkRightAnimation = new Animation(PLAYER_FRAME_DURATION, walkRightFrames);
        runRightAnimation = new Animation(SPEEDRUN_FRAME_DURATION, walkRightFrames);

        TextureRegion[] walkLeftFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
            walkLeftFrames[i].flip(true, false);
        }
        walkLeftAnimation = new Animation(PLAYER_FRAME_DURATION, walkLeftFrames);
        runLeftAnimation = new Animation(SPEEDRUN_FRAME_DURATION, walkLeftFrames);


        //FALL
        TextureRegion[] fallRightFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            fallRightFrames[i] = objectAtlas.findRegion(playerName + "falling" + (i + 1));
        }
        fallRightAnimation = new Animation(JUMP_FALL_DURATION, fallRightFrames);

        TextureRegion[] fallLeftFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            fallLeftFrames[i] = new TextureRegion(fallRightFrames[i]);
            fallLeftFrames[i].flip(true, false);
        }
        fallLeftAnimation = new Animation(JUMP_FALL_DURATION, fallLeftFrames);


        //FLY
        TextureRegion[] flyRightFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            flyRightFrames[i] = objectAtlas.findRegion(playerName + "coptercap" + (i + 1));
        }
        flyRightAnimation = new Animation(JUMP_FALL_DURATION, flyRightFrames);

        TextureRegion[] flyLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            flyLeftFrames[i] = new TextureRegion(flyRightFrames[i]);
            flyLeftFrames[i].flip(true, false);
        }
        flyLeftAnimation = new Animation(JUMP_FALL_DURATION, flyLeftFrames);

        //HALT
        TextureRegion[] haltRightFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            haltRightFrames[i] = objectAtlas.findRegion(playerName + "halt" + (i + 1));
        }
        haltRightAnimation = new Animation(HALT_DURATION, haltRightFrames);

        TextureRegion[] haltLeftFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            haltLeftFrames[i] = new TextureRegion(haltRightFrames[i]);
            haltLeftFrames[i].flip(true, false);
        }
        haltLeftAnimation = new Animation(HALT_DURATION, haltLeftFrames);

        //JUMP
        TextureRegion[] jumpRightFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            jumpRightFrames[i] = objectAtlas.findRegion(playerName + "jumping" + (i + 1));
        }
        jumpRightAnimation = new Animation(JUMP_FALL_DURATION, jumpRightFrames);

        TextureRegion[] jumpLeftFrames = new TextureRegion[2];
        for (int i = 0; i < 2; i++) {
            jumpLeftFrames[i] = new TextureRegion(jumpRightFrames[i]);
            jumpLeftFrames[i].flip(true, false);
        }
        jumpLeftAnimation = new Animation(JUMP_FALL_DURATION, jumpLeftFrames);
    }

    public Animation getFlyRightAnimation() {
        return flyRightAnimation;
    }

    public Animation getFlyLeftAnimation() {
        return flyLeftAnimation;
    }

    public Animation getIdleRightAnimation() {
        return idleRightAnimation;
    }

    public Animation getIdleLeftAnimation() {
        return idleLeftAnimation;
    }

    public Animation getHaltLeftAnimation() {
        return haltLeftAnimation;
    }

    public Animation getHaltRightAnimation() {
        return haltRightAnimation;
    }

    public Animation getWalkLeftAnimation() {
        return walkLeftAnimation;
    }

    public Animation getRunLeftAnimation() {
        return runLeftAnimation;
    }

    public Animation getWalkRightAnimation() {
        return walkRightAnimation;
    }

    public Animation getRunRightAnimation() {
        return runRightAnimation;
    }

    public Animation getJumpRightAnimation() {
        return jumpRightAnimation;
    }

    public Animation getJumpLeftAnimation() {
        return jumpLeftAnimation;
    }

    public Animation getFallLeftAnimation() {
        return fallLeftAnimation;
    }

    public Animation getFallRightAnimation() {
        return fallRightAnimation;
    }
}
