package presentation;

import com.googlecode.lanterna.input.KeyStroke;
//import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.input.KeyType;
import datalayer.SessionRepository;
import domain.item.ItemType;
import domain.game.GameEngine;
import domain.game.GameEvent;
import domain.game.GameSession;
import settings.GameSettings;

public class InputHandler {
    private final GameEngine engine;
    private final InventoryView inventoryView;

    private final SessionRepository repository;

    public InputHandler(GameEngine engine,
                        InventoryView inventoryView,
                        SessionRepository repository) {
        this.engine = engine;
        this.inventoryView = inventoryView;
        this.repository = repository;
    }

    public InputResult handle(KeyStroke key, GameSession session, LevelRenderer renderer) {
        if (key.getKeyType() == KeyType.F11) {
            session.toggleViewMode();

            return InputResult.NO_ACTION;
        }
//        if (key.isAltDown() && key.isCtrlDown() && key.getCharacter() == 'f') {
//            GameSettings.switchFOG();
//
//            return  InputResult.NO_ACTION;
//        }
        Character ch = key.getCharacter();
        if (ch == null || ch == '\n' || ch == '\r') return InputResult.NO_ACTION;

        GameEvent e = null;
        boolean fp = session.isFirstPerson();

        switch (key.getCharacter()) {
            // движение
            case 'w' -> e = fp ? engine.moveFP(1, 0) : engine.movePlayer(0, -1, true);
            case 's' -> e = fp ? engine.moveFP(-1, 0) : engine.movePlayer(0, 1, true);

            case 'a' -> {
                if (fp) engine.rotatePlayer(-GameSettings.FP_ROTATION);
                else e = engine.movePlayer(-1, 0, true);
            }

            case 'd' -> {
                if (fp) engine.rotatePlayer(GameSettings.FP_ROTATION);
                else e = engine.movePlayer(1, 0, true);
            }
            // стрейфы только в FP
            case 'z' -> {
                if (fp) e = engine.moveFP(0, -1);
                else return InputResult.NO_ACTION;
            }
            case 'x' -> {
                if (fp) e = engine.moveFP(0, 1);
                else return InputResult.NO_ACTION;
            }
            // предметы
            case 'h' -> e = useItem(session, ItemType.WEAPON);
            case 'j' -> e = useItem(session, ItemType.FOOD);
            case 'k' -> e = useItem(session, ItemType.ELIXIR);
            case 'e' -> e = useItem(session, ItemType.SCROLL);

            case 'q' -> { return InputResult.EXIT; }
            default -> { return InputResult.NO_ACTION; }
        }

        if (e != null) {
            session.pushEvent(e);
            if (e.getType().equals("levelChanged")) {
                if (GameSettings.ENABLE_FOG_OF_WAR)
                    renderer.fog.reset();
                repository.save(session);
            }
        }
        return InputResult.ACTION;
    }

    private GameEvent useItem(GameSession session, ItemType type) {
        Integer index = inventoryView.chooseItem(session.getPlayer(), type);
        if (index != null) {
            return engine.useItem(type, index);
        }
        return null;
    }
}

