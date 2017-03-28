package net.runnerbros.controller;

/**
 * Created by mattiasosth on 25/08/2014.
 */
public interface ActionResolver {

    boolean getSignedInGPGS();

    void loginGPGS();

    void logoutGPGS();

    void submitScoreGPGS(long score, String leaderBoardId);

    void unlockAchievementGPGS(String achievementId);

    void getLeaderboardGPGS(String leaderBoardId);

    void getAchievementsGPGS();
}
