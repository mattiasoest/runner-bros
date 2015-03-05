package net.runnerbros.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.controller.PagedScrollPane;

/**
 * Created by mattiasosth on 20/07/2014.
 */
public class WorldScreen implements Screen {


    //This is good after some testing
    private static final float LEVEL_WIDTH  = 583f;
    private static final int   WORLD_NUMBER = 3;

    private final Stage            stage;
    private final FitViewport      view;
    private final Table            scrollContainer;
    private final Label.LabelStyle ls;
    private final Texture          background;
    private final Sprite           world;
    private final RunnerBros            game;
    private final Sound            clickSound;

    private final boolean          debug = false;

    public WorldScreen(final RunnerBros game) {
        this.game = game;

        clickSound = Assets.manager.get(Assets.SOUND_CLICK_BUTTON, Sound.class);

        background = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);
        world = new Sprite(Assets.manager.get(Assets.BG_CITY_BUILDINGS_2, Texture.class));
        ls = new Label.LabelStyle();
        ls.font = Assets.getNewRockwellFont();

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());

        Table container = new Table();
        container.setPosition(0, 0);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        scrollContainer = new Table();
        PagedScrollPane scroll = new PagedScrollPane();

        float pageSpacing = 0f;

        scroll.setFlingTime(0.5f);
        scroll.setPageSpacing(pageSpacing);

        int levelIndex = 1;
        int first = 0;
        int last = 2;

        for (int l = first; l <= last; l++) {
            Table levels = new Table();
            //            levels.debug();

            //            levels.defaults().pad(15, 30, 15, 30);
            //            levels.defaults().pad(20, 30, 20, 30);
            for (int y = 0; y < 1; y++) {
                //                levels.row();
                for (int x = 0; x < 1; x++) {
                    Table levelTable = new Table();
                    //                    levelTable.setBackground(worldBg.getDrawable());
//                    Image coin = new Image(Assets.manager.get(Assets.COIN, Texture.class));
                    Button levelButton = getLevelButton(levelIndex);
                    Label localHighScore = new Label("Worldinfo here", ls);
                    //                    levelButton.setSize(50, 50);
                    levelTable.add(levelButton).pad(50);
                    levelTable.row();
//                    levelTable.add(coin).row();

                    //DEBUG DATA
                    if (debug) {
                        levelTable.debug();
                    }

                    levelTable.add(localHighScore);
                    levels.add(levelTable).height(GameController.VIRTUAL_HEIGHT * 0.8f).width(LEVEL_WIDTH);
                    //                    levels.add(getLevelButton(c++)).expand().fill();
                    levelIndex++;
                }
            }
            final float standardPad = 110;
            levels.padLeft(standardPad);
            levels.padRight(standardPad);
            scroll.addPage(levels);
        }

        ImageButton.ImageButtonStyle backStyle = new ImageButton.ImageButtonStyle();

        Skin buttonSkin = new Skin(Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class));

        backStyle.up = buttonSkin.getDrawable("btn_left");
        backStyle.down = buttonSkin.getDrawable("btn_left_pressed");

        ImageButton backButton = new ImageButton(backStyle);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play(0.25f);
//                game.setScreen(game.getMainMenuScreen());
                game.switchScreen(stage, game.getMainMenuScreen());
            }
        });

        scrollContainer.add(scroll);
        container.add(scrollContainer).row();
        container.add(backButton).bottom().left().padLeft(25).padBottom(25);

        stage.addActor(container);

    }

    private Button getLevelButton(int i) {

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();


        Skin buttonSkin = new Skin(Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class));

        buttonStyle.up = buttonSkin.getDrawable("btn_right");
        buttonStyle.down = buttonSkin.getDrawable("btn_right_pressed");
        Button button = new ImageButton(buttonStyle);
        Label label = new Label("World - " + Integer.toString(i), ls);
        label.setAlignment(Align.center);

        button.stack(label);

        button.row();
        //World index
        button.setName(Integer.toString(i));
        if (i <= WORLD_NUMBER) {
            button.addListener(levelClickListener);
        }
        return button;

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(background, 0, 0, GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        game.getSpriteBatch().end();

        stage.act(delta);
        stage.draw();
//        Table.drawDebug(stage);

    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height);

    }

    @Override
    public void show() {
        System.out.println("SHOW WORLD");
        stage.getRoot().getColor().a = 0;
        stage.getRoot().addAction(Actions.fadeIn(game.FADE_TIME));
        Gdx.input.setInputProcessor(stage);
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
        ls.font.dispose();
    }

    public ClickListener levelClickListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            clickSound.play(0.25f);
            String worldIndex = event.getListenerActor().getName();
            System.out.println("Clicked world number: " + worldIndex);
            //TODO: Fix worlds
            if (Integer.valueOf(worldIndex) > 2) {
                if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
                    if (game.actionResolver.getSignedInGPGS()) {
                        game.actionResolver.getLeaderboardGPGS("CgkIj7LCjKMNEAIQAQ");
                    }
                    else {
                        game.actionResolver.loginGPGS();
                    }
                }
                return;
            }

            LevelScreen levelSelect = new LevelScreen(game, Integer.valueOf(worldIndex));
            game.setLevelScreen(levelSelect);
            game.switchScreen(stage, levelSelect);
//            game.setScreen(levelSelect);
        }
    };
}
