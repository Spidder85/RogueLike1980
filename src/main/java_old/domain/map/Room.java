package domain.map;

import domain.Item;
import domain.enemy.Enemy;

import java.util.*;

public class Room {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private boolean start;
    private boolean exit;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean contains(int px, int py) {
        return px >= x && px < x + width &&
                py >= y && py < y + height;
    }

    // содержимое
    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Item> getItems() {
        return items;
    }

    // флаги
    public boolean isStart() { return start; }
    public boolean isExit() { return exit; }

    public void setStart(boolean start) { this.start = start; }
    public void setExit(boolean exit) { this.exit = exit; }




    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
