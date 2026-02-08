package datalayer.mapper;

import datalayer.dto.EnemyDTO;
import domain.enemy.Enemy;
import domain.enemy.EnemyFactory;
import domain.common.Position;
import domain.enemy.EnemyType;

public class EnemyMapper {

    public static EnemyDTO toDTO(Enemy e) {
        EnemyDTO dto = new EnemyDTO();
        dto.type = e.getType().name();
        dto.x = e.getX();
        dto.y = e.getY();
        dto.health = e.getHealth();
        dto.maxHealth = e.getMaxHealth();
        dto.agility = e.getAgility();
        dto.strength = e.getStrength();
        dto.hostility = e.getHostility();

        return dto;
    }

    public static Enemy fromDTO(EnemyDTO dto) {
        Enemy enemy = EnemyFactory.create(
                EnemyType.valueOf(dto.type),
                new Position(dto.x, dto.y));

        enemy.restore(
                dto.health,
                dto.maxHealth,
                dto.agility,
                dto.strength,
                dto.hostility,
                new Position(dto.x, dto.y)
        );

        return enemy;
    }
}