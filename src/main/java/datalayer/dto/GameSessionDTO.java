package datalayer.dto;

public record GameSessionDTO (
    PlayerDTO player,
    GameStatsDTO stats,
    LevelDTO level,

    long worldSeed,

    String viewMode,
    String nextLevelBalanceMode
){}
