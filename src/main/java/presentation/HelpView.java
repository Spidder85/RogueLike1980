package presentation;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import settings.GameSettings;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class HelpView {
    public static void show(Screen screen) throws IOException {
        screen.clear();

        TextGraphics g = screen.newTextGraphics();

        int y = 1;

        int middleScreen = GameSettings.GAME_WIDTH / 2;
        g.putString(middleScreen-10, y++, "ОБОЗНАЧЕНИЯ НА КАРТЕ:");
        y++;
        g.putString(middleScreen-3, y++, "Карта:");
        drawString(g, 5, y++, "☻ / ▶◢▼◣◀◤▲◥ - игрок / направление взгляда игрока в FirstPerson", "☻▶◢▼◣◀◤▲◥", TextColor.ANSI.WHITE, TextColor.ANSI.CYAN);
        drawString(g, 5, y++, "╔ ═ ║ ╗ ╚ ╝ - стены комнаты", "╔═║╗╚╝", TextColor.ANSI.WHITE, TextColor.ANSI.YELLOW);
        g.putString(5, y++, "∙ - пол в текущей комнате");
        drawString(g, 5, y++, "▒ - коридор", "▒", TextColor.ANSI.WHITE, new TextColor.RGB(128, 128, 128));
        drawString(g, 5, y++, "╬ - открытый проход", "╬", TextColor.ANSI.WHITE, TextColor.ANSI.YELLOW);
        drawString(g, 5, y++, "▣ - запертая дверь", "▣", TextColor.ANSI.WHITE, TextColor.ANSI.GREEN);
        drawString(g, 5, y++, "❖ - ключ (цвет соответствует цвету отпираемой двери", "❖", TextColor.ANSI.WHITE, TextColor.ANSI.GREEN);
        drawString(g, 5, y++, "> - выход с уровня", ">", TextColor.ANSI.WHITE, TextColor.ANSI.MAGENTA);
        y++;
        g.putString(middleScreen-3, y++, "Предметы:");
        drawString(g, 5, y++, "$ - золото", "$", TextColor.ANSI.WHITE, new TextColor.RGB(255,215,0));
        g.putString(5, y++, "f - еда");
        g.putString(5, y++, "! - эликсир");
        g.putString(5, y++, "? - свиток");
        g.putString(5, y++, "/ - оружие");
        y++;
        g.putString(middleScreen-3, y++, "Враги:");
        drawString(g, 5, y++, "z - зомби", "z", TextColor.ANSI.WHITE, TextColor.ANSI.GREEN);
        drawString(g, 5, y++, "v - вампир", "v", TextColor.ANSI.WHITE, TextColor.ANSI.RED);
        g.putString(5, y++, "g - призрак");
        drawString(g, 5, y++, "O - огр", "O", TextColor.ANSI.WHITE, new TextColor.RGB(255, 165, 0));
        g.putString(5, y++, "s - змей-маг");
        g.putString(5, y++, "m - мимик");

        y+=3;
        g.setForegroundColor(new TextColor.RGB(128, 128, 128));
        g.putString(middleScreen-14, y, "Нажмите ESC для возврата...");

        try {
            screen.refresh();
        } catch (Exception ignored) {}
        KeyStroke key;
        do {
            key = screen.readInput();
        } while (key.getKeyType() != KeyType.Escape);
    }

    private static void drawString(TextGraphics g, int x, int y, String text, String symbols, TextColor baseColor, TextColor otherColor) {
        Set<Character> symbolSet = new HashSet<>();
        for (char c : symbols.toCharArray())
            symbolSet.add(c);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            g.setForegroundColor(symbolSet.contains(c) ? otherColor : baseColor);
            g.putString(x + i, y, String.valueOf(c));
        }
    }
}
