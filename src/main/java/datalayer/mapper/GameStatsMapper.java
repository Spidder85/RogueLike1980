package datalayer.mapper;

import datalayer.dto.GameStatsDTO;
import domain.game.GameStats;

public class GameStatsMapper {

    public static GameStatsDTO toDTO(GameStats s) {
        GameStatsDTO dto = new GameStatsDTO();
        dto.treasure = s.getTreasure();
        dto.enemiesKilled = s.getEnemiesKilled();
        dto.foodEaten = s.getFoodEaten();
        dto.elixirsDrunk = s.getElixirsDrunk();
        dto.scrollsRead = s.getScrollsRead();
        dto.damageDealt = s.getDamageDealt();
        dto.damageTaken = s.getDamageTaken();
        dto.steps = s.getSteps();
        dto.maxLevel = s.getMaxLevel();
        dto.levelEnemiesKilled = s.getLevelEnemiesKilled();
        dto.levelFoodEaten = s.getLevelFoodEaten();
        dto.levelElixirsDrunk = s.getLevelElixirsDrunk();
        dto.levelScrollsRead = s.getLevelScrollsRead();
        dto.levelDamageDealt = s.getLevelDamageDealt();
        dto.levelDamageTaken = s.getLevelDamageTaken();
        dto.levelSteps = s.getLevelSteps();
        return dto;
    }

    public static void fromDTO(GameStatsDTO dto, GameStats s) {
        // прямое восстановление — GameStats без инкапсуляции
        s.addTreasure(dto.treasure);
        for (int i = 0; i < dto.enemiesKilled; i++) s.enemyKilled();
        for (int i = 0; i < dto.foodEaten; i++) s.foodEaten();
        for (int i = 0; i < dto.elixirsDrunk; i++) s.elixirDrunk();
        for (int i = 0; i < dto.scrollsRead; i++) s.scrollRead();
        s.damageDealt(dto.damageDealt);
        s.damageTaken(dto.damageTaken);
        for (int i = 0; i < dto.steps; i++) s.step();
        s.reachLevel(dto.maxLevel);
        s.restoreLevelProgress(
                dto.levelEnemiesKilled,
                dto.levelFoodEaten,
                dto.levelElixirsDrunk,
                dto.levelScrollsRead,
                dto.levelDamageDealt,
                dto.levelDamageTaken,
                dto.levelSteps
        );
    }
}
