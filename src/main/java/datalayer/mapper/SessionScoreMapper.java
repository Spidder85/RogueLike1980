package datalayer.mapper;

import datalayer.dto.SessionScoreDTO;
import domain.game.SessionScore;

public class SessionScoreMapper {
    public static SessionScoreDTO toDTO(SessionScore score) {
        return new SessionScoreDTO(score);
    }

    public static SessionScore fromDTO(SessionScoreDTO dto) {
        SessionScore score = new SessionScore();
        score.setTreasure(dto.getTreasure());
        score.setEnemiesKilled(dto.getEnemiesKilled());
        score.setDamageDealt(dto.getDamageDealt());
        score.setDamageTaken(dto.getDamageTaken());
        score.setSteps(dto.getSteps());
        score.setMaxLevel(dto.getMaxLevel());
        score.setFoodPicked(dto.getFoodPicked());
        score.setElixirsPicked(dto.getElixirsPicked());
        score.setScrollsPicked(dto.getScrollsPicked());
        score.setSaveDate(dto.getSaveDate());
        return score;

    }
}
