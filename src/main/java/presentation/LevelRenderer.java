package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import domain.Item;
import domain.character.Player;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.game.GameSession;
import domain.map.Corridor;
import domain.map.Level;
import domain.map.Room;

import settings.GameSettings;

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

        Player player = session.getPlayer();
        fog.computeVisibility(player, GameSettings.FOG_RADIUS, level);

        drawRooms(level, player);
        drawCorridors(level);
        drawItems(level, player);
        drawEnemies(level, player);
        drawPlayer(player);
        drawExit(level);
        stats.renderStatusLog(session.getStatusLog());

        stats.render(session);
    }

    private void drawRooms(Level level, Player player) {
        g.setForegroundColor(TextColor.ANSI.WHITE);

        for (Room room : level.getRooms()) {
            drawRoom(room, player);
        }
    }

    private void drawRoom(Room room, Player player) {
        // стены
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        for (Position p : room.walls) {
            if (!fog.wasVisited(p.x, p.y)) continue;

            if (p.x == room.x && p.y == room.y)
                g.putString(p.x, p.y, "╔");
            else if (p.x == room.x && p.y == room.y + room.height - 1)
                g.putString(p.x, p.y, "╚");
            else if (p.x == room.x + room.width - 1 && p.y == room.y)
                g.putString(p.x, p.y, "╗");
            else if (p.x == room.x + room.width - 1 && p.y == room.y + room.height - 1)
                g.putString(p.x, p.y, "╝");
            else if (p.x == room.x || p.x == room.x + room.width - 1)
                g.putString(p.x, p.y, "║");
            else if (p.y == room.y || p.y == room.y + room.height - 1)
                g.putString(p.x, p.y, "═");
            else
                g.putString(p.x, p.y, "#");
        }

        // двери
        for (Position p : room.doors) {
            if (fog.wasVisited(p.x, p.y))
                g.putString(p.x, p.y, "╬");
        }

        // пол
        if (!room.isValid(player.getPosition())) return;    // игрок не в комнате
        g.setForegroundColor(TextColor.ANSI.WHITE);
        for (int y = room.y + 1; y < room.y + room.height - 1; y++) {
            for (int x = room.x + 1; x < room.x + room.width - 1; x++) {
                g.putString(x, y, ".");
            }
        }
    }

    private void drawCorridors(Level level) {
        g.setForegroundColor(new TextColor.RGB(128, 128, 128)); // .ANSI.WHITE);

        for (Corridor corridor : level.getCorridors()) {
            for (Position p : corridor.getPath()) {
                if (!fog.wasVisited(p.x, p.y)) continue;

                g.putString(p.x, p.y, "▒"); // ░ ▒ ▓
            }
        }
    }

    private void drawEnemies(Level level, Player player) {
        Position pp = player.getPosition();

        Room playerRoom = level.findRoom(player.getPosition());

        for (Enemy enemy : level.getEnemies()) {
            Position p = enemy.getPosition();

            if (!fog.wasVisited(p.x, p.y) ) continue;

            Room enemyRoom = level.findRoom(p);

            boolean visible =
                    fog.isVisible(p.x, p.y) // в зоне прямой видимости
                    || (enemyRoom != null && enemyRoom.isValid(pp)); // в одной комнате с игроком

            if (!visible) continue;

            g.setForegroundColor(colorByEnemy(enemy));
            g.putString(p.x, p.y, charByEnemy(enemy));
        }
    }

    private void drawItems(Level level, Player player) {
        Position pp = player.getPosition();

        for (Item item : level.getItems()) {
            Position p = item.getPosition();

            // скрыто туманом
            if (!fog.wasVisited(p.x, p.y)) continue;

            boolean visible;
            Room room = level.findRoom(p);
            if(room != null) {
                // предмет в комнате → показываем только если игрок в этой же комнате
                visible = room.isValid(pp);
            } else {
                visible = fog.isVisible(p.x, p.y);
            }

            if (!visible) continue;

            g.setForegroundColor(colorByItem(item));
            g.putString(p.x, p.y, charByItem(item));
        }
    }

    private void drawExit(Level level) {
        Position p = level.getExitPosition();
        if (!fog.wasVisited(p.x, p.y)) return;

        g.setForegroundColor(TextColor.ANSI.MAGENTA);
        g.putString(p.x, p.y, ">"); // 🚪
    }

    private void drawPlayer(Player player) {
        Position p = player.getPosition();
        g.setForegroundColor(TextColor.ANSI.CYAN);
        g.putString(p.x, p.y, "☺"); // 🙂 ☺
    }

    private TextColor colorByEnemy(Enemy enemy) {
        return switch (enemy.getType()) {
            case ZOMBIE -> TextColor.ANSI.GREEN;
            case VAMPIRE -> TextColor.ANSI.RED;
            case GHOST -> TextColor.ANSI.WHITE;
            case OGRE -> TextColor.ANSI.YELLOW;
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
}
