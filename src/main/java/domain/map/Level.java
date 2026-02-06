package domain.map;

import domain.Item;
import domain.common.Position;
import domain.enemy.Enemy;

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

        this.exitPosition = exitRoom.getRandomPoint();
    }

    public void setCorridors(List<Corridor> corridors) {
        this.corridors.clear();
        this.corridors.addAll(corridors);
    }

    public boolean isWalkable(Position p) {
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





//    public static final int ROOM_COUNT = 9;
//
//    private final int levelNumber;
//
//    private final List<Room> rooms = new ArrayList<>();
//    private final List<Corridor> corridors = new ArrayList<>();
//    private final List<Enemy> enemies = new ArrayList<>();
//    private final List<Item> items = new ArrayList<>();
//
////    private final Room startRoom;
////    private final Room exitRoom;
//
//    public Level(int levelNumber) {
//        this.levelNumber = levelNumber;
//    }
//
//    // генерация уровня
//    public void  generate() {
//        LevelGenerator generator = new LevelGenerator();
//        generator.generate(this);
//
//        validateRoomCount();
//        validateConnectivity();
//    }
//
//    // регистрация уровня
//    void addRoom(Room room) {
//        rooms.add(room);
//    }
//
//    void addCorridor(Corridor corridor) {
//        corridors.add(corridor);
//    }
//
    void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    void addItem(Item item) {
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
//
//    // доступ
//
//    public int getLevelNumber() {
//        return levelNumber;
//    }

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
//
//    public Room getStartRoom() {
//        return rooms.stream()
//                .filter(Room::isStart)
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalStateException("Start room is missing"));
//    }
//
//    public Room getExitRoom() {
//        return rooms.stream()
//                .filter(Room::isExit)
//                .findFirst()
//                .orElseThrow(() ->
//                        new IllegalStateException("Exit room is missing"));
//    }
//
//    public Position getStartPosition() {
//        Room start = getStartRoom();
//        return new Position(
//                start.getCenterX(),
//                start.getCenterY()
//        );
//    }
//
//    public Position getExitPosition() {
//        Room exit = getExitRoom();
//        return new Position(
//                exit.getCenterX(),
//                exit.getCenterY()
//        );
//    }
//
//    // Проверка является ли позиция выходом
//    public boolean isExitPosition(int x, int y) {
//        Position exit = getExitPosition();
//        return exit.x == x && exit.y == y;
//    }
//
    public Enemy getEnemyAt(int x, int y) {
        return enemies.stream()
                .filter(e -> e.isAlive() && e.getX() == x && e.getY() == y)
                .findFirst()
                .orElse(null);
    }

    public Item getItemAt(int x, int y) {
        return items.stream()
                .filter(e -> e.getX() == x && e.getY() == y)
                .findFirst()
                .orElse(null);
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
                exitPosition.x == p.x &&
                exitPosition.y == p.y;
    }

    public Object getObjectAt(Position p) {
        Enemy enemy = getEnemyAt(p.x, p.y);
        if (enemy != null) return enemy;

        Item item = getItemAt(p.x, p.y);
        if (item != null) return item;

        if (isExit(p)) return exitPosition;

        return null;
    }
}
