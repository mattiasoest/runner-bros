package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.utils.Base64Coder;
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

    private final ImageButton musicToggleButton;
    private final ImageButton soundToggleButton;
    private final ImageButton backButton;

    public OptionScreen(final RunnerBros game) {
        super(game);

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());

        final ImageButton.ImageButtonStyle musicStyleOn = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle musicStyleOff = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOnStyle = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle soundOffStyle = new ImageButton.ImageButtonStyle();
        final ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();

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

        ImageButton.ImageButtonStyle musicStyle = SoundManager.INSTANCE.isMusicEnabled() ? musicStyleOn : musicStyleOff;
        ImageButton.ImageButtonStyle soundStyle = SoundManager.INSTANCE.isSoundEnabled() ? soundOnStyle : soundOffStyle;
        musicToggleButton = new ImageButton(musicStyle);
        soundToggleButton = new ImageButton(soundStyle);
        backButton = new ImageButton(backStyle);

        addButtonListeners(musicStyleOn, musicStyleOff, soundOnStyle, soundOffStyle);

        Table container = new Table();
        container.setPosition(0, 0);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        Table buttonTable = new Table();

        buttonTable.add(soundToggleButton).pad(25);
        buttonTable.add(musicToggleButton).pad(25);

        container.add(settingsHeader).pad(25).row().expand();
        container.add(buttonTable).pad(25).row();
        container.add(backButton).align(Align.bottomLeft).pad(25);

        stage.addActor(container);
    }

    private void addButtonListeners(ImageButton.ImageButtonStyle musicStyleOn, ImageButton.ImageButtonStyle musicStyleOff, ImageButton.ImageButtonStyle soundOnStyle, ImageButton.ImageButtonStyle soundOffStyle) {
        musicToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
                if (SoundManager.INSTANCE.isMusicEnabled()) {
                    SoundManager.INSTANCE.setMusicEnabled(false);
                    musicToggleButton.setStyle(musicStyleOff);
                    SoundManager.INSTANCE.pauseMenuMusic();
                }
                else {
                    SoundManager.INSTANCE.setMusicEnabled(true);
                    musicToggleButton.setStyle(musicStyleOn);
                    SoundManager.INSTANCE.swtichToMenuMusic();
                }
            }
        });

        soundToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // We want to play the click sound of this button no
                // matter what, because it felt weird to not hear the sound when
                // toggling back to sound mode.
                SoundManager.INSTANCE.playButtonClick(true);
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
                Preferences pref = game.getGameController().getPreferences();
                pref.putString(Base64Coder.encodeString(SoundManager.INSTANCE.SOUND_KEY),
                        Base64Coder.encodeString(SoundManager.INSTANCE.isSoundEnabled() ? "1" : "0"));
                pref.putString(Base64Coder.encodeString(SoundManager.INSTANCE.MUSIC_KEY),
                        Base64Coder.encodeString(SoundManager.INSTANCE.isMusicEnabled() ? "1" : "0"));
                pref.flush();

                game.switchScreen(stage, game.getMainMenuScreen());
            }
        });

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
        stage.dispose();

    }
}
