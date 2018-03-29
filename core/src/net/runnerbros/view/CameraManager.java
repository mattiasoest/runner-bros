package net.runnerbros.view;

import com.badlogic.gdx.graphics.OrthographicCamera;

import net.runnerbros.controller.GameController;

/**
 * Created by GhettoP on 2018-03-29.
 */

public class CameraManager {
    private final OrthographicCamera gameCamera;
    private final OrthographicCamera hoverCamera;

    static final float CAMERA_HOVERSPEED_MAX = 700f;
    static final float CAMERA_HOVERSPEED_MIN = 330f;
    static final float HOVER_CAMERA_SIZE_MULTIPLIER = 2f;
    static final float CAMERA_SPEED_INCREASER = 1.5f;


    public CameraManager()

    {
        this.gameCamera = new OrthographicCamera();
        this.hoverCamera = new OrthographicCamera(GameController.VIRTUAL_WIDTH * HOVER_CAMERA_SIZE_MULTIPLIER, GameController.VIRTUAL_HEIGHT * HOVER_CAMERA_SIZE_MULTIPLIER);
    }

    public OrthographicCamera getGameCamera() {
        return gameCamera;
    }

    public OrthographicCamera getHoverCamera() {
        return hoverCamera;
    }

}
