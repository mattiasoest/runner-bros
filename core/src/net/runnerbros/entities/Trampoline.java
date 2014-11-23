package net.runnerbros.entities;

public class Trampoline extends Entity {

	private float force;
	private boolean hit;
	
	
	public Trampoline(float x, float y, float width, float height, float force) {
		super(x, y, width, height);
		this.force = force;
		this.hit = false;
	}
	
	public float getForce(){
		return force;
	}
	
	public boolean isHit(){
		return hit;
	}
	
	public void hit(){
		hit = true;
	}
	
	public void resetHit() {
		hit = false;
	}
}
