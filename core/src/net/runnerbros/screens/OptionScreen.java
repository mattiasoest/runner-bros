package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
public class OptionScreen extends BackgroundScreen {

    private final Stage      stage;

    private final FitViewport view;

    public OptionScreen(final RunnerBros game) {
        super(game);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());

        final ImageButton.ImageButtonStyle musicStyleOn = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle musicStyleOff = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOnStyle = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOffStyle = new ImageButton.ImageButtonStyle();



        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();

        TextureAtlas generalAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);

        Skin generalSkin = new Skin(generalAtlas);

        TextureRegion title = generalAtlas.findRegion("settings_title");
        Image settingsHeader = new Image(title);


        musicStyleOn.up = generalSkin.getDrawable("btn_musicOn");
        musicStyleOn.down = generalSkin.getDrawable("btn_musicOn_pressed");

        musicStyleOff.up = generalSkin.getDrawable("btn_musicOff");
        musicStyleOff.down = generalSkin.getDrawable("btn_musicOn_pressed");

        soundOnStyle.up = generalSkin.getDrawable("btn_soundOn");
        soundOnStyle.down = generalSkin.getDrawable("btn_soundOn_pressed");

        soundOffStyle.up = generalSkin.getDrawable("btn_soundOff2");
        soundOffStyle.down = generalSkin.getDrawable("btn_soundOn_pressed");
        backStyle.up = generalSkin.getDrawable("btn_left");
        backStyle.down = generalSkin.getDrawable("btn_left_pressed");

        final ImageButton musicToggleButton = new ImageButton(musicStyleOn);
        final ImageButton soundToggleButton = new ImageButton(soundOnStyle);
        ImageButton backButton = new ImageButton(backStyle);

        Table container = new Table();
        container.setPosition(0, 0);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        Table buttonTable = new Table();

        buttonTable.add(soundToggleButton).pad(25);
        buttonTable.add(musicToggleButton).pad(25);

        musicToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                if (SoundManager.INSTANCE.isMusicEnabled()) {
                    SoundManager.INSTANCE.setMusicEnabled(false);
                    musicToggleButton.setStyle(musicStyleOff);
                    SoundManager.INSTANCE.pauseMusic();
                }
                else {
                    SoundManager.INSTANCE.setMusicEnabled(true);
                    musicToggleButton.setStyle(musicStyleOn);
                    SoundManager.INSTANCE.playMenuMusic();
                }
            }
        });

        soundToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                if (SoundManager.INSTANCE.isSoundEnabled()) {
                    SoundManager.INSTANCE.setSoundEnabled(false);
                    soundToggleButton.setStyle(soundOffStyle);
                }
                else {
                    SoundManager.INSTANCE.setSoundEnabled(true);
                    soundToggleButton.setStyle(soundOnStyle);
                }
            }
        });


        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                //                game.setScreen(game.getMainMenuScreen());
                game.switchScreen(stage, game.getMainMenuScreen());
            }
        });

        container.add(settingsHeader).pad(25).row().expand();
        container.add(buttonTable).pad(25).row();
        container.add(backButton).align(Align.bottomLeft).pad(25);

        stage.addActor(container);
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
