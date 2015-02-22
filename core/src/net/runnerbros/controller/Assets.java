package net.runnerbros.controller;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets {

    private static BitmapFont rockwellFont = new BitmapFont(Gdx.files.internal("fonts/rockwell.fnt"), false);

    //GENERAL
    public static final String OBJECT_ATLAS = "textures/objectAtlas.pack";
    public static final String BUTTON_ATLAS = "textures/atlas.pack";

    //CITY WORLD
    public static final String BG_CITY_BUILDINGS_2 = "textures/bg_city_buildings_1024_512.png";
    public static final String BG_CITY_BUILDINGS_1 = "textures/bg_city_buildings_close_1024_512.png";
    public static final String BG_CITY_FOG         = "textures/bg_city_fog_1024_512.png";
    public static final String BG_CITY_SUN         = "textures/bg_city_1024_512.png";
    public static final String BG_CITY_GROUND      = "textures/bg_city_ground_1024_512.png";


    //MUSIC
    public static final String SOUND_COPTER_CAP = "sounds/copter-cap.wav";
    public static final String MUSIC_MENU       = "music/Running_menu.ogg";
    public static final String MUSIC_CITY_WORLD = "music/Free_running.ogg";

    //AUDIO
    public static final String SOUND_JUMP            = "sounds/jump.wav";
    public static final String SOUND_DOUBLEJUMP      = "sounds/double-jump.wav";
    public static final String SOUND_CLICK_BUTTON    = "sounds/resume-gameplay.wav";
    public static final String SOUND_PAUSE           = "sounds/pause-sound2.wav";
    public static final String SOUND_RETRY           = "sounds/retry-sound.wav";
    public static final String SOUND_TOGGLE_ABILITY  = "sounds/toggle-ability.wav";
    public static final String SOUND_SLIME_SMASH     = "sounds/slime-smash.wav";
    public static final String SOUND_DIED            = "sounds/died.wav";
    public static final String SOUND_HIT             = "sounds/hit.wav";
    public static final String SOUND_TRAMPOLINE      = "sounds/trampoline.wav";

    public static final AssetManager manager = new AssetManager();

    public static void load() {

        //GENERAL
        manager.load(OBJECT_ATLAS, TextureAtlas.class);
        manager.load(BUTTON_ATLAS, TextureAtlas.class);

        //CITY WORLD
        manager.load(BG_CITY_BUILDINGS_2, Texture.class);
        manager.load(BG_CITY_BUILDINGS_1, Texture.class);
        manager.load(BG_CITY_FOG, Texture.class);
        manager.load(BG_CITY_SUN, Texture.class);
        manager.load(BG_CITY_GROUND, Texture.class);

        //MUSIC
//        manager.load(MUSIC_MENU, Music.class);
//        manager.load(MUSIC_CITY_WORLD, Music.class);

        //Audio
        manager.load(SOUND_COPTER_CAP, Sound.class);
        manager.load(SOUND_JUMP, Sound.class);
        manager.load(SOUND_DOUBLEJUMP, Sound.class);
        manager.load(SOUND_CLICK_BUTTON, Sound.class);
        manager.load(SOUND_PAUSE, Sound.class);
        manager.load(SOUND_RETRY, Sound.class);
        manager.load(SOUND_TOGGLE_ABILITY, Sound.class);
        manager.load(SOUND_SLIME_SMASH, Sound.class);
        manager.load(SOUND_DIED, Sound.class);
        manager.load(SOUND_HIT, Sound.class);
        manager.load(SOUND_TRAMPOLINE, Sound.class);
    }

    public static void dispose() {
        manager.dispose();
        rockwellFont.dispose();
    }

    public static BitmapFont getRockwellFont() {
        return rockwellFont;
    }

    //If this is used, DISPOSE MANUALLY!!
    public static BitmapFont getNewRockwellFont() {
        return new BitmapFont(Gdx.files.internal("fonts/rockwell.fnt"), false);
    }
}
