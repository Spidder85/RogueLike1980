package presentation;


import domain.Level;
import domain.character.Character;
import jcurses.system.CharColor;
import jcurses.system.InputChar;
import jcurses.system.Toolkit;

import javax.tools.Tool;

public class GameScreen {

    private static final  CharColor BG = new CharColor(CharColor.BLACK, CharColor.BLACK);

    private boolean running = true;

    private final Level level;
    private final Character player;

    public GameScreen(Level level, Character player) {
        this.level = level;
        this.player = player;
    }

    public void run() {
        while (running) {
            draw();
            handleInput();
        }
    }

    // INPUT
    private void handleInput() {
        InputChar ch = Toolkit.readCharacter();
        if (ch == null) return;

        char c = ch.getCharacter();

        switch (c) {
            case 'q' -> running = false;
            case 'w' -> movePlayer(0, -1);
            case 's' -> movePlayer(0, 1);
            case 'a' -> movePlayer(-1, 0);
            case 'd' -> movePlayer(1, 0);
        }
    }

    private void movePlayer(int dx, int dy) {
        if (!player.canAct()) return;

        int nx = player.getX() + dx;
        int ny = player.getY() + dy;

        if (level.canMoveTo(nx, ny)) {
            player.move(dx, dy);
        }
    }

    // render
    private void draw() {
        Toolkit.clearScreen(BG);

        drawPlayer();
        drawUI();
    }

    private void drawPlayer() {
        Toolkit.printString(
            "@",
            player.getX(),
                player.getY(),
                new CharColor(CharColor.YELLOW, CharColor.BLACK)
        );
    }

    private void drawUI() {
        Toolkit.printString(
                "HP: " + player.getHealth() + "/" + player.getMaxHealth(),
                1,
                1,
                new CharColor(CharColor.WHITE, CharColor.BLACK)
        );
        Toolkit.printString(
                "WASD - move | q - quit",
                1,
                2,
                new CharColor(CharColor.WHITE, CharColor.BLACK)
        );
    }
}
