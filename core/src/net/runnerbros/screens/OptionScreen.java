package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
public class OptionScreen implements Screen {


    private final RunnerBros game;
    private final Stage      stage;

    private final FitViewport view;
    private final Texture     background;

    public OptionScreen(final RunnerBros game) {
        this.game = game;

        background = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());

        final ImageButton.ImageButtonStyle musicStyleOn = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle musicStyleOff = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOnStyle = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOffStyle = new ImageButton.ImageButtonStyle();

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();

        TextureAtlas generalAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);

        Skin generalSkin = new Skin(generalAtlas);

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

        Table buttonTable = new Table();

        buttonTable.setPosition(0, 0);
        buttonTable.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        buttonTable.add(soundToggleButton).pad(25);
        buttonTable.add(musicToggleButton).pad(25);
        buttonTable.row();
        buttonTable.add(backButton).align(Align.bottomLeft).pad(25);

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

        stage.addActor(buttonTable);
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
