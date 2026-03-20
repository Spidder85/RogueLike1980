package datalayer.mapper;

import datalayer.dto.*;
import domain.character.Player;
import domain.game.BalanceMode;
import domain.game.GameSession;
import domain.map.Level;
import domain.view.ViewMode;

public class GameSessionMapper {
    public static GameSessionDTO toDTO(GameSession session) {
        return new GameSessionDTO(
            PlayerMapper.toDTO(session.getPlayer()),
            GameStatsMapper.toDTO(session.getStats()),
            LevelMapper.toDTO(session.getCurrentLevel()),
            session.getWorldSeed(),
            session.getViewMode().name(),
            session.getNextLevelBalanceMode().name()
        );
    }

    public static GameSession fromDTO(GameSessionDTO dto) {
        Player player = PlayerMapper.fromDTO(dto.player());

        GameSession session = new GameSession(player);
        session.setWorldSeed(dto.worldSeed());

        Level level = LevelMapper.fromDTO(dto.level(), dto.worldSeed());
        session.setCurrentLevel(level);
        GameStatsMapper.fromDTO(dto.stats(), session.getStats());

        if (dto.viewMode() != null) {
            session.setViewMode(ViewMode.valueOf(dto.viewMode()));
        }
        if (dto.nextLevelBalanceMode() != null) {
            session.setNextLevelBalanceMode(BalanceMode.valueOf(dto.nextLevelBalanceMode()));
        }

        return session;
    }
}
