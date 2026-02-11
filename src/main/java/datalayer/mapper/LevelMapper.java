package datalayer.mapper;

import datalayer.dto.LevelDTO;
import datalayer.dto.EnemyDTO;
import datalayer.dto.ItemDTO;
import domain.enemy.Enemy;
import domain.map.Level;
import domain.map.LevelGenerator;

public class LevelMapper {

    public static LevelDTO toDTO(Level level) {
        LevelDTO dto = new LevelDTO();
        dto.levelIndex = level.getIndex();
        dto.enemies = level.getEnemies().stream()
                .map(EnemyMapper::toDTO)
                .toList();
        dto.items = level.getItems().stream()
                .map(ItemMapper::toDTO)
                .toList();
        dto.exitPosition = level.getExitPosition();
        return dto;
    }

    public static Level fromDTO(LevelDTO dto, long worldSeed) {
        Level level = LevelGenerator.generate(dto.levelIndex, worldSeed);

        level.getEnemies().clear();
        level.getItems().clear();

        for (EnemyDTO e : dto.enemies) {
            Enemy enemy = EnemyMapper.fromDTO(e);
            level.addEnemy(enemy);
        }

        for (ItemDTO i : dto.items) {
            level.addItem(ItemMapper.fromDTO(i));
        }

        return level;
    }
}