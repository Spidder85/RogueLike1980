package domain.map;

import settings.GameSettings;

import java.util.*;

public class RoomGenerator {
    private static final Random random = new Random();

    private RoomGenerator() {}

    public static List<Room> createRooms(int level) {
        List<Room> rooms = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = GameSettings.COUNT_ROOMS * 500;

        while (
                rooms.size() < GameSettings.COUNT_ROOMS &&
                attempts < maxAttempts
        ) {
            int width = random.nextInt(5) + GameSettings.MIN_ROOM_WIDTH;
            int height = random.nextInt(4) + GameSettings.MIN_ROOM_HEIGHT;

            int maxX = Math.max(1, GameSettings.GAME_WIDTH - width - 1);
            int maxY = Math.max(1, GameSettings.GAME_HEIGHT - height - 1);

            int x = random.nextInt(maxX);
            int y = random.nextInt(maxY);

            Room room = new Room(x, y, width, height);

            if (
                    !room.isCross(rooms, GameSettings.MIN_ROOM_DISTANCE) &&
                    !room.isParallel(rooms)
            ) {
                rooms.add(room);
            }
        }

        if (!rooms.isEmpty()) {
            setStartAndEnd(rooms);
        }
        return rooms;
    }

    // =========================
    // старт и выход
    // =========================
    private static void setStartAndEnd(List<Room> rooms) {
        Collections.shuffle(rooms);

        rooms.get(0).isStart = true;

        if (rooms.size() > 1) {
            rooms.get(1).isEnd = true;
        } else {
            rooms.get(0).isEnd = true;
        }
    }
}
