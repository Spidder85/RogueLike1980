package domain.map;

import domain.common.Position;
import domain.item.Item;
import domain.item.ItemFactory;
import domain.item.KeyColor;

import java.util.*;

public class DoorKeyGenerator {
    private DoorKeyGenerator() {}

    public static void generate(Level level, Random random) {
        int pairCount = Math.min(
                KeyColor.values().length,
                level.getIndex() / 2 + 1
        );

        if (pairCount == 0) return;

        Map<Room, List<Room>> graph = buildGraph(level);
        Map<Room, Integer> dist = bfsRooms(level.startRoom, graph);
        List<Room> doorCandidates = level.getRooms().stream()
                .filter(r -> dist.getOrDefault(r, 0) >= 2)
                .toList();
        List<KeyColor> colors = new ArrayList<>(List.of(KeyColor.values()));
        Collections.shuffle(colors, random);

        for (int i = 0; i < pairCount && i < doorCandidates.size(); i++) {
            Room doorRoom = doorCandidates.get(random.nextInt(doorCandidates.size()));
            KeyColor color = colors.get(i);

            List<Room> keyRooms = level.getRooms().stream()
                    .filter(r -> dist.get(r) < dist.get(doorRoom))
                    .filter(r -> r != doorRoom)
                    .toList();

            if (keyRooms.isEmpty()) continue;

            Room keyRoom = keyRooms.get(random.nextInt(keyRooms.size()));

            placeDoor(level, doorRoom, color, random);
            placeKey(level, keyRoom, color, random);
        }
    }

    private static void placeDoor(Level level, Room room, KeyColor color, Random random) {
        Position pos = room.getRandomDoorPosition(random);
        if (pos == null) return;
        if (level.getDoorAt(pos) != null) return;

        level.addDoor(new DoorMeta(pos, color));
    }

    private static void placeKey(Level level, Room room, KeyColor color, Random random) {
        Position p = room.getRandomFreePoint(
                pos -> level.getObjectAt(pos, true) == null
        );

        Item key = ItemFactory.key(color);
        key.setPosition(p);
        level.addItem(key);
    }

    private static Map<Room, List<Room>> buildGraph(Level level) {
        Map<Room, List<Room>> abj = new HashMap<>();

        for (Corridor c : level.getCorridors()) {
            Room a = c.getFrom();
            Room b = c.getTo();

            abj.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
            abj.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
        }
        return abj;
    }

    private static Map<Room, Integer> bfsRooms(Room start, Map<Room, List<Room>> graph) {
        Map<Room, Integer> dist = new HashMap<>();
        Queue<Room> q = new ArrayDeque<>();

        dist.put(start, 0);
        q.add(start);

        while (!q.isEmpty()) {
            Room r = q.poll();
            for (Room n : graph.getOrDefault(r, List.of())) {
                if (!dist.containsKey(n)) {
                    dist.put(n, dist.get(r) + 1);
                    q.add(n);
                }
            }
        }
        return dist;
    }
}
