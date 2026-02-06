package domain;

import domain.enemy.Enemy;
import domain.map.Corridor;
import domain.map.Room;

import java.util.*;

public class Level {

    public static final int ROOM_COUNT = 9;

    private final int levelNumber;

    private final List<Room> rooms = new ArrayList<>();
    private final List<Corridor> corridors = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

//    private final Room startRoom;
//    private final Room exitRoom;

    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    // генерация уровня
    public void  generate() {
        LevelGenerator generator = new LevelGenerator();
        generator.generate(this);

        validateRoomCount();
        validateConnectivity();
    }

    // регистрация уровня
    void addRoom(Room room) {
        rooms.add(room);
    }

    void addCorridor(Corridor corridor) {
        corridors.add(corridor);
    }

    void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    void addItem(Item item) {
        items.add(item);
    }

    // логика передвижения
    public boolean canMoveTo(int x, int y) {
        for (Room room : rooms) {
            if (room.contains(x, y)) {
                return true;
            }
        }
        for (Corridor corridor : corridors) {
            if (corridor.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    // доступ

    public int getLevelNumber() {
        return levelNumber;
    }

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

    public Room getStartRoom() {
        return rooms.stream()
                .filter(Room::isStart)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Start room is missing"));
    }

    public Room getExitRoom() {
        return rooms.stream()
                .filter(Room::isExit)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Exit room is missing"));
    }

    public Position getStartPosition() {
        Room start = getStartRoom();
        return new Position(
                start.getCenterX(),
                start.getCenterY()
        );
    }

    public Position getExitPosition() {
        Room exit = getExitRoom();
        return new Position(
                exit.getCenterX(),
                exit.getCenterY()
        );
    }

    // Проверка является ли позиция выходом
    public boolean isExitPosition(int x, int y) {
        Position exit = getExitPosition();
        return exit.x == x && exit.y == y;
    }

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

    // =========================
    // ПРОВЕРКИ КОРРЕКТНОСТИ
    // =========================
    private void validateRoomCount() {
        if (rooms.size() != ROOM_COUNT) {
            throw new IllegalStateException(
                    "Level must contain exactly " + ROOM_COUNT + " rooms"
            );
        }
    }

    private void validateConnectivity() {
        Map<Room, List<Room>> graph = buildRoomGraph();

        Set<Room> visited = new HashSet<>();
        Deque<Room> stack = new ArrayDeque<>();

        Room start = rooms.getFirst();
        stack.push(start);
        visited.add(start);

        while (!stack.isEmpty()) {
            Room current = stack.pop();
            for (Room neighbor : graph.get(current)) {
                if (visited.add(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }

        if (visited.size() != rooms.size()) {
            throw new IllegalStateException(
                    "Generated level is not connected"
            );
        }
    }

    private Map<Room, List<Room>> buildRoomGraph() {
        Map<Room, List<Room>> graph = new HashMap<>();
        for (Room room : rooms) {
            graph.put(room, new ArrayList<>());
        }
        for (Corridor corridor : corridors) {
            List<Room> touchedRooms = new ArrayList<>();

            for (Room room : rooms) {
                for (Corridor.Point p : corridor.getTiles()) {
                    if (room.contains(p.x(), p.y())) {
                        touchedRooms.add(room);
                        break;
                    }
                }
            }
            if (touchedRooms.size() >= 2) {
                for(int i = 0; i < touchedRooms.size(); i++) {
                    for (int j = i + 1; j < touchedRooms.size(); j++) {
                        Room a = touchedRooms.get(0);
                        Room b = touchedRooms.get(1);

                        graph.get(a).add(b);
                        graph.get(b).add(a);
                    }
                }

            }
        }
        return graph;
    }

    // =========================
    // ВСПОМОГАТЕЛЬНЫЕ ТИПЫ
    // =========================
    public static class Position {
        public final int x;
        public final int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
