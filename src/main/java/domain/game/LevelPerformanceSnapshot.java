package domain.game;

public record LevelPerformanceSnapshot(
        int damageTaken,
        int damageDealt,
        int enemiesKilled,
        int foodEaten,
        int elixirsDrunk,
        int scrollsRead,
        int steps
) {
}
