package net.runnerbros.entities;

public abstract class ItemBox extends Entity {

	private boolean hit;
	private boolean animationDone;
	public enum Type{YELLOW, GREY, BROWN}
	private Type type;
	
	public ItemBox(float x, float y, float width, float height, Type type) {
		super(x, y, width, height);
		this.hit =  false;
		this.animationDone = false;
		this.type = type;
	}
	
	public abstract boolean isEmpty();

	public boolean isHit() {
		return hit;
	}

	public void hit() {
		this.hit = true;
	}
	
	public void setHit(boolean b){
		this.hit = b;
	}
	
	public Type getType(){
		return type;
	}

	public boolean isAnimationDone() {
		return animationDone;
	}

	public void setAnimationDone() {
		this.animationDone = true;
	}

}
