package domain.enemy;

import domain.character.Player;
import domain.common.Position;
import domain.game.GameEvent;

import java.util.List;

public abstract class Enemy {

    protected final EnemyType type; // тип врага

    private int health; // здоровье врага
    private int maxHealth;  // максимальное здоровье врага
    private int agility;    // ловкость врага
    private int strength;   // сила врага
    private int hostility;  // враждебность врага

    protected Position position;

    protected Enemy(
            EnemyType type,
            int health,
            int agility,
            int strength,
            int hostility,
            Position position
    ) {
        this.type = type;
        this.health = health;
        this.maxHealth = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = hostility;
        this.position = position;
    }

    /* ======================
       БАЗОВОЕ СОСТОЯНИЕ
       ====================== */

    public EnemyType getType() { return type; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAgility() { return agility; }
    public int getStrength() { return strength; }
    public int getHostility() { return hostility; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean takeDamage(int damage) {
        health = Math.max(0, health - damage);
        return true;
    }

    /* ======================
       ПОВЕДЕНИЕ (ШАБЛОН)
       ====================== */

    //  Решение намерения врага на текущий ход.
    public abstract EnemyIntent decideIntent(EnemyContext context);

    // Может ли враг атаковать цель.
    public boolean canAttack(Player target) {
        return target != null && target.isAlive();
    }

    public int getX() {
        return position.x;
    }
    public int getY() {
        return position.y;
    }

    public void performSpecialAbility(Player player, List<GameEvent> events) {}

    public abstract void update();

    public void restore(
            int health,
            int maxHealth,
            int agility,
            int strength,
            int hostility,
            Position position
    ) {
        this.health = health;
        this.maxHealth = maxHealth;
        this.agility = agility;
        this.strength = strength;
        this.hostility = hostility;
        this.position = position;
    }
}
