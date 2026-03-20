package datalayer;

import domain.game.GameSession;

public interface SessionRepository {
    void save(GameSession session);
    GameSession load();
    boolean exists();
}
