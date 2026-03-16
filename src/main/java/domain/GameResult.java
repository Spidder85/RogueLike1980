package domain;

import domain.game.SessionScore;

public class GameResult {
    private final int levelReached;
    private final int treasures;
    private SessionScore fullStats;

    public GameResult(int levelReached, int treasures) {
        this.levelReached = levelReached;
        this.treasures = treasures;
        this.fullStats = null;
    }

    public static GameResult fromSessionScore(SessionScore score) {
        GameResult result = new GameResult(score.getMaxLevel(), score.getTreasure());
        result.fullStats = score;
        return result;
    }

    public SessionScore getFullStats() {
        return fullStats;
    }

    public int getLevelReached() {
        return levelReached;
    }

    public int getTreasures() {
        return treasures;
    }
}
