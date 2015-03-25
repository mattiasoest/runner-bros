package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;

/**
 * Created by mattiasosth on 18/07/2014.
 */
public class MainMenuScreen implements Screen {


    private final RunnerBros game;
    private final Stage      stage;

    private final FitViewport view;
    private final Texture     background;
//    private final Music       menuMusc;

    public MainMenuScreen(final RunnerBros game) {
        this.game = game;


//        menuMusc = Assets.manager.get(Assets.MUSIC_MENU, Music.class);
//        menuMusc.setLooping(true);
//        menuMusc.setVolume(0.2f);
        //        menuMusc.play();
        background = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());


//        Label.LabelStyle lStyle = new Label.LabelStyle(Assets.getRockwellFont(), Color.WHITE);
//        Label header = new Label("Runner Bros", lStyle);
//
//        header.setPosition(GameController.VIRTUAL_WIDTH / 2 - header.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.7f);

        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle exitStyle = new ImageButton.ImageButtonStyle();

        TextureAtlas generalAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);

        Skin generalSkin = new Skin(generalAtlas);

        Image titleImage = setupHeader(generalAtlas);


        playStyle.up = generalSkin.getDrawable("btn_play_bigblue");
        playStyle.down = generalSkin.getDrawable("btn_play_bigblue_pressed");
        exitStyle.up = generalSkin.getDrawable("btn_fast");
        exitStyle.down = generalSkin.getDrawable("btn_fast_pressed");

        ImageButton playButton = new ImageButton(playStyle);
        ImageButton exitButton = new ImageButton(exitStyle);

        playButton.setPosition(GameController.VIRTUAL_WIDTH / 2 - playButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.35f);
        exitButton.setPosition(GameController.VIRTUAL_WIDTH / 2  - exitButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.15f);


        final Sound click = Assets.manager.get(Assets.SOUND_CLICK_BUTTON, Sound.class);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play(0.25f);
                game.switchScreen(stage, game.getWorldScreen());
//                game.setScreen(game.getWorldScreen());
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.dispose();
                click.play(0.25f);
                exitGame();
            }

        });

        stage.addActor(titleImage);
        stage.addActor(playButton);
        stage.addActor(exitButton);
    }

    private void exitGame() {
        Gdx.input.setInputProcessor(null);
        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(0.4f));
        sequenceAction.addAction(Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.app.exit();
            }
        }));
        stage.addAction(sequenceAction);
    }

    private Image setupHeader(TextureAtlas generalAtlas) {
        TextureRegion title = generalAtlas.findRegion("runner-bros-title");
        Image titleImage = new Image(title);
        titleImage.setOrigin(titleImage.getWidth() / 2, titleImage.getHeight() / 2f);
        titleImage.setPosition(GameController.VIRTUAL_WIDTH / 2 - titleImage.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.75f);

        ScaleToAction scaleToActionIncrease = new ScaleToAction();
        scaleToActionIncrease.setDuration(1.1f);
        scaleToActionIncrease.setScale(1.03f);


        ScaleToAction scaleToActionDecrease = new ScaleToAction();
        scaleToActionDecrease.setDuration(1.1f);
        scaleToActionDecrease.setScale(0.97f);

//        Actions.forever(Actions.sequence(scaleToActionIncrease, scaleToActionDecrease));
        titleImage.addAction(Actions.forever(Actions.sequence(scaleToActionIncrease, scaleToActionDecrease)));
        return titleImage;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().begin();
            game.getSpriteBatch().draw(background, 0 ,0 , GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        game.getSpriteBatch().end();

        stage.act(delta);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height);
    }

    @Override
    public void show() {

//        stage.getRoot().getColor().a = 0;
        Gdx.input.setInputProcessor(null);
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(game.FADE_TIME), Actions.run(new Runnable() {
            @Override
            public void run() {
                Gdx.input.setInputProcessor(stage);
            }
        })));
//        if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
//            if (!game.actionResolver.getSignedInGPGS()) {
//                game.actionResolver.loginGPGS();
//            }
//        }
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);

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
