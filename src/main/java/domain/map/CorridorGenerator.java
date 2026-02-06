package domain.map;

import domain.common.Position;

import java.util.*;

public class CorridorGenerator {
    private static class Edge {
        Room a, b;
        int dist;
    }

    public static List<Corridor> generate(List<Room> rooms) {
        if (rooms.isEmpty()) return List.of();

        List<Corridor> corridors = new ArrayList<>();
        Set<Room> connected = new HashSet<>();
        List<Edge> edges = new ArrayList<>();

        Room start = rooms.stream()
                .filter(r -> r.isStart)
                .findFirst()
                .orElse(rooms.getFirst());

        connected.add(start);
        addEdges(start, rooms, connected, edges);

        while (connected.size() < rooms.size() && !edges.isEmpty()) {
            edges.sort(Comparator.comparingInt(e -> e.dist));
            Edge e = edges.removeFirst();

            Room next = null;
            if (connected.contains(e.a) && !connected.contains(e.b)) next = e.b;
            if (connected.contains(e.b) && !connected.contains(e.a)) next = e.a;

            if (next != null) {
                corridors.add(
                    new Corridor(
                        e.a.getExitPointToward(e.b),// e.a.getCenter(),
                        e.b.getExitPointToward(e.a),// e.b.getCenter(),
                        rooms,
                        e.a
                    )
                );
                connected.add(next);
                addEdges(next, rooms, connected, edges);
            }
        }
        return corridors;
    }

    private static void addEdges(Room from, List<Room> rooms, Set<Room> connected, List<Edge> edges) {
        for (Room r : rooms) {
            if (!connected.contains(r)) {
                Edge e = new Edge();
                e.a = from;
                e.b = r;
                e.dist = manhattan(from.getCenter(), r.getCenter());
                edges.add(e);
            }
        }
    }

    private static int manhattan(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}
