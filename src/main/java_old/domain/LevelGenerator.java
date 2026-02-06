package domain;

//import domain.map.Corridor;
import domain.enemy.Enemy;
import domain.enemy.EnemyFactory;
import domain.map.Corridor;
import domain.map.Room;
//import domain.map.Section;

import java.util.*;

public class LevelGenerator {
    private static final int LVL_WIDTH = 80;
    private static final int LVL_HEIGHT = 25;

    private static final int SECTION_X = 3;
    private static final int SECTION_Y = 3;

    private final Random random = new Random();

    public void generate(Level level) {
        int sectionW = LVL_WIDTH / SECTION_X;
        int sectionH = LVL_HEIGHT / SECTION_Y;

        Room[] rooms = new Room[SECTION_X * SECTION_Y];
        int idx = 0;
        // генерация комнат
        for (int sy = 0; sy < SECTION_Y; sy++) {
            for (int sx = 0; sx < SECTION_X; sx++) {
                int baseX = sx * sectionW;
                int baseY = sy * sectionH;

                int w = 4 + random.nextInt(sectionW - 4);
                int h = 4 + random.nextInt(sectionH - 4);

                int x = baseX + random.nextInt(sectionW - w);
                int y = baseY + random.nextInt(sectionH - h);

                Room room = new Room(x, y, w, h);
                rooms[idx++] = room;
                level.addRoom(room);
            }
        }

        // соединяем комнаты цепочкой (гарантированная связность)
        for (int i = 0; i < rooms.length - 1; i++) {
            Corridor corridors = connect(rooms[i], rooms[i+1]);
            level.addCorridor(corridors);
        }
        // добавляем дополнительные 2 случайные связи
        int extraLinks = 2;
        for (int i = 0; i < extraLinks; i++) {
            Room a = rooms[random.nextInt(rooms.length)];
            Room b = rooms[random.nextInt(rooms.length)];
            if (a != b) {
                level.addCorridor(connect(a, b));
            }
        }


        // Старт и выход
        Room start = rooms[random.nextInt(rooms.length)];
        Room exit;
        do {
            exit = rooms[random.nextInt(rooms.length)];
        } while (exit == start);

        start.setStart(true);
        exit.setExit(true);

        populate(level);
    }

    private Corridor connect(Room a, Room b) {
        Corridor corridor = new Corridor();

        int x = a.getCenterX();
        int y = a.getCenterY();

        // горизонталь
        while (x != b.getCenterX()) {
            corridor.addTile(x, y);
            x+= Integer.compare(b.getCenterX(), x);
        }
        // вертикаль
        while (y != b.getCenterY()) {
            corridor.addTile(x, y);
            y += Integer.compare(b.getCenterY(), y);
        }

        return corridor;
    }

    private void populate(Level level) {
        for (Room room : level.getRooms()) {
            if (room.isStart()) continue;

            int enemyCount = random.nextInt(level.getLevelNumber() + 1);
            for (int i = 0; i < enemyCount; i++) {
                Enemy enemy = EnemyFactory.randomEnemy(level.getLevelNumber());
                placeEnemy(enemy, room, level);
                room.addEnemy(enemy);
                level.addEnemy(enemy);
            }

            int itemCount = random.nextInt(0, 3 - level.getLevelNumber() / 3);
            for (int i = 0; i < itemCount; i++) {
                Item item = ItemFactory.randomItem(level.getLevelNumber());
                placeItem(item, room, level);
                room.addItem(item);
                level.addItem(item);
            }
        }
    }

    private void placeEnemy(Enemy enemy, Room room, Level level) {
        int x, y;

        do {
            x = room.getX() + random.nextInt(room.getWidth());
            y = room.getY() + random.nextInt(room.getHeight());
        } while (
                level.getEnemyAt(x, y) != null ||
                level.getItemAt(x, y) != null
        );

        enemy.setPosition(x, y);
        enemy.setRoom(room);
    }

    private void placeItem(Item item, Room room, Level level) {
        int x, y;

        do {
            x = room.getX() + random.nextInt(room.getWidth());
            y = room.getY() + random.nextInt(room.getHeight());
        } while (
                level.getEnemyAt(x, y) != null ||
                level.getItemAt(x, y) != null
        );

        item.setPosition(x, y);
    }
}
