package net.runnerbros.entities;

public class RandomBox extends ItemBox {

	
	public RandomBox(float x, float y, float width, float height, Type type) {
		super(x, y, width, height, type);
	}

	@Override
	public boolean isEmpty() {
		//contains only 1 HealthItem and is created when player hits the box
		return true;
	}
 
}
