package domain.game;

import domain.character.Player;
import domain.map.Level;
import domain.map.Room;
import domain.view.ViewMode;
import presentation.StatusLog;
import settings.GameSettings;

import java.util.Objects;

public class GameSession {
    private final Player player;
    private final GameStats stats = new GameStats();
    private Level currentLevel;
    private boolean gameOver;
    private boolean finished;

    private ViewMode viewMode = ViewMode.TOP_DOWN;
    private BalanceMode nextLevelBalanceMode = BalanceMode.NORMAL;

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
        stats.startLevel();
    }

    public GameStats getStats() {
        return stats;
    }

    public static GameSession newGame(Player player, Level firstLevel, long worldSeed) {
        GameSession session = new GameSession(player);

        session.worldSeed = worldSeed;
        session.currentLevel = firstLevel;
        session.finished = false;
        session.stats.reachLevel(firstLevel.getIndex());
        session.stats.startLevel();

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

    public void pushEvent(GameEvent e) {
        if (e == null) return;

        if (!Objects.equals(e.getType(), "moved") &&
                !Objects.equals(e.getType(), "blocked")) {
            statusLog.add(e);
        }
        statsChange(e);
    }

    private void statsChange(GameEvent e) {
        switch (e.getType()) {
            case "moved" -> stats.step();
            case "hit" -> stats.damageDealt(e.getValue());
            case "enemyHit" -> stats.damageTaken(e.getValue());
            case "enemyKilled" -> stats.enemyKilled();
            case "usedItem" -> {
                switch (e.getItemType()) {
                    case "food" -> stats.foodEaten();
                    case "elixir" -> stats.elixirDrunk();
                    case "scroll" -> stats.scrollRead();
                }
            }
            case "pickup" -> {
                if ("TREASURE".equals(e.getItemType())) stats.addTreasure(e.getValue());
            }
        }
    }

    public StatusLog getStatusLog() {
        return statusLog;
    }

    public BalanceMode getNextLevelBalanceMode() {
        return nextLevelBalanceMode;
    }

    public void setNextLevelBalanceMode(BalanceMode nextLevelBalanceMode) {
        this.nextLevelBalanceMode = nextLevelBalanceMode == null
                ? BalanceMode.NORMAL
                : nextLevelBalanceMode;
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
