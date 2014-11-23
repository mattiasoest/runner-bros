package net.runnerbros.controller;

/**
 * Created by mattiasosth on 25/08/2014.
 */
public interface ActionResolver {

    public boolean getSignedInGPGS();

    public void loginGPGS();

    public void logoutGPGS();

    public void submitScoreGPGS(long score, String leaderBoardId);

    public void unlockAchievementGPGS(String achievementId);

    public void getLeaderboardGPGS(String leaderBoardId);

    public void getAchievementsGPGS();
}
