package net.runnerbros.controller;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by mattiasosth, 28/03/2015.
 */
public class SoundManager {

    private Music menuMusc;
    private Music gameMusic;
    private Sound click;
    private boolean isSoundEnabled = true;
    private boolean isMusicEnabled = true;

    public static final String MUSIC_KEY = "music";
    public static final String SOUND_KEY = "sound";

    private static final float GAME_MUSIC_PAUSE = 5f;
    private static final float GAME_MUSIC_VOLUME = 0.2f;
    private float gameMusicPauseTimer = 0f;

    public static final SoundManager INSTANCE = getInstance();
    private Sound jumpSound;
    private Sound doubleJump;
    private Sound retrySound;
    private Sound toggleAbility;
    private Sound copterCap;
    private Sound slimeSmash;
    private Sound died;
    private Sound hit;
    private Sound trampoline;
    private Sound pauseSound;

    private static SoundManager getInstance() {
        return INSTANCE != null ? INSTANCE : new SoundManager();
    }

    private SoundManager() {
        loadMusic();
        loadAudio();

    }

    private void loadMusic() {
        menuMusc = Assets.manager.get(Assets.MUSIC_MENU, Music.class);
        menuMusc.setLooping(true);
        menuMusc.setVolume(0.15f);

        gameMusic = Assets.manager.get(Assets.MUSIC_CITY_WORLD, Music.class);
        gameMusic.setLooping(false);
        gameMusic.setVolume(GAME_MUSIC_VOLUME);

    }

    private void loadAudio() {
        //        this.cityWorldMusic  = Assets.manager.get(Assets.MUSIC_CITY_WORLD, Music.class);

        this.jumpSound       = Assets.manager.get(Assets.SOUND_JUMP, Sound.class);

        this.pauseSound      = Assets.manager.get(Assets.SOUND_PAUSE, Sound.class);
        this.doubleJump      = Assets.manager.get(Assets.SOUND_DOUBLEJUMP, Sound.class);
        this.retrySound      = Assets.manager.get(Assets.SOUND_RETRY, Sound.class);
        this.toggleAbility   = Assets.manager.get(Assets.SOUND_TOGGLE_ABILITY, Sound.class);

        this.copterCap       = Assets.manager.get(Assets.SOUND_COPTER_CAP, Sound.class);

        this.slimeSmash      = Assets.manager.get(Assets.SOUND_SLIME_SMASH, Sound.class);
        this.died            = Assets.manager.get(Assets.SOUND_DIED, Sound.class);
        this.hit             = Assets.manager.get(Assets.SOUND_HIT, Sound.class);
        this.trampoline      = Assets.manager.get(Assets.SOUND_TRAMPOLINE, Sound.class);

        this.click = Assets.manager.get(Assets.SOUND_CLICK_BUTTON, Sound.class);
    }

    public void manageGameMusic(float delta) {

        // We're in the ready state for the first time, let it play until the user
        // goes into RUNNING state.
        if (menuMusc.isPlaying()) {
            return;
        }
        if (gameMusic.isPlaying()) {
            //Just return since we want to fade after 21.5f seconds.
            if (gameMusic.getPosition() < 21.5f) {
                // Do nothing
                return;
            }
            else {
                float volume = gameMusic.getVolume() - 0.105f * delta;
                gameMusic.setVolume(volume);
            }
        }
        else {
            if (gameMusicPauseTimer < GAME_MUSIC_PAUSE) {
                gameMusicPauseTimer += delta;
            }
            else {
                switchToGameMusic();
            }
        }
    }


    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public void setSoundEnabled(boolean isSoundEnabled) {
        this.isSoundEnabled = isSoundEnabled;
    }

    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }

    public void setMusicEnabled(boolean isMusicEnabled) {
        this.isMusicEnabled = isMusicEnabled;
    }

    public void swtichToMenuMusic() {
        if (isMusicEnabled) {
            gameMusic.stop();
            menuMusc.play();
        }
    }

    public void playMenuMusic() {
        menuMusc.play();
    }

    public void playGameMusic() {
        if (isMusicEnabled) {
            if (menuMusc.isPlaying()) {
                menuMusc.pause();
            }
            gameMusic.play();
        }
    }

    public void switchToGameMusic() {
        if (isMusicEnabled) {
            if (menuMusc.isPlaying()) {
                menuMusc.pause();
            }
            if (!gameMusic.isPlaying()) {
                //Reset timer and volume
                gameMusicPauseTimer = 0;
                gameMusic.setVolume(GAME_MUSIC_VOLUME);
                gameMusic.play();
            }
        }
    }

    public void playButtonClick() {
        if (isSoundEnabled) {
            click.play(0.3f);
        }
    }

    public void playButtonClick(boolean forcePlay) {
            click.play(0.3f);
    }

    public void playSlimeSmash() {
        if (isSoundEnabled) {
            slimeSmash.play(0.45f);
        }
    }

    public void playPlayedDied() {
        if (isSoundEnabled) {
            died.play(0.3f);
        }
    }

    public void playJump() {
        if (isSoundEnabled) {
            jumpSound.play(0.2f);
        }
    }

    public void playPaused() {
        if (isSoundEnabled) {
            pauseSound.play(0.3f);
        }
    }

    public void playDoubleJumpp() {
        if (isSoundEnabled) {
            doubleJump.play(0.3f);
        }
    }

    public void playToggleAbility() {
        if (isSoundEnabled) {
            toggleAbility.play(0.35f);
        }
    }

    public void playCopterCap() {
        if (isSoundEnabled) {
            copterCap.play(0.17f);
        }
    }

    public void playTrampoline() {
        if (isSoundEnabled) {
            trampoline.play(0.85f);
        }
    }

    public void pauseMenuMusic() {
        menuMusc.pause();

    }
    public void pauseGameMusic() {
        gameMusic.pause();
    }
}
