package net.runnerbros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

    private static final int THE_CITY    = 1;
    private static final int SNOW_LANDS  = 2;
    private static final int LOST_GARDEN = 3;

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
            case 1:
                amountOfLevels = 8;
                break;
            case 2:
                amountOfLevels = 5;
                break;
            case 3:
                amountOfLevels = 5;
                break;
                default:
                    throw  new RuntimeException("Unknown world: " + worldIndex);

        }
        // TODO: ADJUST WHEN WE KNOW THE NUMBER OF LEVELS FOR EACH WORLD
        for (int levelIndex = 1; levelIndex <= amountOfLevels; levelIndex++) {
            Table levels = new Table();
            Table levelTable = new Table();
//                    levelTable.setBackground(worldBg.getDrawable());
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
//                    levelButton.setSize(50, 50);
            levelTable.add(levelButton).pad(50);
            levelTable.row();
            //DEBUG DATA
            if (debug) {
                levelTable.debug();
            }

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

        //TODO: Only have 7 (7 test)? levels atm.
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
//        Table.drawDebug(stage);

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
            String levelName = event.getListenerActor().getName();
            System.out.println("Selected: " + levelName);

            //TODO: LEVEL NAME
            game.getGameController().loadLevel(event.getListenerActor().getName(), "FUNNY STUFF");
            // May remove this.
            game.getGameController().resetCurrentGame();
            game.getRenderer().initRenderer();
            game.getGameController().initMap();
            game.setScreen(game.getPlayscreen());
//            game.switchScreen(stage, game.getPlayscreen());
        }
    };
}
