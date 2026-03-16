package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import domain.enemy.impl.Ghost;
import domain.enemy.impl.Mimic;
import domain.item.Item;
import domain.character.Player;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.game.GameSession;
import domain.item.ItemType;
import domain.map.Corridor;
import domain.map.DoorMeta;
import domain.map.Level;
import domain.map.Room;

import presentation.Sprite.AsciiSprite;
import presentation.Sprite.SpriteEditor;
import settings.GameSettings;
import settings.RenderSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LevelRenderer {

    private final Screen screen;
    private final TextGraphics g;
    public final FogOfWar fog;
    private final StatsPanel stats;

    private final AsciiSprite zombieSprite = new AsciiSprite("assets/zombie.spr");
    private final AsciiSprite vampireSprite = new AsciiSprite("assets/vampire.spr");
    private final AsciiSprite ghostSprite = new AsciiSprite("assets/ghost.spr");
    private final AsciiSprite ogreSprite = new AsciiSprite("assets/ogre.spr");
    private final AsciiSprite snakeMageSprite = new AsciiSprite("assets/snakemage.spr");
    private final AsciiSprite mimicSprite = new AsciiSprite("assets/mimic.spr");
    private final AsciiSprite goldSprite = new AsciiSprite("assets/gold.spr");
    private final AsciiSprite foodSprite = new AsciiSprite("assets/food.spr");
    private final AsciiSprite elixirSprite = new AsciiSprite("assets/elixir.spr");
    private final AsciiSprite scrollSprite = new AsciiSprite("assets/scroll.spr");
    private final AsciiSprite weaponSprite = new AsciiSprite("assets/weapon.spr");
    private final AsciiSprite keySprite = new AsciiSprite("assets/key.spr");
    private final AsciiSprite exitSprite = new AsciiSprite("assets/exit.spr");


    //private final boolean iconMode = GameSettings.ICON_MODE;

    public LevelRenderer(Screen screen) throws IOException {
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

    public void render(Level level, GameSession session, RenderSettings renderSettings) {
        screen.clear();
        boolean revealAll = !GameSettings.ENABLE_FOG_OF_WAR || renderSettings.isDebugRevealAll();
        fog.setDebugRevealAll(revealAll);
        if (session.isTopDown())
            renderTopDown(level, session, new Position(0,0), 1.0, renderSettings);
        else
            renderFirstPerson(level, session, renderSettings);
    }

    public void renderTopDown(Level level, GameSession session, Position offset, double scale, RenderSettings renderSettings) {
        Player player = session.getPlayer();
        fog.computeVisibility(player, GameSettings.FOG_RADIUS, level);

        drawRooms(level, player, offset, scale);
        drawCorridors(level, offset, scale);
        drawDoors(level, offset, scale);
        drawItems(level, player, offset, scale, renderSettings);
        drawEnemies(level, player, offset, scale, renderSettings);

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
        double angle = player.getAngleRadian();
        int index = (int) Math.round(angle / (Math.PI / 4)) % 8;
        String symbols = "▶◢▼◣◀◤▲◥"; // ⇒⇘⇓⇙⇐⇖⇑⇗
        //String symbols1 = "⇒⇘⇓⇙⇐⇖⇑⇗";
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
        // пол
        if (room.isValid(player.getPosition())) {    // игрок в комнате
            g.setForegroundColor(TextColor.ANSI.WHITE);
            for (int y = room.y + 1; y < room.y + room.height - 1; y++) {
                for (int x = room.x + 1; x < room.x + room.width - 1; x++) {
                    putScaled(new Position(x, y), "∙", offset, scale);
                }
            }
        }

        // стены
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        for (Position p : room.walls) {
            if (!fog.wasVisited(p.x, p.y)) continue;

            if (p.x == room.x || p.x == room.x + room.width - 1)
                putScaled(p, "║", offset, scale);
            else if (p.y == room.y || p.y == room.y + room.height - 1)
                putScaled(p, "═", offset, scale);
        }
        int x1 = room.x, x2 = room.x + room.width - 1;
        int y1 = room.y, y2 = room.y + room.height - 1;

        if (fog.wasVisited(x1, y1)) putScaled(new Position(x1,y1), "╔", offset, scale);
        if (fog.wasVisited(x2, y1)) putScaled(new Position(x2,y1), "╗", offset, scale);
        if (fog.wasVisited(x1, y2)) putScaled(new Position(x1,y2), "╚", offset, scale);
        if (fog.wasVisited(x2, y2)) putScaled(new Position(x2,y2), "╝", offset, scale);
    }

    private void drawDoors(Level level, Position offset, double scale) {
        // двери
        for (Room room : level.getRooms()) {
            for (Position p : room.doors) {
                if (!fog.wasVisited(p.x, p.y)) continue;

                DoorMeta door = level.getDoorAt(p);
                if (door == null) {
                    g.setForegroundColor(TextColor.ANSI.YELLOW);
                    putScaled(p, "╬", offset, scale);
                } else {
                    g.setForegroundColor(door.getColor().toColor());
                    putScaled(p, "▣", offset, scale);
                }
            }
        }
    }

    private void drawCorridors(Level level, Position offset, double scale) {
        g.setForegroundColor(new TextColor.RGB(128, 128, 128)); // .ANSI.WHITE);

        for (Corridor corridor : level.getCorridors()) {
            for (Position p : corridor.getPath()) {
                if (!fog.wasVisited(p.x, p.y)) continue;

                putScaled(p, "▒", offset, scale);   // ░ ▒ ▓
            }
        }
    }

    private void drawEnemies(Level level, Player player, Position offset, double scale, RenderSettings renderSettings) {
        boolean hidByFog = GameSettings.ENABLE_FOG_OF_WAR && !renderSettings.isDebugRevealAll();

        for (Enemy enemy : level.getEnemies()) {
            Position p = enemy.getPosition();

            if (!fog.wasVisited(p.x, p.y) ) continue;

            if (hidByFog && !isVisible(p, player, level)) continue;
            if (enemy instanceof Ghost ghost && ghost.isInvisible()) continue;
            if (enemy instanceof Mimic mimic && mimic.isDisguised()) {
                drawItem(mimic.getDisguiseItem(), p, offset, scale);
                continue;
            }
            g.setForegroundColor(colorByEnemy(enemy));
            putScaled(p, charByEnemy(enemy), offset, scale);
        }
    }

    private void drawItems(Level level, Player player, Position offset, double scale, RenderSettings renderSettings) {
        boolean hidByFog = GameSettings.ENABLE_FOG_OF_WAR && !renderSettings.isDebugRevealAll();
        for (Item item : level.getItems()) {
            Position p = item.getPosition();

            // скрыто туманом
            if (!fog.wasVisited(p.x, p.y)) continue;

            if (hidByFog && !isVisible(p, player, level)) continue;

            drawItem(item, p, offset, scale);
        }
    }

    private void drawItem(Item item, Position p, Position offset, double scale) {
        g.setForegroundColor(colorByItem(item));
        putScaled(p, charByItem(item), offset, scale);

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
        putScaled(p, ">", offset, scale);   // "⌂" / "▛" / "▲" / "≋"
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

    private void renderFirstPerson(Level level, GameSession session, RenderSettings renderSettings) {
        Player player = session.getPlayer();
        fog.computeVisibility(player, GameSettings.FOG_RADIUS, level);

        renderFPField(level, player);
        renderFPExit(level, player);
        renderFPItems(level, player);
        renderFPEnemies(level, player);

        int x = screen.getTerminalSize().getColumns() - (int)Math.floor(GameSettings.MINIMAP_SCALE*GameSettings.GAME_WIDTH)-1;
        renderTopDown(level,session, new Position(x, 1), GameSettings.MINIMAP_SCALE, renderSettings);

        stats.renderStatusLog(session.getStatusLog());
        stats.render(session);
    }

    private void renderFPField(Level level, Player p) {
        int viewW = GameSettings.GAME_WIDTH;
        int viewH = GameSettings.GAME_HEIGHT;

        double posX = p.getPosX();
        double posY = p.getPosY();
        double angle = p.getAngleRadian();

        int depth = GameSettings.FOG_RADIUS;
        double fov = GameSettings.FP_FOV;

        String floorTex = " .:!/r(l1Z4H9W8$@";  // текстура пола от самого темного к самому яркому

        int depthCeiling = (int) (viewH / 2.0 - (double) viewH / depth);
        int depthFloor = viewH - depthCeiling;

        for (int x = 0; x < viewW; x++) {
            double rayAngle = (angle - fov / 2.0) + ((double) x / viewW) * fov;

            double eyeY = Math.sin(rayAngle);
            double eyeX = Math.cos(rayAngle);

            double distanceToWall = 0.0;
            boolean hitWall = false;    // Устанавливается, когда луч попадает в блок стены
            boolean boundary = false;   // Устанавливается, когда луч попадает в границу между двумя блоками стены
            DoorMeta door = null;

            while (!hitWall && distanceToWall < depth) {
                distanceToWall += GameSettings.FP_STEP;

                int testX = (int) (posX + eyeX * distanceToWall);
                int testY = (int) (posY + eyeY * distanceToWall);

                Position testPos = new Position(testX, testY);

                if (testX < 0 || testX >= viewW || testY < 0 || testY >= viewH) {
                    hitWall = true;
                    distanceToWall = depth;
                } else if (!level.isWalkable(testPos)) {
                    hitWall = true;
                    boundary = isBoundary(level, posX, posY, eyeX, eyeY, testX, testY);
                    door = level.getDoorAt(testPos);
                }
            }

            // коррекция рыбьего глаза (fish-eye)
            double correctedDist = distanceToWall * Math.cos(rayAngle - angle);
            if (correctedDist <= 0.0001) correctedDist = 0.0001;

            for (int y = 0; y < viewH; y++) {
                depthBuffer[y][x] = correctedDist;
            }

            int ceiling = (int) (viewH / 2.0 - viewH / correctedDist);
            int floor = viewH - ceiling;

            char wallShade = getWallShade(correctedDist, depth, boundary);
            TextColor wallColor = TextColor.ANSI.WHITE;

            if (door != null) {
                wallColor = door.getColor().toColor();
            }

            for (int y = 0; y < viewH; y++) {
                if (y <= ceiling) {
                    // пустота / потолок - не рисуем
                } else if (y <= floor) {    // если стена
                    draw(x, y, wallShade, wallColor);
                } else {    // если пол
                    double t = (double) (y - depthFloor) / (viewH - 1 - depthFloor);
                    int idx = (int) (t * (floorTex.length() -1));
                    idx = Math.max(0, Math.min(idx, floorTex.length() -1));

                    draw(x, y, floorTex.charAt(idx), TextColor.ANSI.WHITE);
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
                if (d <= 1e-9) {
                    continue;
                }
                double dot = (eyeX * vx / d) + (eyeY * vy / d);
                dot = Math.max(-1.0, Math.min(1.0, dot));

                corner.add(new double[]{d, dot});
            }
        }
        corner.sort(Comparator.comparingDouble(a -> a[0]));
        if (corner.size() < 2) {
            return false;
        }

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

    private void draw(int x, int y, char c, TextColor color) {
        g.setForegroundColor(color);
        g.setCharacter(x, y, c);//new TextCharacter(c));
    }

    private void renderFPItems(Level level, Player player) {
        for (Item item : level.getItems()) {
            Position ip = item.getPosition();

            // скрыто туманом
            //if (!fog.wasVisited(ip.x, ip.y)) continue;
            if (!isVisible(ip, player, level)) continue;

            AsciiSprite sprite = spriteByItem(item);
            TextColor color =
                    item.getType() == ItemType.KEY
                            ? item.getKeyColor().toColor()
                            : null;
            renderObjectSprite(player, ip, sprite, 0.4, color, true);
        }
    }

    private void renderFPEnemies(Level level, Player player) {
        for (Enemy enemy : level.getEnemies()) {
            Position ep = enemy.getPosition();

            //if (!fog.isVisible(ep.x, ep.y)) continue;
            if (!isVisible(ep, player, level)) continue;
            if (enemy instanceof Ghost ghost && ghost.isInvisible()) continue;

            if (enemy instanceof Mimic mimic && mimic.isDisguised()) {
                Item item = mimic.getDisguiseItem();
                AsciiSprite sprite = spriteByItem(item);
                TextColor color =
                        item.getType() == ItemType.KEY
                                ? item.getKeyColor().toColor()
                                : null;
                renderObjectSprite(player, ep, sprite, 0.5, color, false);
                continue;
            }
            AsciiSprite sprite = spriteByEnemy(enemy);
            boolean isCenter = enemy instanceof Ghost;
            renderObjectSprite(player, ep, sprite,0.8, null, isCenter);
        }
    }

    private void renderObjectSprite(Player player, Position objPos, AsciiSprite sprite, double scale, TextColor color, boolean isCenter) {
        Position pp = player.getPosition();

        int viewW = GameSettings.GAME_WIDTH;
        int viewH = GameSettings.GAME_HEIGHT;

        int depth = GameSettings.FOG_RADIUS;
        double fov = GameSettings.FP_FOV;

        double dx = objPos.dX - pp.dX;
        double dy = objPos.dY - pp.dY;

        double distance = Math.sqrt(dx*dx + dy*dy);

        double angle  = player.getAngleRadian();

        double objAngle = Math.atan2(dy, dx) - angle;

        while (objAngle < -Math.PI) objAngle += 2 * Math.PI;
        while (objAngle >  Math.PI) objAngle -= 2 * Math.PI;

        if (Math.abs(objAngle) > fov / 2.0
                || distance < 0.2 || distance >= depth)
            return;

        double correctedDist = distance * Math.cos(objAngle);

        double fullHeight = 2 * viewH / correctedDist;  // полная высота объекта
        double objHeight = fullHeight * scale ;  // желаемая высота предмета (0,5 от полной)
        // предмет стоит на полу

        double objCeiling;
        if (isCenter) {
            objCeiling = viewH / 2.0 - objHeight / 2.0;
        } else {
            double objFloor = viewH / 2.0 + viewH / correctedDist;
            objCeiling = objFloor - objHeight;
        }

        double aspect = (double) sprite.height / sprite.width;
        double objWidth = objHeight / aspect;

        double middle =
                (0.5 * (objAngle / (fov / 2.0)) + 0.5) * viewW;

        for (int lx = 0; lx < objWidth; lx++) {
            int column = (int) (middle + lx - objWidth / 2.0);
            if (column < 0 || column >= viewW) continue;

            //if (depthBuffer[column] < correctedDist) continue;

            for (int ly = 0; ly < objHeight; ly++) {
                int row = (int) (objCeiling + ly);
                if (row < 0 || row >= viewH) continue;

                if (depthBuffer[row][column] < correctedDist) continue;
                double sampleX = (double) lx / objWidth;
                double sampleY = (double) ly / objHeight;

                char glyph = sprite.sampleGlyph(sampleX, sampleY);
                if (glyph == ' ') continue;

                TextColor colDraw = color != null
                        ? color
                        : SpriteEditor.mapColor(sprite.sampleFG(sampleX, sampleY));
                g.setForegroundColor(colDraw);
                g.setCharacter(column, row, glyph);

                depthBuffer[row][column] = correctedDist;
            }
        }
    }

    private void renderFPExit(Level level, Player player) {
        Position ep = level.getExitPosition();
        if (ep == null) return;

        //if (!fog.isVisible(ep.x, ep.y)) return;
        if (!isVisible(ep, player, level)) return;

        renderObjectSprite(player, ep, exitSprite, 0.5, null, true);
    }

    private final double[][] depthBuffer = new double[GameSettings.GAME_HEIGHT][GameSettings.GAME_WIDTH];

    private AsciiSprite spriteByEnemy(Enemy enemy) {
        return switch (enemy.getType()) {
            case ZOMBIE -> zombieSprite;
            case VAMPIRE -> vampireSprite;
            case GHOST -> ghostSprite;
            case OGRE -> ogreSprite;
            case SNAKE_MAGE -> snakeMageSprite;
            case MIMIC -> mimicSprite;
        };
    }

    private AsciiSprite spriteByItem(Item item) {
        return switch (item.getType()) {
            case TREASURE -> goldSprite;
            case FOOD -> foodSprite;
            case ELIXIR -> elixirSprite;
            case SCROLL -> scrollSprite;
            case WEAPON -> weaponSprite;
            case KEY -> keySprite;
        };
    }
}
