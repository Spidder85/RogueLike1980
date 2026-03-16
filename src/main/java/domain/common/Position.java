package domain.common;

import java.util.Objects;

public final class Position {

    public final int x;
    public final int y;
    public final double dX;
    public final double dY;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.dX = x + 0.5;
        this.dY = y + 0.5;
    }

    public Position(double x, double y) {
        this.x = (int) Math.floor(x);
        this.y = (int) Math.floor(y);
        this.dX = x;
        this.dY = y;
    }

    public Position move(Direction direction) {
        return new Position(x + direction.dx, y + direction.dy);
    }

    public Position add(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
