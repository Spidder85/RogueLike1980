package datalayer;

import domain.game.SessionScore;

import java.util.List;

public interface ScoreBoardRepository {
    void addSession(SessionScore newScore);
    List<SessionScore> loadAll();
    void saveAll(List<SessionScore> scores);
}
