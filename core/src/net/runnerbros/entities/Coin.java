package net.runnerbros.entities;

import java.util.Random;


public class Coin extends Entity {

	private final float MAX_POS_Y;
	private final float MIN_POS_Y;
	private boolean max = false;
	
	public Coin(float x, float y, float width, float height, boolean up) {
		super(x, y, width, height);
		Random n = new Random();
		n.setSeed(System.currentTimeMillis());
		MAX_POS_Y = (float) (y + 0.5f + n.nextFloat() * 0.15f);
		MIN_POS_Y = (float) (y - 0.5f - n.nextFloat() * 0.15f);
		this.max = up;
	}
	
	
	public float getMaxPosY(){
		return MAX_POS_Y;
	}

	public float getMinPosY(){
		return MIN_POS_Y;
	}
	
	public boolean getMax(){
		return max;
	}
	
	public void setMax(boolean b){
		max = b;
	}

}
