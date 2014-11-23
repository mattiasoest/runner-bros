package net.runnerbros.entities;

import com.badlogic.gdx.Gdx;

public class CoinBox extends ItemBox {

	int coins;
	float[] coinsPos;
	private final float MAXPOSY;
	private boolean done = false;
	
	public CoinBox(float x, float y, float width, float height, Type type, int coinAmount) {
		super(x, y, width, height, type);
		this.coinsPos = new float[coinAmount];
		for(int i = 0; i< coinsPos.length; i++){
			this.coinsPos[i] = y;
		}
		this.MAXPOSY = y + height*2.5f;
		this.coins = coinAmount;
	}
	
	
	
	@Override
	public void hit(){
		super.setHit(true);
		if(coins > 0)
			coins--;
	}

	@Override
	public boolean isEmpty() {
		return coins == 0;
	}
	
	public void done(){
		done = true;
	}
	
	public boolean isDone(){
		return done;
	}
	
	public float[] getCoinsY(){
		return coinsPos;
	}
	
	public float getMaxCoinPosY(){
		return MAXPOSY;
	}
	
	public void updateCoinPos(){
		for(int i = 0; i<coinsPos.length - coins; i++){
			if(coinsPos[i] < MAXPOSY)
				coinsPos[i] += 170 * Gdx.graphics.getDeltaTime();
			else
				coinsPos[i] = 800f; //outside map
		}
	}
}
