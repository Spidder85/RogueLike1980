package domain;

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

    private int x;
    private int y;

    private final int duration; // >0 для эликсиров, 0 для свитков
    //private boolean isTemporary; // true для эликсиров, false для свитков

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

    public int getX() { return x; }
    public int getY() { return y;}

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
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
        return new Position(x, y);
    }
}
