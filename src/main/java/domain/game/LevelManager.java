package domain.game;

import domain.map.Level;
import domain.map.LevelGenerator;
import settings.GameSettings;

import java.util.HashMap;
import java.util.Map;

public class LevelManager {
    private final Map<Integer, Level> levels = new HashMap<>();

    public void createLevels() {
        levels.clear();
        for(int i = 1; i <= GameSettings.MAX_LEVELS; i++) {
            Level level = LevelGenerator.generate(i);
            levels.put(i,level);
        }
    }

    public Level getLevel(int index) {
        return levels.get(index);
    }
}
