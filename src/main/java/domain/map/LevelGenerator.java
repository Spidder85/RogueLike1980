package domain.map;

import domain.Item;
import domain.ItemFactory;
import domain.common.Position;
import domain.enemy.EnemyFactory;
import settings.GameSettings;

import java.util.List;
import java.util.Random;

import domain.enemy.Enemy;

public class LevelGenerator {
    private LevelGenerator() {}

    public static Level generate(int levelIndex, long worldSeed) {
        Random random = new Random(worldSeed + levelIndex);
        // 1. комнаты
        List<Room> rooms = RoomGenerator.createRooms(levelIndex, random);

        // 2. коридоры
        List<Corridor> corridors = CorridorGenerator.generate(rooms);

        // 3. уровень
        Level level = new Level(levelIndex);
        level.setRooms(rooms);
        level.setCorridors(corridors);

        // 4. контент (пока пусто — строго как в JS до moveRoomContentToLevel)
        populate(level);

        return level;
    }

    private static void populate(Level level) {
        Random random = new Random();
    //private static void populate(Level level, Random random) {
        int levelNumber = level.getIndex();

        for (Room room : level.getRooms()) {
            if (room.isStart) continue;

            // Enemies
            int maxEnemies = Math.min(
                    GameSettings.MAX_COUNT_ENEMIES_IN_ROOM,
                    1 + (int)(Math.log(levelNumber + 1))
            );
            int enemyCount = random.nextInt(maxEnemies) + 1;

            for (int i = 0; i < enemyCount; i++) {
                Position pos = room.getRandomFreePoint(
                        p -> level.getEnemyAt(p.x, p.y) == null
                );

                Enemy enemy = EnemyFactory.randomEnemy(levelNumber, pos);
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
            int itemCount = random.nextInt(maxItems) + 1;
            for (int i = 0; i < itemCount; i++) {
                Item item = ItemFactory.randomItem(levelNumber);
                Position p = room.getRandomFreePoint(
                        pos -> level.getEnemyAt(pos.x, pos.y) == null
                );
                item.setPosition(p.x, p.y);
                level.addItem(item);
            }
        }
    }
}
