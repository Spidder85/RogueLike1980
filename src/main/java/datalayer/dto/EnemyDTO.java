package datalayer.dto;

public record EnemyDTO (
    String type,      // EnemyType.name()
    int x,
    int y,

    int health,
    int maxHealth,
    int agility,
    int strength,
    int hostility
){}
