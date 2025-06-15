package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Repository.GameRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static xyz.playground.stl_web_app.Constants.StringConstants.INVALID_USER_ID;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    public Optional<Game> getGameByUuid(String uuid) {
        return gameRepository.findByUuid(uuid);
    }

    public List<Game> getUpcomingGames() {
        return gameRepository.findUpcomingGames(LocalDateTime.now());
    }

    public List<Game> getActiveGames() {
        return gameRepository.findActiveGames(LocalDateTime.now());
    }

    public Game createGame(Game game) {
        // Generate UUID if not provided
        if (game.getUuid() == null || game.getUuid().isEmpty()) {
            game.setUuid(UUID.randomUUID().toString());
        }

        // Validate that schedule date is after current date
        if (game.getScheduleDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Schedule date must be in the future");
        }

        // Validate that cut-off date is before schedule date
        if (game.getCutOffDateTime().isAfter(game.getScheduleDateTime())) {
            throw new IllegalArgumentException("Cut-off date must be before schedule date");
        }

        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game game) {

        // Validate that schedule date is after current date if not executed
        if (!game.isExecuted() && game.getScheduleDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Schedule date must be in the future for non-executed games");
        }

        // Validate that cut-off date is before schedule date
        if (game.getCutOffDateTime().isAfter(game.getScheduleDateTime())) {
            throw new IllegalArgumentException("Cut-off date must be before schedule date");
        }

        Game currentGame = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game id: " + id));
        
        currentGame.setScheduleDateTime(game.getScheduleDateTime());
        currentGame.setCutOffDateTime(game.getCutOffDateTime());
        currentGame.setEnabled(game.isEnabled());

        return gameRepository.save(currentGame);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    public Game executeGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + id));

        if (game.isExecuted()) {
            throw new IllegalStateException("Game has already been executed");
        }

        game.setExecuted(true);
        return gameRepository.save(game);
    }
}
