package net.runnerbros.entities;

public class BoxItem extends Entity {
	
	private final float MAX_POSY;
	
	public BoxItem(float x, float y, float width, float height, float maxPos) {
		super(x, y, width, height);
		this.MAX_POSY = maxPos;
	}
	
	
	public float getMaxPos(){
		return MAX_POSY;
	}
}
