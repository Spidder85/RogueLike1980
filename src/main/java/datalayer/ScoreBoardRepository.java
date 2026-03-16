package datalayer;

import domain.game.GameStats;
import domain.game.SessionScore;

import java.io.IOException;
import java.util.List;

public interface ScoreBoardRepository {
    public void addSession(SessionScore newScore);
    List<SessionScore> loadAll();
    void saveAll(List<SessionScore> scores);
}
