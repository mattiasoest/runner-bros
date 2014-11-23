package net.runnerbros.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class MoveableEntity extends Entity {

    private boolean facingLeft;
    private Vector2 velocity;
    private int     previousTilePositionX;
    private int     previousTilePositionY;
    private boolean isAlive;
    private float   mass;

    public MoveableEntity(float x, float y, float width, float height, boolean facingLeft) {
        this(x, y, width, height, facingLeft, 1);
	}

    public MoveableEntity(float x, float y, float width, float height, boolean facingLeft , float mass) {
        super(x, y, width, height);
        this.facingLeft = facingLeft;
        this.setVelocity(new Vector2(0, 0));
        this.setAlive(true);
        this.mass = mass;
    }
	
	public boolean isFacingLeft() {
		return facingLeft;
	}

	public void setFacingLeft(boolean facingLeft) {
		this.facingLeft = facingLeft;
	}
	

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public int getPreviousTilePositionX() {
        return previousTilePositionX;
    }

    public void setPreviousTilePositionX(int previousTilePositionX) {
        this.previousTilePositionX = previousTilePositionX;
    }

    public int getPreviousTilePositionY() {
        return previousTilePositionY;
    }

    public void setPreviousTilePositionY(int previousTilePositionY) {
        this.previousTilePositionY = previousTilePositionY;
    }
}
