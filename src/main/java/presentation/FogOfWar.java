package presentation;

import domain.character.Player;
import domain.common.Position;
import domain.map.Level;
import settings.GameSettings;

public class FogOfWar {
    private final int width, height;

    private final boolean[][] visible;
    private final boolean[][] visited;

    public FogOfWar(int width, int height) {
        this.width = width;
        this.height = height;
        this.visible = new boolean[height][width];
        this.visited = new boolean[height][width];

        if (!GameSettings.ENABLE_FOG_OF_WAR) {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    visited[i][j] = true;
                }
            }
        }
    }

    public void clearVisible() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                visible[i][j] = false;
            }
        }
    }

    public boolean isVisible(int x, int y) {
        return inBounds(x, y) && visible[y][x];
    }

    public boolean wasVisited(int x, int y) {
        return inBounds(x, y) && visited[y][x];
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void markVisible(int x, int y) {
        if (!inBounds(x, y)) return;
        visible[y][x] = true;
        visited[y][x] = true;
    }

    // алгоритм Ray Marching
    public void computeVisibility(Player player, int radius, Level level) {
        clearVisible();

        double px = player.getX() + 0.5;
        double py = player.getY() + 0.5;

        int rays = 360;     // количество лучей
        double step = 0.5;  // шаг луча (чем меньше — тем точнее)

        for (int i = 0; i < rays; i++) {
            double angle = i * 2 * Math.PI / rays;
            double dx = Math.cos(angle);
            double dy = Math.sin(angle);

            double x = px;
            double y = py;

            for (double dist = 0; dist < radius; dist += step) {
                x += dx * step; y += dy * step;

                int cx = (int) x;
                int cy = (int) y;

                markVisible(cx, cy);
                // если упёрлись в стену — луч дальше не идёт
                if (!level.isWalkable(new Position(cx, cy))) {
                    break;
                }
            }
        }
    }

    public void reset() {
        clearVisible();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                visited[y][x] = false;
    }
}
