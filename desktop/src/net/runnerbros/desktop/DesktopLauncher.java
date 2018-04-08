package net.runnerbros.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.runnerbros.RunnerBros;

public class DesktopLauncher {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Runner Bros";
		cfg.width = 1024;
		cfg.height = 512;
		new LwjglApplication(new RunnerBros(), cfg);
	}
}
