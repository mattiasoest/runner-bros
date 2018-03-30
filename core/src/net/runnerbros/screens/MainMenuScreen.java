package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.controller.SoundManager;

/**
 * Created by mattiasosth on 18/07/2014.
 */
public class MainMenuScreen extends BackgroundScreen {

    private final Stage      stage;

    private final FitViewport view;

    public MainMenuScreen(final RunnerBros game) {
        super(game);
        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());

        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle exitStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle settingsStyle = new ImageButton.ImageButtonStyle();

        TextureAtlas generalAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);

        Skin generalSkin = new Skin(generalAtlas);

        Image titleImage = setupHeader(generalAtlas);


        playStyle.up = generalSkin.getDrawable("btn_play_bigblue");
        playStyle.down = generalSkin.getDrawable("btn_play_bigblue_pressed");
        exitStyle.up = generalSkin.getDrawable("btn_left");
        exitStyle.down = generalSkin.getDrawable("btn_left_pressed");
        settingsStyle.up = generalSkin.getDrawable("btn_settings");
        settingsStyle.down = generalSkin.getDrawable("btn_settings_pressed");

        ImageButton playButton = new ImageButton(playStyle);
        ImageButton exitButton = new ImageButton(exitStyle);
        ImageButton settingsButton = new ImageButton(settingsStyle);

        Table container =  new Table();
        container.setPosition(0, 0f);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        container.add(titleImage).pad(25).row().expand();
        container.add(playButton).row();


        Table bottomTable = new Table();

        bottomTable.setPosition(0, 0f);
        bottomTable.add(exitButton).expandX().align(Align.left);

        bottomTable.add(settingsButton);

        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                game.switchScreen(stage, game.getWorldScreen());
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                exitGame();
            }

        });


        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                game.switchScreen(stage, game.getOptionScreen());
            }
        });

        container.add(bottomTable).bottom().pad(25).fill();
        stage.addActor(container);
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
        super.render(delta);
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
