package datalayer.dto;

import java.util.List;

import domain.common.Position;

public record LevelDTO (
    int levelIndex,

    List<EnemyDTO> enemies,
    List<ItemDTO> items,

    Position exitPosition,
    List<DoorDTO> doors
){}
