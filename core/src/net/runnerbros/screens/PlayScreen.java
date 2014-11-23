package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import net.runnerbros.RunnerBros;

public class PlayScreen implements Screen {


	private final RunnerBros game;

	public PlayScreen(RunnerBros game) {
		this.game = game;
	}

	@Override
	public void render(float delta) {
		game.getGameController().update(delta);
		game.getRenderer().render(delta);
	}

	@Override
	public void resize(int width, int height) {
		game.getRenderer().resize(width, height);

	}

	@Override
	public void show() {
        System.out.println("SHOW");
        game.getGameController().startTimer();
        Gdx.input.setInputProcessor(game.getGameController());
	}

	@Override
	public void hide() {
        System.out.println("HIDE");
        Gdx.input.setInputProcessor(null);

	}

	@Override
	public void pause() {
        System.out.println("PAUSE");
        game.getGameController().pauseGame(true);
	}

	@Override
	public void resume() {
        //Let the user unpause himself...
	}

	@Override
	public void dispose() {
	}

}
