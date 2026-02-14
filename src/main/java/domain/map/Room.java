package domain.map;

import domain.item.Item;
import domain.enemy.Enemy;
import domain.common.Position;

import java.util.*;
import java.util.function.Predicate;

public class Room {
    private static final Random RANDOM = new Random();

    public final int x, y, width, height;

    public boolean isStart = false;
    public boolean isEnd = false;

    public final List<Position> walls = new ArrayList<>();
    public final List<Position> doors = new ArrayList<>();

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        buildWalls();
    }

    private void buildWalls() {
        // top / bottom
        for (int i = 0; i < width; i++) {
            walls.add(new Position(x + i, y));
            walls.add(new Position(x + i, y + height - 1));
        }

        // left / right
        for (int i = 1; i < height - 1; i++) {
            walls.add(new Position(x, y + i));
            walls.add(new Position(x + width - 1, y + i));
        }
    }

    public Position getCenter() {
        return new Position(
                x + width / 2,
                y + height / 2
        );
    }

    public Position getRandomPoint() {
        return new Position(
                x + 1 + RANDOM.nextInt(width - 2),
                y + 1 + RANDOM.nextInt(height - 2)
//                x + 1 + (int) (Math.random() * (width - 2)),
//                y + 1 + (int) (Math.random() * (height - 2))
        );
    }

    public boolean isValid(Position p) {
        boolean inside =
              p.x > x && p.x < x + width - 1 &&
              p.y > y && p.y < y + height - 1;

        return inside || isDoor(p);
    }

    public boolean isDoor(Position p) {
        return doors.contains(p);
    }

    public void addDoor(Position p) {
        if (!doors.contains(p))
            doors.add(p);
    }

    public boolean isCross(List<Room> rooms, int minDistance) {
        for (Room r : rooms) {
            if (r == this) continue;

            if (
                    x - minDistance < r.x + r.width &&
                    x + width + minDistance > r.x &&
                    y - minDistance < r.y + r.height &&
                    y + height + minDistance > r.y
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean isParallel(List<Room> rooms) {
        for (Room r : rooms) {
            if (r == this) continue;

            if (
                    x == r.x ||
                            x + width == r.x + r.width ||
                            y == r.y ||
                            y + height == r.y + r.height
            ) {
                return true;
            }
        }
        return false;
    }

    public Position getRandomFreePoint(Predicate<Position> isFree) {
        List<Position> freePoints = new ArrayList<>();

        for (int px = x+1; px < x + width - 1; px++) {
            for (int py = y+1; py < y + height - 1; py++) {
                Position p = new Position(px, py);
                if (isFree.test(p)) {
                    freePoints.add(p);
                }
            }
        }

        if (freePoints.isEmpty()) {
            throw new IllegalStateException("Room is full");
        }

        return freePoints.get(RANDOM.nextInt(freePoints.size()));

    }

    public Position getExitPointToward(Room target) {
        boolean overlapX =
                x < target.x + target.width+3 &&
                        x + width +3> target.x;

        boolean overlapY =
                y < target.y + target.height +3&&
                        y + height+3 > target.y;

        Position c = getCenter();
        Position tc = target.getCenter();

        // 1. Есть пересечение по X → идём по Y
        if (overlapX && !overlapY) {
            if (tc.y > c.y) {
                return new Position(c.x, y + height - 1); // вниз
            } else {
                return new Position(c.x, y); // вверх
            }
        }

        // 2. Есть пересечение по Y → идём по X
        if (overlapY && !overlapX) {
            if (tc.x > c.x) {
                return new Position(x + width - 1, c.y); // вправо
            } else {
                return new Position(x, c.y); // влево
            }
        }
        // 3. Нет пересечений → выбираем ближайшую сторону
        int leftGap     = Math.abs(target.x + target.width - x);
        int rightGap    = Math.abs(x + width - target.x);
        int topGap      = Math.abs(target.y + target.height - y);
        int bottomGap   = Math.abs(y + height - target.y);

        int min = Math.min(
                Math.min(leftGap, rightGap),
                Math.min(topGap, bottomGap)
        );

        if (min == leftGap)
            return new Position(x, getCenter().y);
        if (min == rightGap)
            return new Position(x + width - 1, getCenter().y);
        if (min == topGap)
            return new Position(getCenter().x, y);
        //if (min == bottomGap)
        return new Position(getCenter().x, y + height - 1);
    }

    public Position getRandomDoorPosition(Random random) {
        if (doors.isEmpty()) {
            return null;
        }
        return doors.get(random.nextInt(doors.size()));
    }
}
