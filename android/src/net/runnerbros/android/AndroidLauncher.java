package net.runnerbros.android;


import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import net.runnerbros.RunnerBros;
import net.runnerbros.controller.ActionResolver;


public class AndroidLauncher extends AndroidApplication implements GameHelper.GameHelperListener, ActionResolver {

	public GameHelper gameHelper;

	@Override
	public void onStart() {
		super.onStart();
//		gameHelper.onStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
//		gameHelper.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
//		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();

		RelativeLayout layout = new RelativeLayout(this);

		//What initalize would have done instead...
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);


		//create the libgdx view
		View gameView = initializeForView(new RunnerBros(this), cfg);


		//create the ad view
		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-9102826422054679/3269846946");
		adView.setBackgroundColor(Color.TRANSPARENT);
		layout.addView(gameView);


		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice("36662575C1CD369A") // Mate 10
				.build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);


		RelativeLayout.LayoutParams adParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
												RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

		layout.addView(adView, adParams);

		setContentView(layout);
//		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
//		gameHelper.enableDebugLog(true);
//		gameHelper.setup(this);
//
//		if (!gameHelper.isSignedIn()) {
//			System.out.println("TRYING TO LOGIN!!!!!!");
//			loginGPGS();
//		}
	}

	@Override
	public void onSignInFailed() {
		System.out.println("Sign in failed.");
	}

	@Override
	public void onSignInSucceeded() {
		System.out.println("Sign in succeeded.");
	}

	@Override
	public boolean getSignedInGPGS() {
		return gameHelper.isSignedIn();
	}

	@Override
	public void loginGPGS() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		}
		catch (final Exception ex) {
		}
	}

	@Override
	public void logoutGPGS() {
		gameHelper.signOut();
	}

	@Override
	public void submitScoreGPGS(long score, String leaderBoardId) {
		Games.Leaderboards.submitScore(gameHelper.getApiClient(), leaderBoardId, score);
	}

	@Override
	public void unlockAchievementGPGS(String achievementId) {

	}

	@Override
	public void getLeaderboardGPGS(String leaderBoardId) {
		if (gameHelper.isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(
							gameHelper.getApiClient(),
							leaderBoardId), 100);
		}
		else if (!gameHelper.isConnecting()) {
			loginGPGS();
		}
	}




	@Override
	public void getAchievementsGPGS() {

	}
}