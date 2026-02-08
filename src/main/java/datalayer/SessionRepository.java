package datalayer;

import domain.character.Player;
import domain.game.GameSession;

public interface SessionRepository {
    void save(GameSession session);
    GameSession load();
    boolean exists();
}
