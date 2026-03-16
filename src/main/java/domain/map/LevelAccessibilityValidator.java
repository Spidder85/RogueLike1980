package domain.map;

import domain.common.Position;
import domain.item.Item;
import domain.item.ItemType;

import java.util.*;

public class LevelAccessibilityValidator {
    private static final int[][] DIRS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };
    private record Node(int x, int y, int mask) {}

    public static boolean isReachable(Level level) {
        Position start = level.startRoom.getRandomPoint();
        Position exit = level.getExitPosition();

        Queue<Node> q = new ArrayDeque<>();
        Set<Node> visited = new HashSet<>();

        Node startNode = new Node(start.x, start.y, 0);
        q.add(startNode);
        visited.add(startNode);

        while (!q.isEmpty()) {
            Node cur = q.poll();

            if (cur.x == exit.x && cur.y == exit.y) {
                return true;
            }

            for (int[] d : DIRS) {
                int nx = cur.x + d[0];
                int ny = cur.y + d[1];
                Position nPos = new Position(nx, ny);
                int mask = cur.mask;

                //if (!level.isWalkable(nPos)) continue;
                DoorMeta door = level.getDoorAt(nPos);
                if (door != null) {
                    if ((mask & door.getColor().bit()) == 0) {
                        continue;
                    }
                } else if (!level.isWalkable(nPos)) {
                    continue;
                }

                Item item = level.getItemAt(nPos);
                if (item != null && item.getType() == ItemType.KEY) {
                    mask |= item.getKeyColor().bit();
                }

                Node next = new Node(nx, ny, mask);

                if (visited.add(next)) {
                    q.add(next);
                }
            }
        }
        return false;
    }
}
