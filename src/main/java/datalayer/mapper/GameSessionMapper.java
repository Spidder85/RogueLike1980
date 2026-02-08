package datalayer.mapper;

import datalayer.dto.*;
import domain.character.Player;
import domain.game.GameSession;
import domain.map.Level;

public class GameSessionMapper {
    public static GameSessionDTO toDTO(GameSession session) {
        GameSessionDTO dto = new GameSessionDTO();
        dto.player = PlayerMapper.toDTO(session.getPlayer());
        dto.stats = GameStatsMapper.toDTO(session.getStats());
        dto.level = LevelMapper.toDTO(session.getCurrentLevel());
        return dto;
    }

    public static GameSession fromDTO(GameSessionDTO dto) {
        Player player = PlayerMapper.fromDTO(dto.player);

        GameSession session = new GameSession(player);

        GameStatsMapper.fromDTO(dto.stats, session.getStats());

        Level level = LevelMapper.fromDTO(dto.level);
        session.setCurrentLevel(level);

        return session;
    }
}
