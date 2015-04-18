package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;

/**
 * Created by mattiasosth on 18/04/2015.
 */
public class BackgroundScreen implements Screen {

    private final Texture    background;
    private final Texture    bg_city;
    private final Texture    bg_city_black;
    protected final RunnerBros game;

    public BackgroundScreen(final RunnerBros game) {

        this.game = game;
        background = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);
        bg_city = Assets.manager.get(Assets.BG_CITY_BUILDINGS_1, Texture.class);
        bg_city_black = Assets.manager.get(Assets.BG_CITY_BUILDINGS_2, Texture.class);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(background, 0, 0, GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        game.getSpriteBatch().draw(bg_city_black, 30, -10 , GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        game.getSpriteBatch().draw(bg_city, 0, -30, GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        game.getSpriteBatch().end();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
