package datalayer.mapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import datalayer.SessionRepository;
import datalayer.dto.GameSessionDTO;
import domain.game.GameSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonSessionRepository implements SessionRepository {
    //private static final Path SAVE_FILE = Path.of("save.json");
    private static final Path SAVE_PATH = Path.of("save", "session.json");

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public void save(GameSession session) {
        try {
            Files.createDirectories(SAVE_PATH.getParent());

            GameSessionDTO dto = GameSessionMapper.toDTO(session);
            String json = gson.toJson(dto);

            Files.writeString(SAVE_PATH, json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game", e);
        }
    }

    @Override
    public GameSession load() {
        if (!exists()) {
            throw new IllegalStateException("No save file found");
        }
        try {
            String json = Files.readString(SAVE_PATH);
            GameSessionDTO dto = gson.fromJson(json, GameSessionDTO.class);

            return GameSessionMapper.fromDTO(dto);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game", e);
        }
    }

    @Override
    public boolean exists() {
        return Files.exists(SAVE_PATH);
    }
}
