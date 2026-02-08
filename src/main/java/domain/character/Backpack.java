package domain.character;

import domain.Item;
import domain.ItemType;

import java.util.*;

public class Backpack {
    private static final int LIMIT = 9;

    private Map<ItemType, List<Item>> items = new EnumMap<>(ItemType.class);
    private int treasureAmount = 0;

    public Backpack() {
        for (ItemType type : ItemType.values()) {
            items.put(type, new ArrayList<>());
        }
    }

    public boolean addItem(Item item) {
        if (item.getType() == ItemType.TREASURE) {
            treasureAmount += item.getCost();
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
        if (item.getType() == ItemType.TREASURE) {
            return false;
        }
        return items.get(item.getType()).remove(item);
    }

    public List<Item> getItems(ItemType type) {
        return Collections.unmodifiableList(items.get(type));
    }

    public int getTreasureAmount() {
        return treasureAmount;
    }

    public Map<ItemType, List<Item>> getItemsMap() {
        return items;
    }

    public void clear() {
        for (List<Item> list : items.values()) {
            list.clear();
        }
        treasureAmount = 0;
    }
}
