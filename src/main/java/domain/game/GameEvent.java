package domain.game;

public class GameEvent {
    private final String type;
    //private final int x, y;
    private final int value;
    private final String itemType;

    //public GameEvent(String type, int x, int y, int value, String itemType) {
    public GameEvent(String type, int value, String itemType) {
        this.type = type;
//        this.x = x;
//        this.y = y;
        this.value = value;
        this.itemType = itemType;
    }

    public String getType() { return type; }
//    public int getX() { return x; }
//    public int getY() { return y; }
    public int getValue() { return value; }
    public String getItemType() { return itemType; }
}
