package net.runnerbros.entities;

public class Slime extends MoveableEntity {



	public static enum Type {
		GREY, RED, BLUE, YELLOW, PINK,
	}
	
	private final Type type;
	
	public Slime(float x, float y, float width, float height, Type type) {
		super(x, y, width, height, false);
		this.type =  type;
	}
	
	public Type getType(){
		return type;
	}

	public Slime copy() {
		return new Slime(getBounds().x, getBounds().y, width, height, type);
	}
}
