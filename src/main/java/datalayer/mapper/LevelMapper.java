package datalayer.mapper;

import datalayer.dto.DoorDTO;
import datalayer.dto.EnemyDTO;
import datalayer.dto.ItemDTO;
import datalayer.dto.LevelDTO;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.item.KeyColor;
import domain.map.DoorMeta;
import domain.map.Level;
import domain.map.LevelGenerator;

public class LevelMapper {

    public static LevelDTO toDTO(Level level) {

        return new LevelDTO(
            level.getIndex(),
            level.getEnemies().stream()
                    .map(EnemyMapper::toDTO)
                    .toList(),
            level.getItems().stream()
                    .map(ItemMapper::toDTO)
                    .toList(),
            level.getExitPosition(),

            level.getDoors().stream().map(d -> new DoorDTO(
                d.getPosition().x,
                d.getPosition().y,
                d.getColor().name()
            )).toList()
        );
    }

    public static Level fromDTO(LevelDTO dto, long worldSeed) {
        Level level = LevelGenerator.generate(dto.levelIndex(), worldSeed);

        level.getEnemies().clear();
        level.getItems().clear();

        for (EnemyDTO e : dto.enemies()) {
            Enemy enemy = EnemyMapper.fromDTO(e);
            level.addEnemy(enemy);
        }

        level.clearDoorsAndKeys();
        for (ItemDTO i : dto.items()) {
            level.addItem(ItemMapper.fromDTO(i));
        }

        for (DoorDTO dd : dto.doors()) {
            DoorMeta door = new DoorMeta(new Position(dd.x(),dd.y()), KeyColor.valueOf(dd.color()));

            level.addDoor(door);
        }
        return level;
    }
}
