package domain;

public class GameResult {
    private final int levelReached;
    private final int treasures;

    public GameResult(int levelReached, int treasures) {
        this.levelReached = levelReached;
        this.treasures = treasures;
    }

    public int getLevelReached() {
        return levelReached;
    }

    public int getTreasures() {
        return treasures;
    }
}
