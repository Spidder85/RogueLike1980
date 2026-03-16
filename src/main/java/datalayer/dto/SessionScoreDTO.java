package datalayer.dto;

import domain.game.SessionScore;

import java.util.Date;

public class SessionScoreDTO {
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

    public SessionScoreDTO(SessionScore score){
        this.treasure = score.getTreasure();
        this.enemiesKilled = score.getEnemiesKilled();
        this.damageDealt = score.getDamageDealt();
        this.damageTaken = score.getDamageTaken();
        this.steps = score.getSteps();
        this.maxLevel = score.getMaxLevel();
        this.foodPicked = score.getFoodPicked();
        this.elixirsPicked = score.getElixirsPicked();
        this.scrollsPicked = score.getScrollsPicked();
        this.saveDate = score.getSaveDate();
    }

    public int getTreasure() { return treasure; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public int getDamageDealt() { return damageDealt; }
    public int getDamageTaken() { return damageTaken; }
    public int getSteps() { return steps; }
    public int getMaxLevel() { return maxLevel; }
    public int getFoodPicked() { return foodPicked; }
    public int getElixirsPicked() { return elixirsPicked; }
    public int getScrollsPicked() { return scrollsPicked; }
    public String getSaveDate() { return saveDate; }
}
