package domain.item;

import domain.character.Player;
import domain.common.Position;

public class Item {
    private final ItemType type;
    private final String subtype;

    private final int health; // здоровье (количество единиц повышения, для еды),
    private final int maxHealth;  // максимальный уровень здоровья (количество единиц повышения, для свитков и эликсиров, вместе с этим повышается и сам уровень здоровья),
    private final int agility;    // ловкость (количество единиц повышения, для свитков и эликсиров),
    private final int strength;   // сила (количество единиц повышения, для свитков, эликсиров и оружия),
    private final int cost;   // стоимость (для сокровищ).

    private Position position;
    private int x;
    private int y;

    private final int duration; // >0 для эликсиров, 0 для свитков
    //private boolean isTemporary; // true для эликсиров, false для свитков

    private KeyColor keyColor; // для дверей

    public Item(ItemType type, String subtype, int health, int maxHealth,
                int agility, int strength, int cost, int duration) {
        this.type = type;
        this.subtype = subtype;
        this.health = health;
        this.maxHealth = maxHealth;
        this.agility = agility;
        this.strength = strength;
        this.cost = cost;
        this.duration = duration;
    }

    public ItemType getType() { return type; }
    public String getSubtype() { return subtype; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getAgility() { return agility; }
    public int getStrength() { return strength; }
    public int getCost() { return cost; }
    public int getDuration() { return duration; }

    public int getX() { return position.x; }
    public int getY() { return position.y; }
    public double getDY() { return position.dY; }
    public double getDX() { return position.dX;}

    public void setPosition(Position pos) {
        this.position = pos;
    }

    // использование предмета
    public void apply(Player player) {
        switch (type) {
            case FOOD:
                player.heal(health);
                break;
            case SCROLL:    // ПОСТОЯННЫЕ эффекты
                player.applyPermanentEffect(maxHealth, agility, strength);
                break;
            case ELIXIR:    // ВРЕМЕННЫЕ эффекты
                player.applyTemporaryEffect(duration, maxHealth, agility, strength);
                break;

            case WEAPON:
                //player.setCurrentWeapon(this);
                break;

            default:
                break;
        }
    }

    public Position getPosition() {
        return position;
    }

    public KeyColor getKeyColor() { return keyColor; }
    public void setKeyColor(KeyColor keyColor) { this.keyColor = keyColor; }

    @Override
    public String toString() {
        return capitalize(type.toString()) +
                (getMaxHealth() > 0 ? " (+ " + getMaxHealth() + " max HP)" : "") +
                (getHealth() > 0 ? " (+ " + getHealth() + " HP)" : "") +
                (getAgility() > 0 ? " (+ " + getAgility() + " Agility)" : "") +
                (getStrength() > 0 ? " (+ " + getStrength() + " Strength)" : "") +
                (getDuration() > 0 ? " : " + getDuration() + " Duration" : "");
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return "-";
        }
        return  str.substring(0, 1).toUpperCase() +
                str.substring(1).toLowerCase();
    }

    public static String toString(Item item) {
        return item == null ? "----" : item.toString();
    }
}
