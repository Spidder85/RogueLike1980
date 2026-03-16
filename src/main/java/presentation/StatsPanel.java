package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import domain.item.Item;
import domain.item.ItemType;
import domain.game.GameSession;
import domain.item.KeyColor;
import settings.GameSettings;

public class StatsPanel {
    private final TextGraphics g;
    private final int x;
    private final int y;

    public StatsPanel(TextGraphics g, int x, int y) {
        this.g = g;
        this.x = x;
        this.y = y;
    }

    public void renderStatusLog(StatusLog log) {
        int y = GameSettings.GAME_HEIGHT + 3;
        g.setForegroundColor(new TextColor.RGB(127,127,127));

        g.putString(0, y++, "  ────────────────────────────────────────");
        for (String line : log.getLines()) {
            g.putString(2, y++, line);
        }
    }

    public void render(GameSession session) {
        int yy = session.isTopDown()? y : (int)Math.floor(GameSettings.GAME_HEIGHT * GameSettings.MINIMAP_SCALE + 1);
        g.setForegroundColor(new TextColor.RGB(127,127,127));
        g.putString(x, yy++, "=== СТАТИСТИКА ===");
        //g.putString(x, yy++, "Сокровища: " + session.getStats().getTreasure());
        g.putString(x, yy++, "Уровень: " + session.getStats().getMaxLevel());
        g.putString(x, yy++, "Врагов: " + session.getStats().getEnemiesKilled());
        g.putString(x, yy++, "Еда: " + session.getStats().getFoodEaten());
        g.putString(x, yy++, "Эликсиры: " + session.getStats().getElixirsDrunk());
        g.putString(x, yy++, "Свитки: " + session.getStats().getScrollsRead());
        g.putString(x, yy++, "Шаги: " + session.getStats().getSteps());

        yy++;
        g.putString(x, yy++, "=== Игрок ===");
        g.putString(x, yy++, "Здоровье: " + session.getPlayer().getHealth() + "/" + session.getPlayer().getMaxHealth());
        g.putString(x, yy++, "Ловкость: " + session.getPlayer().getAgility());
        g.putString(x, yy++, "Сила: " + session.getPlayer().getStrength());
        g.putString(x, yy++, "Оружие: " + Item.toString(session.getPlayer().getCurrentWeapon()));
        //g.putString(x, yy++, "Angle: " + session.getPlayer().getAngleDegree() + "°");

        yy++;
        g.putString(x, yy++, "=== Инвентарь ===");
        g.putString(x, yy++, "Еда: " + session.getPlayer().getBackpack().getItems(ItemType.FOOD).size());
        g.putString(x, yy++, "Эликсиры: " + session.getPlayer().getBackpack().getItems(ItemType.ELIXIR).size());
        g.putString(x, yy++, "Свитки: " + session.getPlayer().getBackpack().getItems(ItemType.SCROLL).size());
        g.putString(x, yy++, "Оружие: " + session.getPlayer().getBackpack().getItems(ItemType.WEAPON).size());
        g.putString(x, yy++, "Золото: " + session.getPlayer().getBackpack().getTreasureAmount());

        if (GameSettings.ENABLE_KEY) {
            String s = "Ключи: ";
            g.putString(x, yy, s);
            getKeyMaskString(session.getPlayer().getBackpack().getKeyMask(), x + s.length(), yy++);
        }

        yy++;
        g.putString(x, yy++, "=== Меню ===");
        g.putString(x, yy++,"Управление:");
        if (session.isTopDown()) {
            g.putString(x, yy++, "W/A/S/D - Движение");
        } else {
            g.putString(x, yy++, "W/A - Вперед/Назад");
            g.putString(x, yy++, "D/S - Повернуть налево/направо");
            g.putString(x, yy++, "Z/X - Шаг в лево/право");
        }
        g.putString(x, yy++,"H - Использовать оружие");
        g.putString(x, yy++,"J - Использовать еду");
        g.putString(x, yy++,"K - Использовать эликсир");
        g.putString(x, yy++,"E - Использовать свиток");
        g.putString(x, yy++,"F11 - Сменить вид");
        g.putString(x, yy++,"Q - Выход");


    }
    private void getKeyMaskString(int keyMask, int x, int y) {
        if (keyMask != 0) {
            for (KeyColor key : KeyColor.values()) {
                if ((keyMask & key.bit()) != 0) {
                    g.setForegroundColor(key.toColor());
                    g.putString(x, y, "❖");
                    x += 2;
                }
            }
            g.setForegroundColor(new TextColor.RGB(127, 127, 127));
        }
    }
}
