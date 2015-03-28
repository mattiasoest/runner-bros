package net.runnerbros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import net.runnerbros.controller.ActionResolver;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.controller.SoundManager;
import net.runnerbros.screens.LevelScreen;
import net.runnerbros.screens.MainMenuScreen;
import net.runnerbros.screens.OptionScreen;
import net.runnerbros.screens.PlayScreen;
import net.runnerbros.screens.WorldScreen;
import net.runnerbros.view.GameRenderer;

public class RunnerBros extends Game {

    public static final float FADE_TIME_FROM = 0.4f;
    public static final float FADE_TIME      = 0.4f;

    //Access the google GPS
    public ActionResolver actionResolver;

    private GameRenderer   renderer;
    private GameController gameController;

    //Create all screens except the level screen.
    private PlayScreen     playscreen;
    private MainMenuScreen mainMenuScreen;
    private WorldScreen    worldScreen;

    //Creates during runtime
    private LevelScreen  levelScreen;
    private OptionScreen optionScreen;


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

        //		while (!Assets.manager.update()) {
        //			System.out.println(Assets.manager.getProgress() * 100 + "%");
        //		}

        Assets.manager.finishLoading();
        gameController = new GameController(this);
        renderer = new GameRenderer(gameController);
        playscreen = new PlayScreen(this);
        mainMenuScreen = new MainMenuScreen(this);
        worldScreen = new WorldScreen(this);
        optionScreen = new OptionScreen(this);

        SoundManager.INSTANCE.playMenuMusic();

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

	public void switchScreen(Stage currentStage, final Screen screen) {
		final RunnerBros bros = this;
//		currentStage.getRoot().getColor().a = 1;
		Gdx.input.setInputProcessor(null);
		SequenceAction sequenceAction = new SequenceAction();
		sequenceAction.addAction(Actions.fadeOut(FADE_TIME_FROM));
		sequenceAction.addAction(Actions.run(new Runnable() {
			@Override
			public void run() {
				bros.setScreen(screen);
			}
		}));
		currentStage.addAction(sequenceAction);
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

    public Screen getOptionScreen() {
        return optionScreen;

    }

	//Used when navigates from world screen to levelscreen
	public void setLevelScreen(LevelScreen levelScreen) {
		this.levelScreen = levelScreen;
	}

}
