package domain.map;

import domain.item.Item;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.item.ItemType;
import settings.GameSettings;

import java.util.*;

public class Level {

    private final List<Room> rooms = new ArrayList<>();
    private final List<Corridor> corridors = new ArrayList<>();

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    public Room startRoom = null;
    public Room exitRoom = null;

    private Position exitPosition;

    private final int index;

    public Level(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms.clear();
        this.rooms.addAll(rooms);

        this.startRoom = rooms.stream()
                .filter(r -> r.isStart)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Start room is missing"));
        this.exitRoom = rooms.stream()
                .filter(r -> r.isEnd)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Exit room is missing"));

        Position exit = exitRoom.getRandomPoint();
        this.exitPosition = new Position(exit.x + 0.5, exit.y + 0.5);
    }

    public void setCorridors(List<Corridor> corridors) {
        this.corridors.clear();
        this.corridors.addAll(corridors);
    }

    public boolean isWalkable(Position p) {
        DoorMeta door = getDoorAt(p);
        if (door != null) {
            return false;
        }

        for (Room r : rooms) {
            if (r.isValid(p)) return true;
        }
        for (Corridor c : corridors) {
            if (c.isValid(p))  return true;
        }
        return false;
    }

    public Room findRoom(Position p) {
        return rooms.stream()
                .filter(r -> r.isValid(p))
                .findFirst()
                .orElse(null);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    // логика передвижения
    public boolean canMoveTo(int x, int y) {
        Position p = new Position(x, y);
        for (Room room : rooms) {
            if (room.isValid(p)) {
                return true;
            }
        }
        for (Corridor corridor : corridors) {
            if (corridor.isValid(p)) {
                return true;
            }
        }
        return false;
    }

    // доступ
    public List<Corridor> getCorridors() {
        return corridors;
    }

    public  List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Item> getItems() { return items; }

    public Enemy getEnemyAt(Position p) {
        return enemies.stream()
                //.filter(e -> e.isAlive() && e.getX() == x && e.getY() == y)
                .filter(e -> e.isAlive() && inCollisionBox(e.getPosition(), p))
                .findFirst()
                .orElse(null);
    }

    public Item getItemAt(Position p) {
        return items.stream()
                .filter(e -> inCollisionBox(e.getPosition(), p))
                .findFirst()
                .orElse(null);
    }

    public List<Item> getItemsAt(Position p) {
        return items.stream()
                .filter(e -> inCollisionBox(e.getPosition(), p))
                .toList();
    }

    private boolean inCollisionBox(Position pObj, Position pCheck) {
        double colBoxSize = GameSettings.COLLISION_BOX_SIZE;

        return pCheck.dX >= pObj.dX - colBoxSize &&
               pCheck.dX <= pObj.dX + colBoxSize &&
               pCheck.dY >= pObj.dY - colBoxSize &&
               pCheck.dY <= pObj.dY + colBoxSize;
    }


    public Position getExitPosition() {
        return exitPosition;
    }

    public void removeEnemy(Enemy enemy) {
        enemies.removeIf(e -> e == enemy);
    }

    public void removeItem(Item item) {
        items.removeIf(i -> i == item);
    }

    public boolean isExit(Position p) {
        return exitPosition != null &&
                inCollisionBox(exitPosition, p);
    }

    public Object getObjectAt(Position p, boolean byCell) {
        if (byCell) p = new Position(p.x, p.y);
        Enemy enemy = getEnemyAt(p);
        if (enemy != null) return enemy;

        Item item = getItemAt(p);
        if (item != null) return item;

        DoorMeta door = getDoorAt(p);
        if (door != null) return door;

        if (isExit(p)) return exitPosition;

        return null;
    }

    public Position findFreeAdjacentCell(Position from) {
        int[][] dirs = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };

        for (int[] d : dirs) {
            Position p = new Position(from.x + d[0], from.y + d[1]);
            if (isWalkable(p) && getObjectAt(p, true) == null) {
                return p;
            }
        }
        return null; // если все занято — оружие пропадает
    }

    private final List<DoorMeta> doors = new ArrayList<>();

    public List<DoorMeta> getDoors() {
        return doors;
    }

    public void addDoor(DoorMeta door) {
        doors.add(door);
    }

    public void removeDoor(DoorMeta door) {
        doors.removeIf(d -> d == door);
    }
    public DoorMeta getDoorAt(Position p) {
        for (DoorMeta d : doors) {
            if (d.getPosition().x == p.x
                    && d.getPosition().y == p.y) {
                return d;
            }
        }
        return null;
    }

    public void clearDoorsAndKeys() {
        doors.clear();
        items.removeIf(i -> i.getType() == ItemType.KEY);
    }

    public Room findNearestRoom(Position p) {
        Room nearest = null;
        double bestDistance = Double.MAX_VALUE;

        for (Room r : rooms) {
            // центр комнаты
            Position cPos = r.getCenter();
            double dx = p.dX - cPos.dX;
            double dy = p.dY - cPos.dY;

            double dist = dx * dx + dy * dy;
            if (dist < bestDistance) {
                bestDistance = dist;
                nearest = r;
            }
        }
        return nearest;
    }
}
