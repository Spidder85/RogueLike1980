package domain.character;

import domain.Item;
import domain.common.Position;

import java.util.*;

public class Character {
    private int maxHealth;  // максимальное здоровье
    private int health; // текущее здоровье
    private int agility;    // ловкость
    private int strength;   // сила

    private Item currentWeapon; // текущее оружие

    private int x;
    private int y;

    private final Backpack backpack = new Backpack();

    // -------- состояния --------
    private boolean sleeping = false; // для эффекта змея-мага
    private int sleepTurns = 0;

    // -------- ременные эффекты --------
    private final List<TemporaryEffect> temporaryEffects = new ArrayList<>();

    // вложенный класс для временных эффектов
    private static class TemporaryEffect {
        int remainingTurns;
        int maxHealthBonus;
        int agilityBonus;
        int strengthBonus;

        public TemporaryEffect(
                int remainingTurns,
                int maxHealthBonus,
                int agilityBonus,
                int strengthBonus
        ) {
            this.remainingTurns = remainingTurns;
            this.maxHealthBonus = maxHealthBonus;
            this.agilityBonus = agilityBonus;
            this.strengthBonus = strengthBonus;
        }
    }

    public Character(int maxHealth, int agility, int strength) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.agility = agility;
        this.strength = strength;
    }

    // -------- getters --------
    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    public Item getCurrentWeapon() {
        return currentWeapon;
    }

    public Backpack getBackpack() {
        return backpack;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // -------- позиция --------
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position getPosition() {
        return new Position(x, y);
    }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    // ---------- жизнь ----------
    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    // восстановление здоровья
    public void heal(int amount) {
        if (amount <= 0) return;
        health = Math.min(health + amount, maxHealth);
    }

    // -------- оружие --------
    public void setCurrentWeapon(Item currentWeapon) {
        this.currentWeapon = currentWeapon;
    }

    // ------------ эффекты -----------
    public void applyPermanentEffect(
            int maxHealthBonus,
            int agilityBonus,
            int strengthBonus
    ) {
        if (maxHealthBonus > 0) {
            maxHealth += maxHealthBonus;
            health += maxHealthBonus;
        }
        agility += agilityBonus;
        strength += strengthBonus;
    }

    public void applyTemporaryEffect(
            int duration,
            int maxHealthBonus,
            int agilityBonus,
            int strengthBonus
    ) {
        if (duration <= 0) return;

        // сначала применяем бонусы
        maxHealth += maxHealthBonus;
        health += maxHealthBonus; // увеличиваем здоровье на максимум
        agility += agilityBonus;
        strength += strengthBonus;

        // сохраняем эффект
        temporaryEffects.add(
                new TemporaryEffect(
                        duration,
                        maxHealthBonus,
                        agilityBonus,
                        strengthBonus
                )
        );
    }

    // метод для обновления состояний каждый ход
    public void update() {
        updateSleep(); // обновление состояния "сон"
        updateTemporaryEffects();   // обновление временных эффектов
    }

    private void updateSleep() {
        if (!sleeping) return;
        sleepTurns--;
        if (sleepTurns <= 0) sleeping = false;
    }

    private void updateTemporaryEffects() {
        Iterator<TemporaryEffect> it = temporaryEffects.iterator();
        while (it.hasNext()) {
            TemporaryEffect e = it.next();
            e.remainingTurns--;

            if (e.remainingTurns <= 0) {
                // отмена эффекта
                maxHealth -= e.maxHealthBonus;
                agility -= e.agilityBonus;
                strength -= e.strengthBonus;

                if (health > maxHealth) health = maxHealth; // если здоровье больше максимума

                // если здоровье <=0 после отмены эликсира
                if (e.maxHealthBonus > 0 && health <= 0) {
                    health = 1; // минимально возможное здоровье
                }
                it.remove();
            }
        }
    }

    // -------- сон (змей-маг) --------
    public void putToSleep(int turns) {
        sleeping = true;
        sleepTurns = Math.max(sleepTurns, turns);
    }

    public boolean canAct() {
        return !sleeping;
    }

    // -------- вампир --------
    public void decreaseMaxHealth(int amount) {
        if (amount <= 0) return;
        maxHealth = Math.max(1, maxHealth - amount);
        health = Math.min(health, maxHealth);
    }
}
