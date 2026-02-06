package domain.enemy;

import domain.Level;
import domain.character.Character;
import domain.map.Room;

import java.util.*;

public abstract class Enemy {

    protected final EnemyType type; // тип врага

    private int health; // здоровье врага
    private int maxHealth;  // максимальное здоровье врага
    private int agility;    // ловкость врага
    private int strength;   // сила врага
    private int hostility;  // враждебность врага

    protected int x;
    protected int y;

    protected Room room;

    protected static final Random random = new Random();

    protected Enemy(
            EnemyType type,
            int health,
            int agility,
            int strength,
            int hostility
    ) {
        this.type = type;
        this.health = health;
        this.maxHealth = health;
        this.agility = agility;
        this.strength = strength;
        this.hostility = hostility;
    }

    // ---------------- СОСТОЯНИЕ ----------------
    public boolean isAlive() {  // проверка, жив ли персонаж
        return health > 0;
    }

    public void takeDamage(int damage) { // получение урона
        health -= damage;
        if (health < 0) health = 0;
    }

    // ---------------- ПОЗИЦИЯ ----------------
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void setRoom(Room room) { this.room = room; }

    public Room getRoom() { return room; }

    // ---------------- ХАРАКТЕРИСТИКИ ----------------
    public EnemyType getType() { return type; }
    public int getHealth() { return health; }
    public int getAgility() { return agility; }
    public int getStrength() { return strength; }
    public int getHostility() { return hostility; }

    // ---------------- ПРОВЕРКИ ----------------
    public boolean isPlayerInRange(Character player) {
        int dx = Math.abs(player.getX() - x);
        int dy = Math.abs(player.getY() - y);
        return dx + dy <= hostility;
    }

    public boolean isNear(Character player) {
        int dx = Math.abs(player.getX() - x);
        int dy = Math.abs(player.getY() - y);
        return (dx + dy) <= 1;
    }

    // ---------------- ПОВЕДЕНИЕ ----------------
    /** Может ли атаковать игрока (например, вампир после первого удара) */
    public abstract boolean canAttack(Character player);
    public abstract void performSpecialAbility(Character player);
    public abstract void update();
    /** Индивидуальный паттерн движения врага */
    public abstract void move(Level level);


    // ---------------- ВСПОМОГАТЕЛЬНОЕ ДВИЖЕНИЕ ----------------
    protected void moveRandomly(Level level) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        int[] d = dirs[random.nextInt(dirs.length)];

        int nx = getX() + d[0];
        int ny = getY() + d[1];

        if (level.canMoveTo(nx, ny)) {
            setPosition(nx, ny);
        }
    }

    public void moveTowardsPlayer(Character player, Level level) {
        List<Position> possibleMoves = getPossibleMoves(level);
        if (possibleMoves.isEmpty()) return;

        // выбираем позицию ближе всего к игроку
        Position bestMove = possibleMoves.stream()
                .min(Comparator.comparingInt(pos ->
                        Math.abs(pos.x - player.getX()) +
                        Math.abs(pos.y - player.getY())))
                .orElse(null);

        if (bestMove != null) {
            setPosition(bestMove.x, bestMove.y);
        }
    }

    // Метод для получения возможных ходов
    private List<Position> getPossibleMoves(Level level) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        for(int[] dir : directions) {
            int nx = getX() + dir[0];
            int ny = getY() + dir[1];

            if (level.canMoveTo(nx, ny) && !isCellOccupied(level, nx, ny)) {
                moves.add(new Position(nx, ny));
            }
        }
        return moves;
    }

    // проверка занята ли клетка другим врагом
    private boolean isCellOccupied(Level level, int x, int y) {
        for (Enemy other : level.getEnemies()) {
            if (other != this && other.isAlive() &&
                    other.getX() == x && other.getY() == y) {
                return true;
            }
        }
        return false;
    }

    protected static class Position {
        final int x, y;
        Position(int x, int y) { this.x = x; this.y = y; }
    }

    public boolean canAct() {
        return true;
    }




    // -------- управление пропуском атаки --------
//    public boolean shouldSkipAttack() { return skipNextAttack; }
//    public void resetSkip() { skipNextAttack = false; }
//    public void markSkipNextAttack() { skipNextAttack = true; }
}
