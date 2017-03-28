package net.runnerbros.entities;

public class Slime extends MoveableEntity {

	private final float slimeVelocityMultiplier;

	public static enum Type {
		RED,
		BLUE,
//		YELLOW,
		PINK,
		GREEN
	}
	
	private final Type type;
	
	public Slime(float x, float y, float width, float height, float slimeVelocityMultiplier, Type type) {
		super(x, y, width, height, false);
		this.slimeVelocityMultiplier = slimeVelocityMultiplier;
		this.type =  type;
	}
	
	public Type getType(){
		return type;
	}

	public Slime copy() {
		return new Slime(getBounds().x, getBounds().y, getWidth(), getHeight(), slimeVelocityMultiplier, type);
	}

	public float getSlimeVelocityMultiplier() {
		return slimeVelocityMultiplier;
	}
}
