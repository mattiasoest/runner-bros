package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.runnerbros.RunnerBros;
import net.runnerbros.controller.Assets;
import net.runnerbros.controller.GameController;
import net.runnerbros.controller.PagedScrollPane;
import net.runnerbros.controller.SoundManager;

/**
 * Created by mattiasosth on 19/07/2014.
 */
public class LevelScreen extends BackgroundScreen {

    private static final int THE_CITY  = 1;
    private static final int SNOW_CITY = 2;
    private static final int CITY_PARK = 3;

    //This is good after some testing
    private static final float LEVEL_WIDTH = 583f;

    private final Stage            stage;
    private final FitViewport      view;
    private final Label.LabelStyle ls;
    private       int              worldIndex;

    private final boolean          debug = false;
    public LevelScreen(final RunnerBros game, final int worldIndex) {
        super(game);
        this.worldIndex = worldIndex;
        ls = new Label.LabelStyle();
        ls.font = Assets.getNewRockwellFont();

        view = new FitViewport(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);
        this.stage = new Stage(view, game.getSpriteBatch());
        Table container = new Table();
        container.setPosition(0, 0);
        container.setSize(GameController.VIRTUAL_WIDTH, GameController.VIRTUAL_HEIGHT);

        Table scrollContainer = new Table();
        PagedScrollPane scroll = new PagedScrollPane();

        Preferences pref = game.getGameController().getMapScores();
        float pageSpacing = 0f;
        scroll.setFlingTime(0.5f);
        scroll.setPageSpacing(pageSpacing);

        final int  amountOfLevels;
        switch (worldIndex) {
            case THE_CITY:
                amountOfLevels = game.getGameController().THE_CITY_NO_LVLS;
                break;
            case SNOW_CITY:
                amountOfLevels = game.getGameController().SNOW_CITY_NO_LVLS;
                break;
            case CITY_PARK:
                amountOfLevels = game.getGameController().CITY_PARK_NO_LVLS;
                break;
            default:
                    throw  new RuntimeException("Unknown world: " + worldIndex);

        }

        for (int levelIndex = 1; levelIndex <= amountOfLevels; levelIndex++) {
            Table levels = new Table();
            Table levelTable = new Table();
            Button levelButton = getLevelButton(levelIndex);

            Label localHighScore;
            String currentLevel = getLevelKey(levelIndex);
            if (pref.contains(Base64Coder.encodeString(currentLevel))) {
                float time = Float.valueOf(Base64Coder.decodeString(pref.getString(Base64Coder.encodeString(currentLevel))));
                String formattedTime = game.getGameController().getDecimalFormat().format(time);
                localHighScore = new Label("Time: " + formattedTime, ls);
            }
            else {
                localHighScore = new Label("Time: N/A", ls);
            }
            //DEBUG DATA
            if (debug) {
                levelTable.debug();
            }

            levelTable.setBackground(new TextureRegionDrawable(
                    new TextureRegion(Assets.manager.get(Assets.BG_CITY_FOG, Texture.class))));

//            levelTable.setColor(0,0,0, 0.5f);
            Label label = new Label(getLevelName(Integer.toString(levelIndex)), ls);
            label.setAlignment(Align.center);
            levelTable.add(label);
            levelTable.row();
            levelTable.add(levelButton).pad(10);
            levelTable.row();
            levelTable.add(localHighScore);
            levels.add(levelTable).height(GameController.VIRTUAL_HEIGHT * 0.8f).width(LEVEL_WIDTH);
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
                SoundManager.INSTANCE.playButtonClick();
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

        buttonStyle.up = buttonSkin.getDrawable("btn_play_biggreen");
        buttonStyle.down = buttonSkin.getDrawable("btn_play_bigblue_pressed");

        Button button = new ImageButton(buttonStyle);

        String worldKey = getLevelKey(levelIndex);

        button.setName(worldKey);

        if (levelIndex <= 8) {
            button.addListener(levelClickListener);
        }
        return button;

    }

    private String getLevelKey(int levelIndex) {
        return "world_"+ worldIndex + "-" + levelIndex;
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
            SoundManager.INSTANCE.playButtonClick();

            String levelKey = event.getListenerActor().getName();
            System.out.println("Selected: " + levelKey);
            // Example world_1-1

            String levelNumber = levelKey.split("-")[1];
            String levelName = getLevelName(levelNumber);


            game.getGameController().loadLevel(event.getListenerActor().getName(), levelName);
            game.getGameController().resetCurrentGame();
            game.getRenderer().initRenderer();
            game.getGameController().initMap();
            game.setScreen(game.getPlayscreen());
//            game.switchScreen(stage, game.getPlayscreen());
        }
    };

    private String getLevelName(String levelNumber) {

        String levelName = "";
        switch (worldIndex) {
            case THE_CITY:
                levelName = "The City " + levelNumber;
                break;
            case SNOW_CITY:
                levelName = "Snow City " + levelNumber;
                break;
            case CITY_PARK:
                levelName = "City Park" + levelNumber;
                break;
            default:
                throw new RuntimeException("Unknown world: " + worldIndex);

        }
        return levelName;
    }
}
