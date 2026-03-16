package domain.map;

import domain.game.BalanceMode;
import domain.item.Item;
import domain.item.ItemFactory;
import domain.common.Position;
import domain.enemy.EnemyFactory;
import settings.GameSettings;

import java.util.List;
import java.util.Random;

import domain.enemy.Enemy;

public class LevelGenerator {
    private LevelGenerator() {}

    public static Level generate(int levelIndex, long worldSeed) {
        return generate(levelIndex, worldSeed, BalanceMode.NORMAL);
    }

    public static Level generate(int levelIndex, long worldSeed, BalanceMode balanceMode) {
        Random random = new Random(worldSeed + levelIndex);
        // 1. комнаты
        List<Room> rooms = RoomGenerator.createRooms(levelIndex, random);

        // 2. коридоры
        List<Corridor> corridors = CorridorGenerator.generate(rooms);

        // 3. уровень
        Level level = new Level(levelIndex);
        level.setRooms(rooms);  // задать начало комнаты
        level.setCorridors(corridors);

        // 4. контент (враги, предметы)
        populate(level, worldSeed, balanceMode);

        // 5. двери и ключи
        if (GameSettings.ENABLE_KEY){
            for (int i = 0; i < 20; i++) {
                DoorKeyGenerator.generate(level, random);

                if(LevelAccessibilityValidator.isReachable(level)) {
                    break;
                }
                level.clearDoorsAndKeys();
            }
        }

        return level;
    }

    private static void populate(Level level, long worldSeed, BalanceMode balanceMode) {
        Random random = new Random(worldSeed * 31 + level.getIndex() * 17L + balanceMode.ordinal() * 1009L);

        int levelNumber = level.getIndex();

        for (Room room : level.getRooms()) {
            if (room.isStart) continue;

            // Enemies
            int maxEnemies = Math.min(
                    GameSettings.MAX_COUNT_ENEMIES_IN_ROOM,
                    1 + (int)(Math.log(levelNumber + 1))
            );
            int enemyCount = scaleCount(random.nextInt(maxEnemies+1), balanceMode, true);

            for (int i = 0; i < enemyCount; i++) {
                Position pos = room.getRandomFreePoint(
                        p -> level.getEnemyAt(p) == null
                );

                Enemy enemy = EnemyFactory.randomEnemy(levelNumber, pos, balanceMode, random);
                level.addEnemy(enemy);
            }

            // Items
            int maxItems = Math.max(
                    1,
                    (int)(
                        GameSettings.MAX_COUNT_ITEM_IN_ROOM /
                        (1 + Math.log(levelNumber + 1))
                    )
            );
            int itemCount = Math.max(1, scaleCount(random.nextInt(maxItems) + 1, balanceMode, false));
            for (int i = 0; i < itemCount; i++) {
                Item item = ItemFactory.randomItem(levelNumber, balanceMode, random);
                Position p = room.getRandomFreePoint(
                        pos -> level.getEnemyAt(pos) == null
                );
                item.setPosition(p);
                level.addItem(item);
            }
        }
    }

    private static int scaleCount(int baseCount, BalanceMode balanceMode, boolean canBeZero) {
        double multiplier = switch (balanceMode) {
            case EASY_ASSIST -> canBeZero ? 0.7 : 1.4;
            case HARDER -> canBeZero ? 1.4 : 0.75;
            case NORMAL -> 1.0;
        };
        int min = canBeZero ? 0 : 1;
        return Math.max(min, (int) Math.round(baseCount * multiplier));
    }
}
