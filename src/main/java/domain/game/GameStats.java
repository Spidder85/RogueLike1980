package domain.game;

public class GameStats {
    private int treasure;
    private int enemiesKilled;
    private int foodEaten;
    private int elixirsDrunk;
    private int scrollsRead;
    private int damageDealt;    // нанесено урона
    private int damageTaken;    // получено урона
    private int steps;
    private int maxLevel;
    private int levelEnemiesKilled;
    private int levelFoodEaten;
    private int levelElixirsDrunk;
    private int levelScrollsRead;
    private int levelDamageDealt;
    private int levelDamageTaken;
    private int levelSteps;

    public void addTreasure(int value) {
        treasure += value;
    }

    public void enemyKilled() {
        enemiesKilled++;
        levelEnemiesKilled++;
    }

    public void foodEaten() {
        foodEaten++;
        levelFoodEaten++;
    }

    public void elixirDrunk() {
        elixirsDrunk++;
        levelElixirsDrunk++;
    }

    public void scrollRead() {
        scrollsRead++;
        levelScrollsRead++;
    }

    public void damageDealt(int dmg) {
        damageDealt += dmg;
        levelDamageDealt += dmg;
    }   // нанесено урона

    public void damageTaken(int dmg) {
        damageTaken += dmg;
        levelDamageTaken += dmg;
    }   // получено урона

    public void step() {
        steps++;
        levelSteps++;
    }

    public void reachLevel(int level) {
        maxLevel = Math.max(maxLevel, level);
    }

    public void startLevel() {
        levelEnemiesKilled = 0;
        levelFoodEaten = 0;
        levelElixirsDrunk = 0;
        levelScrollsRead = 0;
        levelDamageDealt = 0;
        levelDamageTaken = 0;
        levelSteps = 0;
    }

    public LevelPerformanceSnapshot currentLevelSnapshot() {
        return new LevelPerformanceSnapshot(
                levelDamageTaken,
                levelDamageDealt,
                levelEnemiesKilled,
                levelFoodEaten,
                levelElixirsDrunk,
                levelScrollsRead,
                levelSteps
        );
    }

    public void restoreLevelProgress(
            int enemiesKilled,
            int foodEaten,
            int elixirsDrunk,
            int scrollsRead,
            int damageDealt,
            int damageTaken,
            int steps
    ) {
        levelEnemiesKilled = enemiesKilled;
        levelFoodEaten = foodEaten;
        levelElixirsDrunk = elixirsDrunk;
        levelScrollsRead = scrollsRead;
        levelDamageDealt = damageDealt;
        levelDamageTaken = damageTaken;
        levelSteps = steps;
    }

    // ===== getters =====

    public int getTreasure() { return treasure; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getFoodEaten() { return foodEaten; }
    public int getElixirsDrunk() { return elixirsDrunk; }
    public int getScrollsRead() { return scrollsRead; }
    public int getDamageDealt() { return damageDealt; }
    public int getDamageTaken() { return damageTaken; }
    public int getSteps() { return steps; }
    public int getMaxLevel() { return maxLevel; }
    public int getLevelEnemiesKilled() { return levelEnemiesKilled; }
    public int getLevelFoodEaten() { return levelFoodEaten; }
    public int getLevelElixirsDrunk() { return levelElixirsDrunk; }
    public int getLevelScrollsRead() { return levelScrollsRead; }
    public int getLevelDamageDealt() { return levelDamageDealt; }
    public int getLevelDamageTaken() { return levelDamageTaken; }
    public int getLevelSteps() { return levelSteps; }
}
