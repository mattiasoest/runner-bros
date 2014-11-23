package net.runnerbros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;

import net.runnerbros.controller.ActionResolver;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.screens.LevelScreen;
import net.runnerbros.screens.MainMenuScreen;
import net.runnerbros.screens.PlayScreen;
import net.runnerbros.screens.WorldScreen;
import net.runnerbros.view.GameRenderer;

public class RunnerBros extends Game {


	//Access the google GPS
	public ActionResolver actionResolver;

	private GameRenderer   renderer;
	private GameController gameController;

	//Create all screens except the level screen.
	private PlayScreen     playscreen;
	private MainMenuScreen mainMenuScreen;
	private WorldScreen    worldScreen;

	//Creates during runtime
	private LevelScreen levelScreen;


	public RunnerBros() {
	}

	public RunnerBros(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}


	@Override
	public void create() {

		//TODO: FIX LEADERBOARD CODE
		//        if (game.actionResolver.getSignedInGPGS()) {
		//            game.actionResolver.getLeaderboardGPGS("CgkIj7LCjKMNEAIQAQ");
		//        }
		//        else {
		//            game.actionResolver.loginGPGS();
		//        }


		Assets.load();

		while (!Assets.manager.update()) {
			System.out.println(Assets.manager.getProgress() * 100 + "%");
		}

		//        Assets.manager.finishLoading();
		gameController = new GameController(this);
		renderer = new GameRenderer(gameController);
		playscreen = new PlayScreen(this);
		mainMenuScreen = new MainMenuScreen(this);
		worldScreen = new WorldScreen(this);

		setScreen(mainMenuScreen);
	}

	@Override
	public void dispose() {
		//These screens uses own instance of font.
		worldScreen.dispose();

		Assets.dispose();
		renderer.dispose();
		System.exit(0);
	}

	public GameRenderer getRenderer() {
		return renderer;
	}

	public GameController getGameController() {
		return gameController;
	}

	//Usage of only ONE sprite batch in the whole project since its resource heavy
	public Batch getSpriteBatch() {
		return renderer.getSpriteBatch();
	}

	public PlayScreen getPlayscreen() {
		return playscreen;
	}

	public MainMenuScreen getMainMenuScreen() {
		return mainMenuScreen;
	}

	public WorldScreen getWorldScreen() {
		return worldScreen;
	}

	public LevelScreen getLevelScreen() {
		return levelScreen;
	}

	//Used when navigates from world screen to levelscreen
	public void setLevelScreen(LevelScreen levelScreen) {
		this.levelScreen = levelScreen;
	}
}
