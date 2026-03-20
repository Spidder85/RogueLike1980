package datalayer.mapper;

import datalayer.dto.GameStatsDTO;
import domain.game.GameStats;

public class GameStatsMapper {

    public static GameStatsDTO toDTO(GameStats s) {
        return new GameStatsDTO(
            s.getTreasure(),
            s.getEnemiesKilled(),
            s.getFoodEaten(),
            s.getElixirsDrunk(),
            s.getScrollsRead(),
            s.getDamageDealt(),
            s.getDamageTaken(),
            s.getSteps(),
            s.getMaxLevel(),
            s.getLevelEnemiesKilled(),
            s.getLevelFoodEaten(),
            s.getLevelElixirsDrunk(),
            s.getLevelScrollsRead(),
            s.getLevelDamageDealt(),
            s.getLevelDamageTaken(),
            s.getLevelSteps()
        );
    }

    public static void fromDTO(GameStatsDTO dto, GameStats s) {
        // прямое восстановление — GameStats без инкапсуляции
        s.addTreasure(dto.treasure());
        for (int i = 0; i < dto.enemiesKilled(); i++) s.enemyKilled();
        for (int i = 0; i < dto.foodEaten(); i++) s.foodEaten();
        for (int i = 0; i < dto.elixirsDrunk(); i++) s.elixirDrunk();
        for (int i = 0; i < dto.scrollsRead(); i++) s.scrollRead();
        s.damageDealt(dto.damageDealt());
        s.damageTaken(dto.damageTaken());
        for (int i = 0; i < dto.steps(); i++) s.step();
        s.reachLevel(dto.maxLevel());
        s.restoreLevelProgress(
                dto.levelEnemiesKilled(),
                dto.levelFoodEaten(),
                dto.levelElixirsDrunk(),
                dto.levelScrollsRead(),
                dto.levelDamageDealt(),
                dto.levelDamageTaken(),
                dto.levelSteps()
        );
    }
}
