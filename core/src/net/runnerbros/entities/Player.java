package net.runnerbros.entities;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;

public class Player extends MoveableEntity {

    private final Random  random;
    private       Vector2 acceleration;

    private boolean jumpBootsOn    = false;
    private boolean canJump        = false;
    private boolean canDoubleJump  = false;
    private boolean isFlying       = false;
    private boolean isSpeedRunning = false;
    private boolean isKenny        = true;
    private State   state          = State.IDLE;
    private int     healthPoints   = 100;
    private float   haltSmokeState = 0f;
    private float   turnSmokeState = 0f;
    private float   jumpSmokeState = 0f;

    public enum State {
        IDLE, WALKING, JUMPING, FALLING, HALT, FLYING
    }

    public Player(float x, float y, float width, float height) {
		super(x, y, width, height, false);
        this.random = new Random();
		this.acceleration =  new Vector2(0,0);
	}

    public void resetPlayer() {
        setState(State.IDLE);
        resetJumpSmokeState();
        resetTurnSmokeState();
        resetHaltSmokeState();
        resetState();
        resetHp();
        getAcceleration().x = 0;
        getAcceleration().y = 0;
        getVelocity().x = 0;
        getVelocity().y = 0;
        setAlive(true);
        getBounds().x = 0;
        getBounds().y = 0;
        jumpBootsOn    = false;
    }

    public void randomizeBrother() {
        random.setSeed(System.currentTimeMillis());
        int randomInt = random.nextInt(100);

        if (randomInt < 50){
            isKenny = true;
        }
        else {
            isKenny = false;
        }
    }

    // Either Kenny or Lenny (Red or yellow brother)
    public boolean isKenny() {
        return isKenny;
    }

	public void resetHp(){
		this.healthPoints = 100;
	}
	
	public void hit(){
		if (healthPoints <= 0)
			healthPoints = 0;
		else
			this.healthPoints -= 100;
	}

    public void updateHaltSmokeState(float delta) {
        this.haltSmokeState += delta;
    }

    public void resetHaltSmokeState() {
        this.haltSmokeState = 0f;
    }

    public float getHaltSmokeState() {
        return haltSmokeState;
    }

    public void updateTurnSmokeState(float delta) {
        this.turnSmokeState += delta;
    }

    public void resetTurnSmokeState() {
        this.turnSmokeState = 0f;
    }

    public float getTurnSmokeState() {
        return turnSmokeState;
    }

    public void updateJumpSmokeState(float delta) {
        this.jumpSmokeState += delta;
    }

    public void resetJumpSmokeState() {
        this.jumpSmokeState = 0f;
    }

    public float getJumpSmokeState() {
        return this.jumpSmokeState;
    }

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}


	/**
	 * @return the canJump
	 */
	public boolean getCanJump() {
		return canJump;
	}

	/**
	 * @param jump the canJump to set
	 */
	public void setCanJump(boolean jump) {
		this.canJump = jump;
	}

	/**
	 * @return the acceleration
	 */
	public Vector2 getAcceleration() {
		return acceleration;
	}

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean isFlying) {
        this.isFlying = isFlying;
    }

    public boolean getCanDoubleJump() {
        return canDoubleJump;
    }

    public void setCanDoubleJump(boolean canDoubleJump) {
        this.canDoubleJump = canDoubleJump;
    }

    public boolean isJumpBootsOn() {
        return jumpBootsOn;
    }

    public void setJumpBootsOn(boolean jumpBootsOn) {
        this.jumpBootsOn = jumpBootsOn;
    }

    public boolean isSpeedRunning() {
        return isSpeedRunning;
    }

    public void setSpeedRunning(boolean isSpeedRunning) {
        this.isSpeedRunning = isSpeedRunning;
    }

}
