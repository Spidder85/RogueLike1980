package datalayer.dto;

public record ItemDTO (
    String type,
    String subtype,

    int health,
    int maxHealth,
    int agility,
    int strength,
    int cost,
    int duration,

    int x,
    int y,

    String keyColor
 ) {}
