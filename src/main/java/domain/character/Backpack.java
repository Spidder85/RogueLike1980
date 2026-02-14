package domain.character;

import domain.item.Item;
import domain.item.ItemType;
import domain.item.KeyColor;

import java.util.*;

public class Backpack {
    private static final int LIMIT = 9;

    private Map<ItemType, List<Item>> items = new EnumMap<>(ItemType.class);
    private int treasureAmount = 0;
    private int keyMask = 0;

    public Backpack() {
        for (ItemType type : ItemType.values()) {
            if (type != ItemType.TREASURE && type != ItemType.KEY)
                items.put(type, new ArrayList<>());
        }
    }

    // ключи
    public void addKey(KeyColor color) {
        keyMask |= color.bit();
    }
    public boolean hasKey(KeyColor color) {
        return (keyMask & color.bit()) != 0;
    }
    public void useKey(KeyColor color) {
        keyMask &= ~color.bit();
    }
    public void clearKeys() {
        keyMask = 0;
    }
    public int getKeyMask() {
        return keyMask;
    }
    public void setKeyMask(int keyMask) {
        this.keyMask = keyMask;
    }

    // treasure
    public int getTreasureAmount() {
        return treasureAmount;
    }
    public void addTreasure(int amount) {
        treasureAmount += amount;
    }
    public void setTreasure(int amount) {
        this.treasureAmount = amount;
    }

    public boolean addItem(Item item) {
        if (item.getType() == ItemType.TREASURE) {
            addTreasure(item.getCost());
            return true;
        }

        if (item.getType() == ItemType.KEY) {
            addKey(item.getKeyColor());
            return true;
        }

        List<Item> list = items.get(item.getType());
        if (list.size() >= LIMIT) {
            return false;
        }
        list.add(item);
        return true;
    }

    public boolean removeItem(Item item) {
//        if (item.getType() == ItemType.TREASURE) {
//            return false;
//        }
//        return items.get(item.getType()).remove(item);
        return items.getOrDefault(item.getType(), List.of()).remove(item);
    }

    public List<Item> getItems(ItemType type) {
        return Collections.unmodifiableList(items.get(type));
    }

    public Map<ItemType, List<Item>> getItemsMap() {
        return items;
    }

    public void clear() {
        items.values().forEach(List::clear);
        treasureAmount = 0;
        keyMask = 0;
    }
}
