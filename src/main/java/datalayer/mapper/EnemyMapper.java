package datalayer.mapper;

import datalayer.dto.EnemyDTO;
import domain.common.Position;
import domain.enemy.Enemy;
import domain.enemy.EnemyFactory;
import domain.enemy.EnemyType;

public class EnemyMapper {

    public static EnemyDTO toDTO(Enemy e) {
        return new EnemyDTO(
            e.getType().name(),
            e.getX(),
            e.getY(),
            e.getHealth(),
            e.getMaxHealth(),
            e.getAgility(),
            e.getStrength(),
            e.getHostility()
        );
    }

    public static Enemy fromDTO(EnemyDTO dto) {
        Enemy enemy = EnemyFactory.create(
                EnemyType.valueOf(dto.type()),
                new Position(dto.x(), dto.y()));

        enemy.restore(
                dto.health(),
                dto.maxHealth(),
                dto.agility(),
                dto.strength(),
                dto.hostility(),
                new Position(dto.x(), dto.y())
        );

        return enemy;
    }
}
