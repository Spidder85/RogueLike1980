package domain.game;

public class GameStats {
    private int treasure;
    private int enemiesKilled;
    private int foodEaten;
    private int elixirsDrunk;
    private int scrollsRead;
    private int damageDealt;
    private int damageTaken;
    private int steps;
    private int maxLevel;

    public void addTreasure(int value) {
        treasure += value;
    }

    public void enemyKilled() {
        enemiesKilled++;
    }

    public void foodEaten() {
        foodEaten++;
    }

    public void elixirDrunk() {
        elixirsDrunk++;
    }

    public void scrollRead() {
        scrollsRead++;
    }

    public void damageDealt(int dmg) {
        damageDealt += dmg;
    }

    public void damageTaken(int dmg) {
        damageTaken += dmg;
    }

    public void step() {
        steps++;
    }

    public void reachLevel(int level) {
        maxLevel = Math.max(maxLevel, level);
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
}
