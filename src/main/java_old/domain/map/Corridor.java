package domain.map;

import java.util.HashSet;
import java.util.Set;

public class Corridor {
//    private int startX;
//    private int startY;
//    private int endX;
//    private int endY;
//
//    public Corridor(int startX, int startY, int endX, int endY) {
//        this.startX = startX;
//        this.startY = startY;
//        this.endX = endX;
//        this.endY = endY;
//    }
//
//    public boolean contains(int x, int y) {
//        // простая проверка для прямой линии
//        if (startX == endX) {
//            return x == startX &&
//                   y >= Math.min(startY, endY) &&
//                   y <= Math.max(startY, endY);
//        } else if (startY == endY) {    // для вертикальной линии
//            return y == startY &&
//                   x >= Math.min(startX, endX) &&
//                   x <= Math.max(startX, endX);
//        }
//        return false;
//    }
//
//    public int getStartX() { return startX; }
//    public int getStartY() { return startY; }
//    public int getEndX() { return endX; }
//    public int getEndY() { return endY; }
    private final Set<Point> tiles = new HashSet<>();

    public void addTile(int x, int y) {
        tiles.add(new Point(x, y));
    }

    public boolean contains(int x, int y) {
        return tiles.contains(new Point(x, y));
    }

    public Set<Point> getTiles() {
        return tiles;
    }

    public record Point(int x, int y) {}
}
