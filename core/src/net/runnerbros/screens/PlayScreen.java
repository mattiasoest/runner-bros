package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.GameController;

public class PlayScreen implements Screen {


	private final RunnerBros  game;
	private final FitViewport view;
	private final Stage       stage;

	public PlayScreen(RunnerBros game) {
		this.game = game;
		view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
		this.stage = new Stage(view, game.getSpriteBatch());
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
		stage.getRoot().getColor().a = 0;
		stage.getRoot().addAction(Actions.fadeIn(game.FADE_TIME));
        game.getGameController().startTimer();
        Gdx.input.setInputProcessor(game.getGameController());
	}

	@Override
	public void hide() {
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
