package domain.game;

import domain.character.Character;
import domain.map.Level;
import presentation.StatusLog;
import settings.GameSettings;

public class GameSession {
    private final Character player;
    private final GameStats stats = new GameStats();
    private Level currentLevel;
    private boolean gameOver;
    private boolean finished;

    private final StatusLog statusLog = new StatusLog(GameSettings.STATUS_LOG_SIZE);

    public GameSession(Character player) {
        this.player = player;
    }

    public Character getPlayer() {
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

    public void finishGame() { finished = true; }
    public boolean isFinished() { return finished; }

    public boolean isGameOver() {
        return !player.isAlive();
    }

    public void reset() {
        finished = false;
        currentLevel = null;
    }

    public void pushEvent(GameEvent e) {
        if (e != null)
            statusLog.add(e);
    }

    public StatusLog getStatusLog() {
        return statusLog;
    }
}
