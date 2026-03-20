package datalayer.dto;

public record PlayerDTO (
    int maxHealth,
    int health,
    int agility,
    int strength,

    ItemDTO currentWeapon,

    int x,
    int y,

    BackpackDTO backpack
){}
