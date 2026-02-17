package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import domain.item.Item;
import domain.character.Player;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.game.GameSession;
import domain.map.Corridor;
import domain.map.DoorMeta;
import domain.map.Level;
import domain.map.Room;

import settings.GameSettings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LevelRenderer {

    private final Screen screen;
    private final TextGraphics g;
    public final FogOfWar fog;
    private final StatsPanel stats;

    //private final boolean iconMode = GameSettings.ICON_MODE;

    public LevelRenderer(Screen screen) {
        this.screen = screen;
        this.g = screen.newTextGraphics();
        this.fog = new FogOfWar(
                GameSettings.GAME_WIDTH,
                GameSettings.GAME_HEIGHT
        );
        this.stats = new StatsPanel(
                g,
                GameSettings.GAME_WIDTH + 7,
                1
        );
    }

    public void render(Level level, GameSession session) {
        screen.clear();
        if (session.isTopDown())
            renderTopDown(level, session, new Position(0,0), 1.0);
        else
            renderFirstPerson(level, session);
    }

    public void renderTopDown(Level level, GameSession session, Position offset, double scale) {
        //screen.clear();

        Player player = session.getPlayer();
        fog.computeVisibility(player, GameSettings.FOG_RADIUS, level);

        drawRooms(level, player, offset, scale);
        drawCorridors(level, offset, scale);
        drawItems(level, player, offset, scale);
        drawEnemies(level, player, offset, scale);

        String symbol = "☻";
        if (session.isFirstPerson()) {
            symbol = getArrowSymbol(player);
        }

        drawPlayer(player, offset, scale, symbol);
        drawExit(level, offset, scale);
        stats.renderStatusLog(session.getStatusLog());

        stats.render(session);
    }

    private String getArrowSymbol(Player player) {
        double angle = player.getAngle();
        int index = (int) Math.round(angle / (Math.PI / 4)) % 8;
        String symbols = "▶◢▼◣◀◤▲◥"; // ⇒⇘⇓⇙⇐⇖⇑⇗
        String symbols1 = "⇒⇘⇓⇙⇐⇖⇑⇗";
        return symbols.substring(index, index + 1);
    }

    private void drawRooms(Level level, Player player, Position offset, double scale) {
        g.setForegroundColor(TextColor.ANSI.WHITE);

        for (Room room : level.getRooms()) {
            drawRoom(room, player, level, offset, scale);
        }
    }

    private  void putScaled(Position p, String s,
                            Position offset, double scale) {
        int sx = offset.x + (int) Math.round(p.x * scale);
        int sy = offset.y + (int) Math.round(p.y * scale);

        g.putString(sx, sy, s);
    }

    private void drawRoom(Room room, Player player, Level level, Position offset, double scale) {
        // стены
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        for (Position p : room.walls) {
            if (!fog.wasVisited(p.x, p.y)) continue;

            if (p.x == room.x && p.y == room.y)
                putScaled(p, "╔", offset, scale);
                //g.putString(p.x, p.y, "╔");
            else if (p.x == room.x && p.y == room.y + room.height - 1)
                putScaled(p, "╚", offset, scale);
                //g.putString(p.x, p.y, "╚");
            else if (p.x == room.x + room.width - 1 && p.y == room.y)
                putScaled(p, "╗", offset, scale);
                //g.putString(p.x, p.y, "╗");
            else if (p.x == room.x + room.width - 1 && p.y == room.y + room.height - 1)
                putScaled(p, "╝", offset, scale);
                //g.putString(p.x, p.y, "╝");
            else if (p.x == room.x || p.x == room.x + room.width - 1)
                putScaled(p, "║", offset, scale);
                //g.putString(p.x, p.y, "║");
            else if (p.y == room.y || p.y == room.y + room.height - 1)
                putScaled(p, "═", offset, scale);
                //g.putString(p.x, p.y, "═");
            else
                putScaled(p, "#", offset, scale);
                //g.putString(p.x, p.y, "#");
        }

        // двери
        for (Position p : room.doors) {
            g.setForegroundColor(TextColor.ANSI.YELLOW);
            if (!fog.wasVisited(p.x, p.y)) continue;
            DoorMeta door = level.getDoorAt(p);
            if (door == null) {
                putScaled(p, "╬", offset, scale);
                //g.putString(p.x, p.y, "╬");
            } else {
                g.setForegroundColor(door.getColor().toColor());
                putScaled(p, "▣", offset, scale);
                //g.putString(p.x, p.y, "▣"); // ▓
            }
        }

        // пол
        if (!room.isValid(player.getPosition())) return;    // игрок не в комнате
        g.setForegroundColor(TextColor.ANSI.WHITE);
        for (int y = room.y + 1; y < room.y + room.height - 1; y++) {
            for (int x = room.x + 1; x < room.x + room.width - 1; x++) {
                putScaled(new Position(x, y), "∙", offset, scale);
                //g.putString(x, y, "∙");
            }
        }
    }

    private void drawCorridors(Level level, Position offset, double scale) {
        g.setForegroundColor(new TextColor.RGB(128, 128, 128)); // .ANSI.WHITE);

        for (Corridor corridor : level.getCorridors()) {
            for (Position p : corridor.getPath()) {
                if (!fog.wasVisited(p.x, p.y)) continue;

                putScaled(p, "▒", offset, scale);
                //g.putString(p.x, p.y, "▒"); // ░ ▒ ▓
            }
        }
    }

    private void drawEnemies(Level level, Player player, Position offset, double scale) {
        Position pp = player.getPosition();

        Room playerRoom = level.findRoom(player.getPosition());

        for (Enemy enemy : level.getEnemies()) {
            Position p = enemy.getPosition();

            if (!fog.wasVisited(p.x, p.y) ) continue;

            if (!isVisible(p, player, level) && GameSettings.ENABLE_FOG_OF_WAR) continue;

            g.setForegroundColor(colorByEnemy(enemy));
            putScaled(p, charByEnemy(enemy), offset, scale);
            //g.putString(p.x, p.y, charByEnemy(enemy));
        }
    }

    private void drawItems(Level level, Player player, Position offset, double scale) {
        for (Item item : level.getItems()) {
            Position p = item.getPosition();

            // скрыто туманом
            if (!fog.wasVisited(p.x, p.y)) continue;

            if (!isVisible(p, player, level) && GameSettings.ENABLE_FOG_OF_WAR) continue;

            g.setForegroundColor(colorByItem(item));
            putScaled(p, charByItem(item), offset, scale);
            //g.putString(p.x, p.y, charByItem(item));
        }
    }

    private boolean isVisible(Position p, Player player, Level level) {
        // 1. в зоне прямой видимости игрока
        if (fog.isVisible(p.x, p.y))
            return true;

        // 2. в одной комнате с игроком
        Room r = level.findRoom(p);
        return r != null && r.isValid(player.getPosition());
    }

    private void drawExit(Level level, Position offset, double scale) {
        Position p = level.getExitPosition();
        if (!fog.wasVisited(p.x, p.y)) return;

        g.setForegroundColor(TextColor.ANSI.MAGENTA);
        putScaled(p, ">", offset, scale);
        //g.putString(p.x, p.y, ">"); // 🚪
    }

    private void drawPlayer(Player player, Position offset, double scale, String s) {
        Position p = player.getPosition();
        g.setForegroundColor(TextColor.ANSI.CYAN);
        putScaled(p, s, offset, scale);
        //g.putString(p.x, p.y, "☻"); // 🙂 ☺
    }

    private TextColor colorByEnemy(Enemy enemy) {
        return switch (enemy.getType()) {
            case ZOMBIE -> TextColor.ANSI.GREEN;
            case VAMPIRE -> TextColor.ANSI.RED;
            case GHOST -> TextColor.ANSI.WHITE;
            case OGRE -> new TextColor.RGB(255, 165, 0); // .ANSI.YELLOW;
            case SNAKE_MAGE -> TextColor.ANSI.WHITE;
            case MIMIC -> TextColor.ANSI.WHITE;
            default -> TextColor.ANSI.WHITE;
        };
    }

    private TextColor colorByItem(Item item) {
        return switch (item.getType()) {
            case FOOD -> TextColor.ANSI.WHITE;
            case ELIXIR -> TextColor.ANSI.WHITE;
            case SCROLL -> TextColor.ANSI.WHITE;
            case WEAPON -> TextColor.ANSI.WHITE;
            case TREASURE -> new TextColor.RGB(255,215,0);
            case KEY -> item.getKeyColor().toColor();
            default -> TextColor.ANSI.WHITE;
        };
    }

    private String charByItem(Item item) {
        return switch (item.getType()) {
            case TREASURE -> "$";   // 💰
            case FOOD -> "f";       // 🍓
            case ELIXIR -> "!";     // 🧪
            case SCROLL -> "?";     // 📜
            case WEAPON -> "/";     // 🗡️
            case KEY -> "❖";        // 🔑 ▣ ▦⧆𝌴 ▍▎
        };
    }

    private String charByEnemy(Enemy enemy) {
        return switch (enemy.getType()) {
            case ZOMBIE -> "z";     // 🧟
            case VAMPIRE -> "v";    // 🦇
            case GHOST -> "g";      // 👻
            case OGRE -> "O";
            case SNAKE_MAGE -> "s"; // 🐍
            case MIMIC -> "m";
        };
    }

    private void renderFirstPerson(Level level, GameSession session) {
        //screen.clear();

        Player player = session.getPlayer();
        fog.computeVisibility(player, GameSettings.FOG_RADIUS, level);

        renderFPField(level, player);
        int x = screen.getTerminalSize().getColumns() - (int)Math.floor(GameSettings.MINIMAP_SCALE*GameSettings.GAME_WIDTH)-1;
        renderTopDown(level,session, new Position(x, 1), GameSettings.MINIMAP_SCALE);
        //renderMiniMap(level, player);
        stats.renderStatusLog(session.getStatusLog());

        stats.render(session);
    }

    private void renderFPField(Level level, Player p) {
        int screenW = screen.getTerminalSize().getColumns();    // GameSettings.GAME_WIDTH + GameSettings.STATS_PANEL_WIDTH
        int screenH = screen.getTerminalSize().getRows();       // GameSettings.GAME_HEIGHT + GameSettings.STATUS_LOG_SIZE + 6

        int viewW = GameSettings.GAME_WIDTH;
        int viewH = GameSettings.GAME_HEIGHT;

        double posX = p.getPosX();
        double posY = p.getPosY();
        double angle = p.getAngle();

        double depth = GameSettings.FOG_RADIUS;
        double fov = GameSettings.FP_FOV;

        String floorTex = " .:!/r(l1Z4H9W8$@";  // текстура пола от самого темного к самому яркому

        for (int x = 0; x < viewW; x++) {
            double rayAngle = (angle - fov / 2.0) + ((double) x / viewW) * fov;

            double eyeY = Math.sin(rayAngle);
            double eyeX = Math.cos(rayAngle);

            double distanceToWall = 0;
            boolean hitWall = false;
            boolean boundary = false;

            while (!hitWall && distanceToWall < depth) {
                distanceToWall += GameSettings.FP_STEP;

                int testX = (int) (posX + eyeX * distanceToWall);
                int testY = (int) (posY + eyeY * distanceToWall);

                //if (!level.isInside(testX, testY)) {
                if (testX < 0 || testX >= viewW || testY < 0 || testY >= viewH) {
                    hitWall = true;
                    distanceToWall = depth;
                } else if (!level.isWalkable(new Position(testX, testY))) {
                    hitWall = true;
                    boundary = isBoundary(level, posX, posY, eyeX, eyeY, testX, testY);
                }
            }
            // коррекция рыбьего глаза (fish-eye)
            double correctedDist = distanceToWall * Math.cos(rayAngle - angle);

            int ceiling = (int) (viewH / 2.0 - viewH / correctedDist);
            int floor = viewH - ceiling;

            char wallShade = getWallShade(correctedDist, depth, boundary);

            for (int y = 0; y < viewH; y++) {
                if (y <= ceiling) {
                    //draw(x, y, ' ');
                } else if (y <= floor) {
                    draw(x, y, wallShade);
                } else {
                    double b = 1.0 - ((double) y - viewH / 2.0) / (viewH / 2.0);
                    int idx = (int) ((floorTex.length() - 1) * (b - 1) * -1);

                    idx = Math.max(0, Math.min(idx, floorTex.length() - 1));

                    draw(x, y, floorTex.charAt(idx));
                }
            }
        }
    }

    private boolean isBoundary(Level level,
                            double posX, double posY,
                            double eyeX, double eyeY,
                            int testX, int testY) {
        List<double[]> corner = new ArrayList<>();
        for (int tx = 0; tx < 2; tx++) {
            for (int ty = 0; ty < 2; ty++) {
                double vx = testX + tx - posX;
                double vy = testY + ty - posY;

                double d = Math.sqrt(vx * vx + vy * vy);
                double dot = (eyeX * vx / d) + (eyeY * vy / d);

                corner.add(new double[]{d, dot});
            }
        }
        corner.sort(Comparator.comparingDouble(a -> a[0]));

        double bound = 0.005;

        return Math.acos(corner.get(0)[1]) < bound
            || Math.acos(corner.get(1)[1]) < bound;
    }

    private char getWallShade(double dist, double depth, boolean boundary) {
        if (boundary) return '|';

        if (dist <= depth / 3.0) return '█';
        if (dist <  depth / 2.0) return '▓';
        if (dist <  depth / 1.5) return '▒';
        if (dist <  depth)       return '░';
        return ' ';
    }

    private void draw(int x, int y, char c) {
        g.setCharacter(x, y, c);//new TextCharacter(c));
    }

//    private void renderMiniMap(Level level, Player player) {
//        double scale = GameSettings.MINIMAP_SCALE;
//        int offsetX = 1;
//        int offsetY = 1;
//
//        renderTopDown(level, player, scale, offsetX, offsetY)
//    }
}
