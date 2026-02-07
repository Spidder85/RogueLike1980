package presentation;

import com.googlecode.lanterna.input.KeyStroke;
import domain.character.Player;
import domain.game.GameEngine;
import domain.game.GameSession;
import domain.map.Level;
import settings.GameSettings;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;


public class GameApplication {

    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(
                        new com.googlecode.lanterna.TerminalSize(
                                GameSettings.GAME_WIDTH + 40,
                                GameSettings.GAME_HEIGHT + GameSettings.STATUS_LOG_SIZE + 6
                        )
                )
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null); // we don't need a cursor

        Player player = new Player(
                GameSettings.MAX_HEALTH,
                GameSettings.INITIAL_AGILITY,
                GameSettings.INITIAL_STRENGTH
        );
        GameEngine engine = new GameEngine(player);
        engine.startNewGame();

        GameSession session = engine.getSession();

        LevelRenderer renderer = new LevelRenderer(screen);

        InventoryView inventoryView = new InventoryView(screen);
        InputHandler inputHandler = new InputHandler(engine, inventoryView);

        boolean running = true;
        while (running && !session.isFinished()) {
            Level level = session.getCurrentLevel();

            renderer.render(level, session);
            screen.refresh();

            KeyStroke key = screen.readInput();
            if (key == null) continue;

            running = inputHandler.handle(key, session, renderer);

            engine.nextTurn();
        }

        screen.stopScreen();
    }
}
