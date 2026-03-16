package domain;

import domain.game.SessionScore;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreBoard {
    private final List<GameResult> results = new ArrayList<>();

    public void add(GameResult result){
        results.add(result);
        results.sort(
                (a, b) -> Integer.compare(b.getTreasures(), a.getTreasures())
        );
    }
    public List<GameResult> getResults(){
        return Collections.unmodifiableList(results);
    }

    public List<GameResult> getTop(int limit) {
        return results.stream().limit(limit).collect(Collectors.toList());
    }

    public static ScoreBoard fromSessionScores(List<SessionScore> scores) {
        ScoreBoard board = new ScoreBoard();
        for( SessionScore score : scores) {
            board.add(GameResult.fromSessionScore(score));
        }
        return board;
    }
}
