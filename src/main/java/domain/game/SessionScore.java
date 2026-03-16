package domain.game;

import domain.character.Player;
import domain.item.ItemType;

import java.util.Date;

public class SessionScore {
    private int treasure;
    private int enemiesKilled;
    private int damageDealt;    // нанесено урона
    private int damageTaken;    // получено урона
    private int steps;
    private int maxLevel;
    private int foodPicked;
    private int elixirsPicked;
    private int scrollsPicked;
    private String saveDate;

    public static SessionScore fromGameSession(GameStats stats, Player player) {
        var score = new SessionScore();
        score.treasure = player.getBackpack().getTreasureAmount();
        score.enemiesKilled = stats.getEnemiesKilled();
        score.damageDealt = stats.getDamageDealt();
        score.damageTaken = stats.getDamageTaken();
        score.steps = stats.getSteps();
        score.maxLevel = stats.getMaxLevel();
        score.foodPicked = stats.getFoodEaten() + player.getBackpack().getItems(ItemType.FOOD).size();
        score.elixirsPicked = stats.getElixirsDrunk() + player.getBackpack().getItems(ItemType.ELIXIR).size();
        score.scrollsPicked = stats.getScrollsRead() + player.getBackpack().getItems(ItemType.SCROLL).size();
        score.saveDate = formatTimeStamp();
        return score;
    }

    private static String formatTimeStamp() {
        return new java.text.SimpleDateFormat("dd.MM.yy HH:mm")
                .format(new java.util.Date(System.currentTimeMillis()));
    }

    // ===== getters =====

    public int getTreasure() { return treasure; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getDamageDealt() { return damageDealt; }
    public int getDamageTaken() { return damageTaken; }
    public int getSteps() { return steps; }
    public int getMaxLevel() { return maxLevel; }
    public int getFoodPicked() { return foodPicked; }
    public int getElixirsPicked() { return elixirsPicked; }
    public int getScrollsPicked() { return scrollsPicked; }
    public String getSaveDate() { return  saveDate;}

    public void setTreasure(int treasure) { this.treasure = treasure; }
    public void setEnemiesKilled(int enemiesKilled) { this.enemiesKilled = enemiesKilled; }
    public void setDamageDealt(int damageDealt) { this.damageDealt = damageDealt; }
    public void setDamageTaken(int damageTaken) { this.damageTaken = damageTaken; }
    public void setSteps(int steps) { this.steps = steps; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    public void setFoodPicked(int foodPicked) { this.foodPicked = foodPicked; }
    public void setElixirsPicked(int elixirsPicked) { this.elixirsPicked = elixirsPicked; }
    public void setScrollsPicked(int scrollsPicked) { this.scrollsPicked = scrollsPicked; }
    public void setSaveDate(String saveDate) { this.saveDate = saveDate; }
}
