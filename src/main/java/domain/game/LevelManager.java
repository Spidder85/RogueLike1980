package domain.game;

import domain.map.Level;
import domain.map.LevelGenerator;
import settings.GameSettings;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {
    private final Map<Integer, Level> levels = new HashMap<>();
    private long worldSeed;

    public void createLevels(long worldSeed) {
        levels.clear();
        this.worldSeed = worldSeed;
        levels.put(1, LevelGenerator.generate(1, worldSeed, BalanceMode.NORMAL));
    }

    public Level getLevel(int index) {
        return levels.get(index);
    }

    public Level getOrCreateLevel(int index, BalanceMode balanceMode) {
        return levels.computeIfAbsent(
                index,
                levelIndex -> LevelGenerator.generate(levelIndex, worldSeed, balanceMode)
        );
    }
}
