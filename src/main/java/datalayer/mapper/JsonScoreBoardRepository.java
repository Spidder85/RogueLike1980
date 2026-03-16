package datalayer.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import datalayer.dto.ScoreBoardDTO;
import datalayer.ScoreBoardRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import datalayer.dto.SessionScoreDTO;
import domain.game.SessionScore;

public class JsonScoreBoardRepository implements ScoreBoardRepository {
    private static final Path SAVE_PATH = Path.of("save", "scoreboard.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void addSession(SessionScore newScore) {
        List<SessionScore> allScores = loadAll();
        allScores.add(newScore);
        saveAll(allScores);
    }
    @Override
    public List<SessionScore> loadAll() {
        if(!Files.exists(SAVE_PATH)) {
            return new ArrayList<>();
        }
        try {
            String jsonContent = Files.readString(SAVE_PATH);
            ScoreBoardDTO wrapper = gson.fromJson(jsonContent, ScoreBoardDTO.class);
            if(wrapper == null || wrapper.sessionStats == null) {
                return new ArrayList<>();
            }
            return new ArrayList<>(wrapper.sessionStats.stream().map(SessionScoreMapper::fromDTO).toList());
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load scoreboard", e);
        }
    }

    @Override
    public void saveAll(List<SessionScore> scores) {
        try {
            List<SessionScoreDTO> dtos = scores.stream().map(SessionScoreMapper::toDTO).toList();
            ScoreBoardDTO dto = new ScoreBoardDTO(dtos);
            String jsonContent = gson.toJson(dto);
            Files.writeString(SAVE_PATH, jsonContent);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save scoreboard", e);
        }
    }
}