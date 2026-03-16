package presentation;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TabBehaviour;
import datalayer.ScoreBoardRepository;
import datalayer.SessionRepository;
import datalayer.mapper.JsonScoreBoardRepository;
import datalayer.mapper.JsonSessionRepository;
import domain.GameResult;
import domain.ScoreBoard;
import domain.character.Player;
import domain.game.GameEngine;
import domain.game.GameSession;
import domain.game.LevelManager;
import domain.game.SessionScore;
import domain.map.Level;
import presentation.StartMenu.StartMenu;
import presentation.StartMenu.StartMenuAction;
import settings.GameSettings;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import settings.RenderSettings;

import java.util.List;



public class GameApplication {

    public static void main(String[] args) throws Exception {
        Screen screen = new DefaultTerminalFactory()
                .setInitialTerminalSize(
                        new com.googlecode.lanterna.TerminalSize(
                                GameSettings.GAME_WIDTH + GameSettings.STATS_PANEL_WIDTH,
                                GameSettings.GAME_HEIGHT + GameSettings.STATUS_LOG_SIZE + 6
                        )
                )
                .createScreen();

        screen.startScreen();
        screen.setCursorPosition(null); // we don't need a cursor

        SessionRepository repo = new JsonSessionRepository();
        ScoreBoardRepository scoreRepo = new JsonScoreBoardRepository();

        GameEngine engine = null;
        LevelManager lm = new LevelManager();

        // стартовое меню
        StartMenu menu = new StartMenu();
        while (true) {
            while (engine == null) {
                StartMenuAction action = menu.show(screen);

                switch (action) {
                    case NEW_GAME -> {
                        long seed = System.currentTimeMillis();

                        Player player = new Player(
                                GameSettings.MAX_HEALTH,
                                GameSettings.INITIAL_AGILITY,
                                GameSettings.INITIAL_STRENGTH
                        );

                        lm.createLevels(seed);
                        Level firstLevel = lm.getLevel(1);

                        GameSession session = GameSession.newGame(player, firstLevel, seed);

                        engine = new GameEngine(session, lm);
                    }
                    case CONTINUE -> {
                        GameSession session = repo.load();
                        if (session == null) {
                            continue;
                        }
                        lm.createLevels(session.getWorldSeed());

                        engine = new GameEngine(session, lm);
                    }
                    case LEADERBOARD -> {
                        showLeaderboard(screen, scoreRepo);
                    }
                    case EXIT -> {
                        screen.stopScreen();
                        return;
                    }
                }
            }

            GameSession session = engine.getSession();

            LevelRenderer renderer = new LevelRenderer(screen);
            RenderSettings renderSettings = new RenderSettings();

            InventoryView inventoryView = new InventoryView(screen);
            InputHandler inputHandler = new InputHandler(engine, inventoryView, repo);

            boolean running = true;
            while (running && !session.isFinished() && !session.isGameOver()) {
                Level level = session.getCurrentLevel();

                renderer.render(level, session, renderSettings);
                screen.refresh();

                KeyStroke key = screen.readInput();
                if (key == null) continue;

                InputResult result = inputHandler.handle(key, session, renderer, renderSettings);
                if (result == InputResult.EXIT)
                    running = false;
                else if (result == InputResult.ACTION)
                    engine.nextTurn();
                else if (result == InputResult.HELP) {
                    HelpView.show(screen);
                }
            }
            if(session.isFinished() || session.isGameOver()) {
                scoreRepo.addSession(SessionScore.fromGameSession(session.getStats(), session.getPlayer()));
                settings.GameResult result;
                if(session.isFinished()) {
                    result = settings.GameResult.WIN;
                } else if(session.isGameOver()) {
                    result = settings.GameResult.LOSE;
                } else {
                    result = settings.GameResult.LOSE;
                }
                menu.drawFinalMessage(screen, result);
            }
            engine = null;
        }
        //screen.stopScreen();
    }

    private static void showLeaderboard(Screen screen, ScoreBoardRepository scoreRepo) {
        try {
            List<SessionScore> rawScores = scoreRepo.loadAll();
            ScoreBoard board = ScoreBoard.fromSessionScores(rawScores);
            List<GameResult> topScores = board.getTop(10);
            screen.clear(); // очищаем экран
            TextGraphics tg = screen.newTextGraphics();
            int y = 1;
            tg.putString(2, y++, "=== ТАБЛИЦА РЕКОРДОВ ===");
            tg.putString(2, y++, "Время завершения | Сокровища | Уровень | Враги | Еда | Эликсиры | " +
                    "Слитки | Урона нанесено | Урона получено | Шаги");
            tg.putString(2, y++, "─".repeat(113));
            for(int i = 0; i < topScores.size(); ++i) {
                GameResult res = topScores.get(i);
                SessionScore s = res.getFullStats();
                if(s == null) continue;;
                String record = String.format("%16s | %9d | %7d | %5d | %3d | %8d | %6d | %14d | %14d | %4d",
                        s.getSaveDate(), s.getTreasure(), s.getMaxLevel(), s.getEnemiesKilled(), s.getFoodPicked(),
                        s.getElixirsPicked(), s.getScrollsPicked(), s.getDamageDealt(), s.getDamageTaken(), s.getSteps());
                tg.putString(2, y++, record);
            }
            tg.putString(2, y + 2, "Нажмите любую клавишу для возврата...");
            screen.refresh(); // обновляем экран
            screen.readInput();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
