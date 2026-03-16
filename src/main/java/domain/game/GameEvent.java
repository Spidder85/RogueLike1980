package domain.game;

public class GameEvent {
    private final String type;
    //private final int x, y;
    private final int value;
    private final String itemType;

    public GameEvent(String type, int value, String itemType) {
        this.type = type;
        this.value = value;
        this.itemType = itemType;
    }

    public String getType() { return type; }
    public int getValue() { return value; }
    public String getItemType() { return itemType; }
}
