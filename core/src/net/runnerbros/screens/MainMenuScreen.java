package net.runnerbros.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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


        Label.LabelStyle lStyle = new Label.LabelStyle(Assets.getRockwellFont(), Color.WHITE);
        Label header = new Label("Runner Bros", lStyle);

        header.setPosition(GameController.VIRTUAL_WIDTH / 2 - header.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.7f);

        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        ImageButton.ImageButtonStyle exitStyle = new ImageButton.ImageButtonStyle();

        Skin buttonSkin = new Skin(Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class));

        playStyle.up = buttonSkin.getDrawable("btn_right");
        playStyle.down = buttonSkin.getDrawable("btn_right_pressed");
        exitStyle.up = buttonSkin.getDrawable("btn_fast");
        exitStyle.down = buttonSkin.getDrawable("btn_fast_pressed");

        ImageButton playButton = new ImageButton(playStyle);
        ImageButton exitButton = new ImageButton(exitStyle);

        playButton.setPosition(GameController.VIRTUAL_WIDTH / 2 - playButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.45f);
        exitButton.setPosition(GameController.VIRTUAL_WIDTH / 2  - exitButton.getWidth() / 2, GameController.VIRTUAL_HEIGHT * 0.25f);


        final Sound click = Assets.manager.get(Assets.SOUND_CLICK_BUTTON, Sound.class);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                click.play(0.25f);
                game.setScreen(game.getWorldScreen());
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                game.dispose();
                click.play(0.25f);
                Gdx.app.exit();
            }
        });

        stage.addActor(header);
        stage.addActor(playButton);
        stage.addActor(exitButton);
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
        Gdx.input.setInputProcessor(stage);
        if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            if (!game.actionResolver.getSignedInGPGS()) {
                game.actionResolver.loginGPGS();
            }
        }
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
