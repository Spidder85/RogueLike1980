package domain.map;

import domain.common.Position;
import domain.item.KeyColor;

public class DoorMeta {
    private final Position position;
    private final KeyColor color;
    //private boolean open;

    public DoorMeta(Position position, KeyColor color) {
        this.position = position;
        this.color = color;
    }

    public Position getPosition() { return position; }
    public KeyColor getColor() { return color; }

    public int getX() {
        return position.x;
    }
    public int getY() {
        return position.y;
    }
//    public boolean isOpen() { return open; }
//    public void open() { this.open = true; }
}
