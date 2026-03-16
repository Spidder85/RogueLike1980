package domain.item;

import domain.game.BalanceMode;

import java.util.Random;

public class ItemFactory {

    private static final Random random = new Random();

    public static Item randomItem(int level) {
        ItemType type = randomType(level, BalanceMode.NORMAL, random);
        return create(type, level, random);
    }

    public static Item randomItem(int level, BalanceMode balanceMode, Random random) {
        ItemType type = randomType(level, balanceMode, random);
        return create(type, level, random);
    }

    private static Item create(ItemType type, int level, Random random) {
        return switch (type) {
            case FOOD -> new Item(
                    ItemType.FOOD,
                    "food",
                    12 + level / 2 + random.nextInt(4),
                    0, 0, 0,
                    0,
                    0
            );
            case ELIXIR -> {
                String stat = randomStat();
                yield new Item(
                        ItemType.ELIXIR,
                        stat,
                        0,
                        stat.equals("maxHealth") ? 10 + level/3 + random.nextInt(3): 0,
                        stat.equals("agility") ? 2 + random.nextInt(2) : 0,
                        stat.equals("strength") ? 2 + random.nextInt(2) : 0,
                        0,
                        4 + random.nextInt(2)
                );
            }
            case SCROLL -> {
                String stat = randomStat();
                yield new Item(
                        ItemType.SCROLL,
                        stat,
                        0,
                        stat.equals("maxHealth") ? 3 + level/8 + random.nextInt(2) : 0,
                        stat.equals("agility") ? 1 + level/14 + random.nextInt(2) : 0,
                        stat.equals("strength") ? 1 + level/14 + random.nextInt(2) : 0,
                        0,
                        0
                );
            }
            case WEAPON -> new Item(
                    ItemType.WEAPON,
                    "weapon",
                    0,0,0,
                    4 + level / 4 + random.nextInt(3),
                    0,
                    0
            );
            default -> throw new IllegalStateException();
        };
    }

    private static String randomStat() {
        return switch (random.nextInt(3)) {
            case 0 -> "agility";
            case 1 -> "strength";
            default -> "maxHealth";
        };
    }

    private static ItemType randomType(int level, BalanceMode balanceMode, Random random) {
        int roll = random.nextInt(100);

        int foodThreshold = Math.max(10, 40 - level * 2);
        int elixirThreshold = 60;
        int scrollThreshold = 80;

        if (balanceMode == BalanceMode.EASY_ASSIST) {
            foodThreshold = Math.min(60, foodThreshold + 20);
            elixirThreshold = Math.min(85, elixirThreshold + 15);
            scrollThreshold = Math.min(95, scrollThreshold + 10);
        } else if (balanceMode == BalanceMode.HARDER) {
            foodThreshold = Math.max(5, foodThreshold - 15);
            elixirThreshold = Math.max(foodThreshold + 10, elixirThreshold - 10);
            scrollThreshold = Math.max(elixirThreshold + 10, scrollThreshold - 10);
        }

        if (roll < foodThreshold) return ItemType.FOOD;
        if (roll < elixirThreshold) return ItemType.ELIXIR;
        if (roll < scrollThreshold) return ItemType.SCROLL;
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
