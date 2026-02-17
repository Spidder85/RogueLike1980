package domain.game;

import domain.character.Player;
import domain.map.Level;
import domain.map.Room;
import domain.view.ViewMode;
import presentation.StatusLog;
import settings.GameSettings;

import java.util.Random;

public class GameSession {
    private final Player player;
    private final GameStats stats = new GameStats();
    private Level currentLevel;
    private boolean gameOver;
    private boolean finished;

    private ViewMode viewMode = ViewMode.TOP_DOWN;

    private long worldSeed; // Seed для генератора случайных чисел

    private final StatusLog statusLog = new StatusLog(GameSettings.STATUS_LOG_SIZE);

    public GameSession(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
    public Level getCurrentLevel() {
        return currentLevel;
    }
    public void setCurrentLevel(Level level) {
        this.currentLevel = level;
        stats.reachLevel(level.getIndex());
    }

    public GameStats getStats() {
        return stats;
    }

    public static GameSession newGame(Player player, Level firstLevel, long worldSeed) {
        GameSession session = new GameSession(player);

        session.worldSeed = worldSeed;
        session.currentLevel = firstLevel;
        session.finished = false;

        Room startRoom = firstLevel.startRoom;
        player.setPosition(
                startRoom.getRandomPoint().x,
                startRoom.getRandomPoint().y
        );

        return session;
    }

    public void finishGame() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean isGameOver() {
        return !player.isAlive();
    }

//    public void reset() {
//        finished = false;
//        currentLevel = null;
//    }

    public void pushEvent(GameEvent e) {
        if (e != null)
            statusLog.add(e);
    }

    public StatusLog getStatusLog() {
        return statusLog;
    }

    public long getWorldSeed() {
        return worldSeed;
    }

    public void setWorldSeed(long worldSeed) {
        this.worldSeed = worldSeed;
    }

    // Задание 9: First Person view (полное 3D)
    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    public boolean isTopDown() {
        return viewMode == ViewMode.TOP_DOWN;
    }

    public boolean isFirstPerson() {
        return viewMode == ViewMode.FIRST_PERSON;
    }

    public void toggleViewMode() {
        viewMode = isTopDown() ? ViewMode.FIRST_PERSON : ViewMode.TOP_DOWN;
    }
}
