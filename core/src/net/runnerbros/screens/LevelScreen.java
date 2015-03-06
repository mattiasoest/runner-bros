package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
 * Created by mattiasosth on 19/07/2014.
 */
public class LevelScreen implements Screen {

    private static final int THE_CITY    = 1;
    private static final int SNOW_LANDS  = 2;
    private static final int LOST_GARDEN = 3;

    //This is good after some testing
    private static final float LEVEL_WIDTH = 583f;

    private final Stage            stage;
    private final FitViewport      view;
    private final Label.LabelStyle ls;
    private final Texture          background;
    private final RunnerBros       game;
    private       int              worldIndex;

    private final Sound clickSound;

    private final boolean          debug = false;
    public LevelScreen(final RunnerBros game, final int worldIndex) {
        this.game = game;
        this.worldIndex = worldIndex;
        clickSound = Assets.manager.get(Assets.SOUND_CLICK_BUTTON, Sound.class);

        background = Assets.manager.get(Assets.BG_CITY_SUN, Texture.class);
        ls = new Label.LabelStyle();
        ls.font = Assets.getNewRockwellFont();

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());
        Table container = new Table();
        container.setPosition(0, 0);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        Table scrollContainer = new Table();
        PagedScrollPane scroll = new PagedScrollPane();

        float pageSpacing = 0f;
        scroll.setFlingTime(0.5f);
        scroll.setPageSpacing(pageSpacing);
        int levelIndex = 1;
        for (int i = 0; i < 4; i++) {
            Table levels = new Table();
            Table levelTable = new Table();
//                    levelTable.setBackground(worldBg.getDrawable());
            Button levelButton = getLevelButton(levelIndex);
            Label localHighScore = new Label("Highscore: 33.255", ls);
//                    levelButton.setSize(50, 50);
            levelTable.add(levelButton).pad(50);
            levelTable.row();
            //DEBUG DATA
            if (debug) {
                levelTable.debug();
            }

            levelTable.add(localHighScore);
            levels.add(levelTable).height(GameController.VIRTUAL_HEIGHT * 0.8f).width(LEVEL_WIDTH);
            levelIndex++;
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
                dispose();
                game.switchScreen(stage, game.getWorldScreen());
//                game.setScreen(game.getWorldScreen());
            }
        });

        scrollContainer.add(scroll);
        container.add(scrollContainer).row();
        container.add(backButton).bottom().left().padLeft(25).padBottom(25);

        stage.addActor(container);

    }

    private Button getLevelButton(int levelIndex) {

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        Skin buttonSkin = new Skin(Assets.manager.get(Assets.BUTTON_ATLAS, TextureAtlas.class));

        buttonStyle.up = buttonSkin.getDrawable("btn_right");
        buttonStyle.down = buttonSkin.getDrawable("btn_right_pressed");

        Button button = new ImageButton(buttonStyle);

        String worldKey = getLevelKey(levelIndex);

        //TODO: Use the key to get the name?
        Label label = new Label("LEVEL NAME HERE " + Integer.toString(levelIndex), ls);
        label.setAlignment(Align.center);

        button.stack(label);

        button.row();
        button.setName(worldKey);

        //TODO: Only have 3 levels atm.
        if (levelIndex < 4) {
            button.addListener(levelClickListener);
        }
        return button;

    }

    private String getLevelKey(int levelIndex) {
        return "world_"+ worldIndex + "-" + levelIndex;
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
//        stage.getRoot().getColor().a = 0;
//        stage.getRoot().addAction(Actions.fadeIn(game.FADE_TIME));
        Gdx.input.setInputProcessor(null);
        stage.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(game.FADE_TIME), Actions.run(new Runnable() {
            @Override
            public void run() {
            Gdx.input.setInputProcessor(stage);
            }
        })));
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

    private ClickListener levelClickListener = new ClickListener() {

        @Override
        public void clicked(InputEvent event, float x, float y) {
            clickSound.play(0.25f);
            String levelName = event.getListenerActor().getName();
            System.out.println("Selected: " + levelName);

            //TODO: LEVEL NAME
            game.getGameController().loadLevel(event.getListenerActor().getName(), "FUNNY STUFF");
            // May remove this.
            game.getGameController().resetCurrentGame();
            game.getRenderer().initRenderer();
            game.setScreen(game.getPlayscreen());
//            game.switchScreen(stage, game.getPlayscreen());
        }
    };
}
