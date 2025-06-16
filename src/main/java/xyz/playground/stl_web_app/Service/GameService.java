package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Repository.GameRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {


    public final String INVALID_GAME_ID = "Invalid game Id:";
    public final String ERROR_GAME_ALREADY_EXECUTED = "Game has already been executed";
    public final String ERROR_SCHEDULED_DATE_NOT_FUTURE_DATED = "Schedule date must be in the future";
    public final String ERROR_CUT_OFF_DATE_NOT_BEFORE_SCHEDULE_DATE = "Cut-off date must be before schedule date";
    public final String ERROR_EXECUTION_NOT_AFTER_SCHEDULED_DATE = "Cannot execute game before scheduled date time";


    @Autowired
    private GameRepository gameRepository;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    public List<Game> getUpcomingGames() {
        return gameRepository.findUpcomingGames(LocalDateTime.now());
    }

    public List<Game> getActiveGames() {
        return gameRepository.findActiveGames(LocalDateTime.now());
    }

    public Game createGame(Game game) {

        // Validate that schedule date is after current date
        if (game.getScheduleDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ERROR_SCHEDULED_DATE_NOT_FUTURE_DATED);
        }

        // Validate that cut-off date is before schedule date
        if (game.getCutOffDateTime().isAfter(game.getScheduleDateTime())) {
            throw new IllegalArgumentException(ERROR_CUT_OFF_DATE_NOT_BEFORE_SCHEDULE_DATE);
        }

        return gameRepository.save(game);
    }

    public Game updateGame(Long id, Game game) {

        // Validate game should be not executed yet
        if (game.isExecuted()) {
            throw new IllegalStateException(ERROR_GAME_ALREADY_EXECUTED);
        }

        // Validate that schedule date is after current date if not executed
        if (game.getScheduleDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ERROR_SCHEDULED_DATE_NOT_FUTURE_DATED);
        }

        // Validate that cut-off date is before schedule date
        if (game.getCutOffDateTime().isAfter(game.getScheduleDateTime())) {
            throw new IllegalArgumentException(ERROR_CUT_OFF_DATE_NOT_BEFORE_SCHEDULE_DATE);
        }

        Game currentGame = findGame(id);

        //TODO: Add validation if bets are already placed.

        currentGame.setScheduleDateTime(game.getScheduleDateTime());
        currentGame.setCutOffDateTime(game.getCutOffDateTime());
        currentGame.setEnabled(game.isEnabled());

        return gameRepository.save(currentGame);
    }

    public void deleteGame(Long id) {

        Game game = findGame(id);

        if (game.isExecuted()) {
            throw new IllegalStateException(ERROR_GAME_ALREADY_EXECUTED);
        }

        //TODO: Add validation if bets are already placed.

        gameRepository.deleteById(id);
    }

    public Game executeGame(Long id) {

        Game game = findGame(id);

        // Validate that execution should be after schedule date
        if (LocalDateTime.now().isBefore(game.getScheduleDateTime())) {
            throw new IllegalArgumentException(ERROR_EXECUTION_NOT_AFTER_SCHEDULED_DATE);
        }

        // Validate game is not yet executed
        if (game.isExecuted()) {
            throw new IllegalStateException(ERROR_GAME_ALREADY_EXECUTED);
        }

        game.setExecuted(true);
        game.setEnabled(false);

        return gameRepository.save(game);
    }

    public Game findGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_GAME_ID + id));
    }
}
