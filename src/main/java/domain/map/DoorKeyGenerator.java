package domain.map;

import domain.common.Position;
import domain.item.Item;
import domain.item.ItemFactory;
import domain.item.ItemType;
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
//        List<Room> doorCandidates = level.getRooms().stream()
//                .filter(r -> dist.getOrDefault(r, 0) >= 2)
//                .toList();
        List<Room> doorCandidates = new ArrayList<>(level.getRooms().stream()
                .filter(r -> dist.getOrDefault(r, -1) >= 2)
                .toList());
        List<KeyColor> colors = new ArrayList<>(List.of(KeyColor.values()));

        Collections.shuffle(colors, random);
        Collections.shuffle(doorCandidates, random);

        Set<Room> usedKeyRooms = new HashSet<>();
        int placedPairs = 0;

//        for (int i = 0; i < pairCount && i < doorCandidates.size(); i++) {
//            Room doorRoom = doorCandidates.get(random.nextInt(doorCandidates.size()));
        for (int i = 0; i < colors.size() && placedPairs < pairCount; i++) {
            KeyColor color = colors.get(i);
            Room doorRoom = findDoorRoom(level, doorCandidates, random);
            if (doorRoom == null) {
                break;
            }

            List<Room> keyRooms = level.getRooms().stream()
                    //.filter(r -> dist.get(r) < dist.get(doorRoom))
                    .filter(r -> dist.getOrDefault(r, -1) >= 0)
                    .filter(r -> dist.getOrDefault(r, -1) < dist.getOrDefault(doorRoom, -1))
                    .filter(r -> r != doorRoom)
                    .filter(r -> !usedKeyRooms.contains(r))
                    .filter(r -> !hasKeyInRoom(level, r))
                    .toList();

            if (keyRooms.isEmpty()) continue;

            Room keyRoom = keyRooms.get(random.nextInt(keyRooms.size()));

//            if (placeDoor(level, doorRoom, color, random))
//                placeKey(level, keyRoom, color, random);
            if (!placeDoor(level, doorRoom, color, random)) {
                continue;
            }

            placeKey(level, keyRoom, color, random);
            usedKeyRooms.add(keyRoom);
            placedPairs++;
        }
    }

    private static Room findDoorRoom(Level level, List<Room> doorCandidates, Random random) {
        List<Room> available = doorCandidates.stream()
                .filter(room -> room.doors.stream().anyMatch(pos -> level.getDoorAt(pos) == null))
                .toList();
        if (available.isEmpty()) return null;
        return available.get(random.nextInt(available.size()));
    }

    private static boolean placeDoor(Level level, Room room, KeyColor color, Random random) {
        //Position pos = room.getRandomDoorPosition(random);
        List<Position> freeDoorPositions = room.doors.stream()
                .filter(pos -> level.getDoorAt(pos) == null)
                .toList();
        if (freeDoorPositions.isEmpty()) return false;

        Position pos = freeDoorPositions.get(random.nextInt(freeDoorPositions.size()));
        if (pos == null) return false;
        //if (level.getDoorAt(pos) != null) return false;

        level.addDoor(new DoorMeta(pos, color));
        return true;
    }

    private static boolean hasKeyInRoom(Level level, Room room) {
        return level.getItems().stream()
                .filter(item -> item.getType() == ItemType.KEY)
                .map(Item::getPosition)
                .map(level::findRoom)
                .anyMatch(room::equals);
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
