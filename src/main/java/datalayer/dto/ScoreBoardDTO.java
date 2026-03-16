package datalayer.dto;

import java.util.List;

public class ScoreBoardDTO {
    public List<SessionScoreDTO> sessionStats;

    public ScoreBoardDTO(List<SessionScoreDTO> sessionStats) {
        this.sessionStats = sessionStats;
    }
}