package datalayer.dto;

import domain.common.Position;

import java.util.List;

public class LevelDTO {
    public int levelIndex;

    public List<EnemyDTO> enemies;
    public List<ItemDTO> items;

    public Position exitPosition;
    public List<DoorDTO> doors;
}
