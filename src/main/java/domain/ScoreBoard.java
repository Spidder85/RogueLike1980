package domain;

import java.util.*;

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
}
