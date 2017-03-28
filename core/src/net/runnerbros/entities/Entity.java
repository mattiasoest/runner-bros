package net.runnerbros.entities;

import com.badlogic.gdx.math.Rectangle;

public abstract class Entity {


	private float stateTime = 0;
	private Rectangle bounds;
	private float     width;
	private float     height;

	public Entity(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		this.bounds = new Rectangle(x, y, width, height);
	}

	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}


	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(float width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @param x, y the position to set
	 */
	public void setPos(float x, float y) {
		this.bounds.x = x;
		this.bounds.y = y;
	}
	

	/**
	 * @return the stateTime
	 */
	public float getStateTime() {
		return stateTime;
	}

	public void updateState(float delta) {
		this.stateTime += delta;
	}
	
	public void resetState(){
		this.stateTime = 0f;
	}

}
