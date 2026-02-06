package presentation;

import domain.Level;
import domain.map.Corridor;
import domain.map.Room;
import jcurses.system.CharColor;
import jcurses.system.Toolkit;

public class LevelRenderer {

    private static final CharColor WALL =
            new CharColor(CharColor.BLACK, CharColor.WHITE);
    private static final CharColor FLOOR =
            new CharColor(CharColor.BLACK, CharColor.WHITE);

    public void render(Level level) {
        drawRooms(level);
        drawCorridors(level);
    }

    private void drawRooms(Level level) {
        for (Room room : level.getRooms()) {
            drawRoom(room);
        }
    }

    private void drawRoom(Room room) {
        int x0 = room.getX();
        int y0 = room.getY();
        int x1 = x0 + room.getWidth() - 1;
        int y1 = y0 + room.getHeight() - 1;

        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                boolean wall =
                        x == x0 || x == x1 ||
                        y == y0 || y == y1;
                if (wall) {
                    Toolkit.printString("#", x, y ,WALL);
                } else {
                    Toolkit.printString(".", x, y, FLOOR);
                }
            }
        }
    }

    private void drawCorridors(Level level) {
        for (Corridor corridor : level.getCorridors()) {
            for (Corridor.Point p : corridor.getTiles()) {
                Toolkit.printString("*", p.x(), p.y(), FLOOR);
            }
        }
    }
}
