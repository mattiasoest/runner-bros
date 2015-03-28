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
        ImageButton.ImageButtonStyle musicStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle soundStyle = new ImageButton.ImageButtonStyle();

        TextureAtlas generalAtlas = Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class);

        Skin generalSkin = new Skin(generalAtlas);

        musicStyle.up = generalSkin.getDrawable("btn_musicOn");
        musicStyle.down = generalSkin.getDrawable("btn_musicOn_pressed");
        soundStyle.up = generalSkin.getDrawable("btn_soundOn");
        soundStyle.down = generalSkin.getDrawable("btn_soundOn_pressed");

        ImageButton musicToggleButton = new ImageButton(musicStyle);
        ImageButton soundToggleButton = new ImageButton(soundStyle);

//        musicToggleButton.setPosition(GameController.VIRTUAL_WIDTH / 2 - musicToggleButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.35f);
//        soundToggleButton.setPosition(GameController.VIRTUAL_WIDTH / 2  - soundToggleButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.15f);


        musicToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();

            }
        });

        soundToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.INSTANCE.playButtonClick();
            }

        });

        stage.addActor(musicToggleButton);
        stage.addActor(soundToggleButton);
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
