package domain.item;

import java.util.Random;

public class ItemFactory {

    private static final Random random = new Random();

    public static Item randomItem(int level) {
        ItemType type = randomType(level);

        return switch (type) {
            case FOOD -> new Item(
                    ItemType.FOOD,
                    "food",
                    5 + level,
                    0, 0, 0,
                    0,
                    0
            );
            case ELIXIR -> new Item(
                    ItemType.ELIXIR,
                    "elixir",
                    0,
                    2,
                    1,
                    1,
                    0,
                    3
            );
            case SCROLL -> new Item(
                    ItemType.SCROLL,
                    "scroll",
                    0,
                    2,
                    1,
                    1,
                    0,
                    0
            );
            case WEAPON -> new Item(
                    ItemType.WEAPON,
                    "weapon",
                    0,0,0,
                    3 + level,
                    0,
                    0
            );
            default -> throw new IllegalStateException();
        };
    }

    private static ItemType randomType(int level) {
        int roll = random.nextInt(100);

        if (roll < Math.max(10, 40 -level * 2)) return ItemType.FOOD;
        if (roll < 60) return ItemType.ELIXIR;
        if (roll < 80) return ItemType.SCROLL;
        return ItemType.WEAPON;
    }

    public static Item key(KeyColor color) {
        Item i = new Item(
                ItemType.KEY,
                color.name().toLowerCase(),
                0,0,0,0,0,0
        );
        i.setKeyColor(color);
        return i;
    }
}
