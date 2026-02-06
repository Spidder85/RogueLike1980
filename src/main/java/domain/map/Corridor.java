package domain.map;

import domain.common.Position;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Corridor {
    private final List<Position> path = new ArrayList<>();

    public Corridor(Position start, Position end, List<Room> rooms, Room startR) {
        //generateLPath(start, end, rooms);
//        if (shouldUseSPath(start, end, rooms)) {
        boolean isHorizontal = start.x == startR.x || start.x == (startR.x + startR.width-1);
            generateSPath(start, end, rooms, isHorizontal);
//        } else {
//            generateLPath(start, end, rooms);
//        }
    }

    private void generateLPath(Position start, Position end, List<Room> rooms) {
        int x = start.x;
        int y = start.y;

        tryAdd(start, rooms);   // сомнитольно, изначально небыло

        while (x != end.x) {
            x += Integer.signum(end.x - x);
            tryAdd(new Position(x, y), rooms);
        }

        while (y != end.y) {
            y += Integer.signum(end.y - y);
            tryAdd(new Position(x, y), rooms);
        }
    }

    private void generateSPath(Position start, Position end, List<Room> rooms, boolean isHorizontal) {
        // определяем основное направление
        //boolean isHorizontal = Math.abs(start.x - end.x) >= Math.abs(start.y - end.y);


//        int midX = (start.x + end.x) / 2;
//        int midY = (start.y + end.y) / 2;

        int x = start.x;
        int y = start.y;

        tryAdd(new Position(x, y), rooms);

        int midX = isHorizontal ? (start.x + end.x) / 2 : end.x;
        int midY = isHorizontal ? end.y : (start.y + end.y) / 2;

        if (isHorizontal) {
            while (x != midX) {
                x += Integer.signum(midX - x);
                tryAdd(new Position(x, y), rooms);
            }
        }

        while (y != midY) {
            y += Integer.signum(midY - y);
            tryAdd(new Position(x, y), rooms);
        }

        while (x != end.x) {
            x += Integer.signum(end.x - x);
            tryAdd(new Position(x, y), rooms);
        }

        while (y != end.y) {
            y += Integer.signum(end.y - y);
            tryAdd(new Position(x, y), rooms);{}
        }
    }

    private void tryAdd(Position p, List<Room> rooms) {
        if (isInsideRoom(p, rooms)) {
            path.add(p);
        }

        for (Room room : rooms) {
            if (room.walls.contains(p)) {
                if(!room.isDoor(p))
                    room.addDoor(p);
                else
                    path.add(p);
                return;
            }
        }
    }

    private boolean isInsideRoom(Position p, List<Room> rooms) {
        for (Room room : rooms) {
            if (
                p.x >= room.x &&
                p.x < room.x + room.width &&
                p.y >= room.y &&
                p.y < room.y + room.height
            ) {
                return false;
            }
        }
        return true;
    }

    public boolean isValid(Position p) {
        return path.contains(p);
    }

    public List<Position> getPath() {
        return path;
    }
}
