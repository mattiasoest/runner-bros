package net.runnerbros.entities;

public class EnergyBall extends MoveableEntity {

	private int bounces;

	public EnergyBall(float x, float y, float width, float height,
			boolean facingLeft) {
		super(x, y, width, height, facingLeft);
		this.bounces = 5;
	}


	public int getBounces() {
		return bounces;
	}


	public void bounce() {
		bounces--;
	}
}
