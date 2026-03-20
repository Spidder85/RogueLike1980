package datalayer.dto;

public record GameStatsDTO (
    int treasure,
    int enemiesKilled,
    int foodEaten,
    int elixirsDrunk,
    int scrollsRead,
    int damageDealt,
    int damageTaken,
    int steps,
    int maxLevel,
    int levelEnemiesKilled,
    int levelFoodEaten,
    int levelElixirsDrunk,
    int levelScrollsRead,
    int levelDamageDealt,
    int levelDamageTaken,
    int levelSteps
){}
