package xyz.playground.stl_web_app.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.Action;
import xyz.playground.stl_web_app.Constants.GameStatus;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Repository.GameRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameService {

    public final String INVALID_GAME_ID = "Invalid game Id:";
    public final String ERROR_SCHEDULED_DATE_NOT_FUTURE_DATED = "Schedule date must be in the future";
    public final String ERROR_CUT_OFF_DATE_NOT_BEFORE_SCHEDULE_DATE = "Cut-off date must be before schedule date";

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CommonUtilsService commonUtilsService;

    @Autowired
    private NumberPickValidationService numberPickValidationService;

    @Autowired
    private TransactionService transactionService;

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Page<Game> searchGames(String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return gameRepository.findAll(pageable);
        }
        return gameRepository.findByReferenceContaining(reference.trim(), pageable);
    }

    public Game findGame(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_GAME_ID + id));
    }

    public List<Game> getUpcomingGames() {
        return gameRepository.findUpcomingGames(LocalDateTime.now());
    }

    public List<Game> getActiveGames() {
        return gameRepository.findActiveGames(LocalDateTime.now());
    }

    public List<Game> getOngoingGames() {
        return gameRepository.findByStatus(GameStatus.ONGOING);
    }

    @Transactional
    public Game createGame(Game game) {

        //Validate dates
        validateDates(game);

        //Generate Reference
        game.setReference(commonUtilsService.generateReference(TransactionType.GAME));

        //Set to pending
        game.setStatus(GameStatus.PENDING);

        //Save transaction
        transactionService.saveTransaction(game, Action.CREATE_GAME);

        return gameRepository.save(game);
    }

    @Transactional
    public void updateGame(Long id, Game game) {

        //Validate dates
        validateDates(game);

        //Find existing game
        Game existingGame = findGame(id);

        if(existingGame.getStatus() == GameStatus.COMPLETED) {
            throw new IllegalStateException("Cannot modify already completed game.");
        }

        if(existingGame.getStatus() == GameStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify already cancelled game.");
        }

        //Only Pending status can modify dates and threshold values
        if (existingGame.getStatus() == GameStatus.PENDING) {

            //Set threshold
            existingGame.setBetAmountThreshold(game.getBetAmountThreshold());
            existingGame.setBetThresholdPercentage(game.getBetThresholdPercentage());

            //Set dates
            existingGame.setScheduleDateTime(game.getScheduleDateTime());
            existingGame.setCutOffDateTime(game.getCutOffDateTime());
        }

        //Only Pending and Ongoing status can modify threshold values
        if (existingGame.getStatus() == GameStatus.ONGOING) {

            //Set threshold
            existingGame.setBetAmountThreshold(game.getBetAmountThreshold());
            existingGame.setBetThresholdPercentage(game.getBetThresholdPercentage());
        }

        // Only update winning combination if game is FOR_COMPLETION
        if (existingGame.getStatus() == GameStatus.FOR_COMPLETION && (game.getWinningCombination() != null && !game.getWinningCombination().isEmpty())) {

            String winningCombination = game.getWinningCombination();
            String gameType = String.valueOf(existingGame.getGameType());

            numberPickValidationService.validateNumberPicks(winningCombination, gameType);

            existingGame.setWinningCombination(winningCombination);
        }

        //Save transaction
        Action action = switch (existingGame.getStatus()) {
            case PENDING ->
                Action.UPDATE_PENDING_GAME;
            case ONGOING ->
                Action.UPDATE_ONGOING_GAME;
            case FOR_COMPLETION ->
                Action.UPDATE_FOR_COMPLETION_GAME;
            default ->
                    throw new IllegalStateException("Unexpected value: " + existingGame.getStatus());
        };

        //Save transaction
        transactionService.saveTransaction(game, action);

        gameRepository.save(existingGame);
    }

    @Transactional
    public void startGame(Long id) {

        Game game = findGame(id);

        if (game.getStatus() != GameStatus.PENDING) {
            throw new IllegalStateException("Game must be in PENDING status to start");
        }

        validateDates(game);

        game.setStatus(GameStatus.ONGOING);

        //Save transaction
        transactionService.saveTransaction(game, Action.START_GAME);

        gameRepository.save(game);
    }

    @Transactional
    public void processGame(Long id) {

        Game game = findGame(id);

        validateGameStatus(game);

        //TODO: Uncomment this after testing/prior release

        /* Commented for testing purposes
        if (LocalDateTime.now().isBefore(game.getScheduleDateTime())) {
            throw new IllegalStateException("Cannot process game ahead of schedule.");
        }
        */

        game.setStatus(GameStatus.FOR_COMPLETION);

        //Save transaction
        transactionService.saveTransaction(game, Action.FOR_COMPLETION_GAME);

        gameRepository.save(game);
    }

    @Transactional
    public void completeGame(Long id) {

        Game game = findGame(id);

        validateGameStatus(game);


        if(game.getWinningCombination() == null || game.getWinningCombination().isEmpty()) {
            throw new IllegalStateException("Complete details of game winning combination before proceeding.");
        }

        //TODO: Validate winning combination input

        //TODO: Process game bets

        game.setStatus(GameStatus.COMPLETED);

        //Save transaction
        transactionService.saveTransaction(game, Action.COMPLETE_GAME);

        gameRepository.save(game);
    }

    @Transactional
    public void cancelGame(Long id) {
        Game game = findGame(id);

        validateGameStatus(game);

        //TODO: Process cancellation of game bets

        game.setStatus(GameStatus.CANCELLED);

        //Save transaction
        transactionService.saveTransaction(game, Action.CANCEL_GAME);

        gameRepository.save(game);
    }

    private void validateDates(Game game) {
        // Validate that schedule date is after current date
        if (game.getScheduleDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ERROR_SCHEDULED_DATE_NOT_FUTURE_DATED);
        }

        // Validate that cut-off date is before schedule date
        if (game.getCutOffDateTime().isAfter(game.getScheduleDateTime())) {
            throw new IllegalArgumentException(ERROR_CUT_OFF_DATE_NOT_BEFORE_SCHEDULE_DATE);
        }
    }

    private void validateGameStatus(Game game) {

        if(game.getStatus() == GameStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update already completed game.");
        }

        if(game.getStatus() == GameStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update already cancelled game.");
        }
    }

}
