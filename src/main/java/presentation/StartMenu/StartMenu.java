package presentation.StartMenu;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.List;

public class StartMenu {
    private static final String[] LOGO = {
            "██████╗  ██████╗  ██████╗ ██╗   ██╗███████╗",
            "██╔══██╗██╔═══██╗██╔════╝ ██║   ██║██╔════╝",
            "██████╔╝██║   ██║██║  ███╗██║   ██║█████╗  ",
            "██╔══██╗██║   ██║██║   ██║██║   ██║██╔══╝  ",
            "██║  ██║╚██████╔╝╚██████╔╝╚██████╔╝███████╗",
            "╚═╝  ╚═╝ ╚═════╝  ╚═════╝  ╚═════╝ ╚══════╝"
    };

    private static final List<String> ITEMS = List.of(
            "Новая игра",
            "Продолжить",
            "Таблица рекордов",
            "Выход"
    );

    private int selected = 0;

    public StartMenuAction show(Screen screen) throws IOException {
        screen.clear();
        draw(screen);

        while (true) {
            KeyStroke key = screen.readInput();

            switch (key.getKeyType()) {
                case ArrowUp -> moveUp();
                case ArrowDown -> moveDown();
                case Enter -> { return mapSelection(); }
                case Character -> {
                    char c = key.getCharacter();
                    if (c >= '1' && c <= '4') {
                        selected = c - '1';
                        return mapSelection();
                    }
                }
                case Escape -> {
                    return StartMenuAction.EXIT;
                }
            }

            draw(screen);
        }
    }

    private void moveUp() {
        selected = (selected + ITEMS.size() - 1) % ITEMS.size();
    }

    private void moveDown() {
        selected = (selected + 1) % ITEMS.size();
    }

    private void draw(Screen screen) throws IOException {
        TextGraphics g = screen.newTextGraphics();
        g.setForegroundColor(TextColor.ANSI.WHITE);

        int width = screen.getTerminalSize().getColumns();

        // LOGO
        for (int i = 0; i < LOGO.length; i++) {
            int x = (width - LOGO[i].length()) / 2;
            g.putString(x, i + 5, LOGO[i]);
        }

        // MENU
        int startY = LOGO.length + 10;
        for (int i = 0; i < ITEMS.size(); i++) {
            String prefix = (i == selected) ? "> " : "  ";
            String text = prefix + (i + 1) + ". " + ITEMS.get(i);

            int x = width / 2 - 10;
            g.putString(x, startY + i, text);
        }
        screen.refresh();
    }

    private StartMenuAction mapSelection() {
        return switch (selected) {
            case 0 -> StartMenuAction.NEW_GAME;
            case 1 -> StartMenuAction.CONTINUE;
            case 2 -> StartMenuAction.LEADERBOARD;
            case 3 -> StartMenuAction.EXIT;
            default -> throw new IllegalStateException();
        };
    }
}
