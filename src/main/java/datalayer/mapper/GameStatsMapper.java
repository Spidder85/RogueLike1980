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
        return dto;
    }

    public static void fromDTO(GameStatsDTO dto, GameStats s) {
        // прямое восстановление — GameStats без инкапсуляции
        s.addTreasure(dto.treasure);
        for (int i = 0; i < dto.enemiesKilled; i++) s.enemyKilled();
        for (int i = 0; i < dto.steps; i++) s.step();
        s.reachLevel(dto.maxLevel);
    }
}