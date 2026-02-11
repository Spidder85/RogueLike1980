package presentation;

import com.googlecode.lanterna.input.KeyStroke;
//import com.googlecode.lanterna.screen.Screen;
import datalayer.SessionRepository;
import domain.ItemType;
import domain.game.GameEngine;
import domain.game.GameEvent;
import domain.game.GameSession;

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

    public boolean handle(KeyStroke key, GameSession session, LevelRenderer renderer) {
        if (key.getCharacter() == null) return true;

        GameEvent e;// = null;

        switch (key.getCharacter()) {
            case 'w' -> e = engine.movePlayer(0, -1);
            case 'a' -> e = engine.movePlayer(-1, 0);
            case 's' -> e = engine.movePlayer(0, 1);
            case 'd' -> e = engine.movePlayer(1, 0);

            case 'h' -> e = useItem(session, ItemType.WEAPON);
            case 'j' -> e = useItem(session, ItemType.FOOD);
            case 'k' -> e = useItem(session, ItemType.ELIXIR);
            case 'e' -> e = useItem(session, ItemType.SCROLL);

            case 'q' -> { return false; }
            default -> { return true; }
        }

        if (e != null) {// || "exit".equals(e.getType())) {
            session.pushEvent(e);
            if (e.getType().equals("levelChanged")) {
                renderer.fog.reset();
                repository.save(session);
            }
        }
        return true;
    }

    private GameEvent useItem(GameSession session, ItemType type) {
        Integer index = inventoryView.chooseItem(session.getPlayer(), type);
        if (index != null) {
            return engine.useItem(type, index);
        }
        return null;
    }
}

