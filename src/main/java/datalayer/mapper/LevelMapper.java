package datalayer.mapper;

import datalayer.dto.DoorDTO;
import datalayer.dto.LevelDTO;
import datalayer.dto.EnemyDTO;
import datalayer.dto.ItemDTO;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.item.KeyColor;
import domain.map.DoorMeta;
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

        dto.doors = level.getDoors().stream().map(d -> {
            DoorDTO dd = new DoorDTO();
            dd.x = d.getPosition().x;
            dd.y = d.getPosition().y;
            dd.color = d.getColor().name();
            //dd.open = d.isOpen();
            return dd;
        }).toList();

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

        level.clearDoorsAndKeys();
        for (ItemDTO i : dto.items) {
            level.addItem(ItemMapper.fromDTO(i));
        }

        for (DoorDTO dd : dto.doors) {
            DoorMeta door = new DoorMeta(new Position(dd.x,dd.y), KeyColor.valueOf(dd.color));
            //if (dd.open) door.open();

            level.addDoor(door);
        }
        return level;
    }
}