package net.runnerbros.entities;

/**
 * Created by mattiasosth on 05/12/2014.
 */
public class Benny extends MoveableEntity {

    private boolean isHappy = false;

    public Benny(float x, float y, float width, float height, boolean facingLeft) {
        super(x, y, width, height, facingLeft);
    }

    public boolean isHappy() {
        return isHappy;
    }

    public void setHappy() {
        isHappy = true;
    }

    public void resetBenny() {
        isHappy = false;
    }
}
