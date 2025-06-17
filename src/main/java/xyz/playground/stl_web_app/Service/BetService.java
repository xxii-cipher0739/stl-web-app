package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.BetStatus;
import xyz.playground.stl_web_app.Model.Bet;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Repository.BetRepository;
import xyz.playground.stl_web_app.Repository.GameRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BetService {
    @Autowired
    private BetRepository betRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WalletService walletService;

    public List<Bet> getAllBets() {
        return betRepository.findAll();
    }

    public Bet findBet(Long id) {
        return betRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bet Id: " + id));
    }

    public Optional<Bet> getBetByReference(String reference) {
        return betRepository.findByReference(reference);
    }

    public List<Bet> getBetsByUser(Long userId) {
        return betRepository.findByCreatedBy(userId);
    }

    public List<Bet> getBetsByGame(Long gameId) {
        return betRepository.findByGameId(gameId);
    }

    public List<Bet> getBetsByStatus(BetStatus status) {
        return betRepository.findByStatus(status);
    }

    public List<Bet> getBetsForToday(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return betRepository.findByCreatedByAndDateRange(userId, startOfDay, endOfDay);
    }

    public Long countBetsForToday(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return betRepository.countBetsByUserAndDateRange(userId, startOfDay, endOfDay);
    }

    public Bet createBet(Bet bet, Long userId) {
        // Validate game exists and is active
        Game game = gameRepository.findById(bet.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id: " + bet.getGameId()));

        if (!game.isEnabled()) {
            throw new IllegalStateException("Game is not active");
        }

        if (game.isExecuted()) {
            throw new IllegalStateException("Game has already been executed");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(game.getCutOffDateTime())) {
            throw new IllegalStateException("Betting period has ended for this game");
        }

        // Generate reference if not provided
        if (bet.getReference() == null || bet.getReference().isEmpty()) {
            bet.setReference(generateReference());
        }

        // Set created by and date
        bet.setCreatedBy(userId);
        bet.setDateTimeCreated(now);

        // Set initial status
        bet.setStatus(BetStatus.PLACED);

        // Deduct amount from wallet
        walletService.decreaseAmount(userId, bet.getAmount());

        return betRepository.save(bet);
    }

    public Bet updateBet(Long id, Bet bet) {
        Bet existingBet = findBet(id);

        // Only allow updates if bet is still in PLACED status
        if (existingBet.getStatus() != BetStatus.PLACED) {
            throw new IllegalStateException("Cannot update bet with status: " + existingBet.getStatus());
        }

        // Check if game is still open for betting
        Game game = gameRepository.findById(existingBet.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id: " + existingBet.getGameId()));

        if (!game.isEnabled() || game.isExecuted() || LocalDateTime.now().isAfter(game.getCutOffDateTime())) {
            throw new IllegalStateException("Game is no longer open for betting");
        }

        // Handle amount change
        if (bet.getAmount().compareTo(existingBet.getAmount()) != 0) {
            BigDecimal difference = bet.getAmount().subtract(existingBet.getAmount());
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                // Additional amount needed
                walletService.decreaseAmount(existingBet.getCreatedBy(), difference);
            } else {
                // Refund excess
                walletService.increaseAmount(existingBet.getCreatedBy(), difference.abs());
            }
        }

        // Update fields
        existingBet.setAmount(bet.getAmount());

        return betRepository.save(existingBet);
    }

    public Bet cancelBet(Long id) {
        Bet bet = findBet(id);

        // Only allow cancellation if bet is still in PLACED status
        if (bet.getStatus() != BetStatus.PLACED) {
            throw new IllegalStateException("Cannot cancel bet with status: " + bet.getStatus());
        }

        // Check if game is still open for betting
        Game game = gameRepository.findById(bet.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id: " + bet.getGameId()));

        if (game.isExecuted() || LocalDateTime.now().isAfter(game.getCutOffDateTime())) {
            throw new IllegalStateException("Game is closed, bet cannot be cancelled");
        }

        // Refund amount to wallet
        walletService.increaseAmount(bet.getCreatedBy(), bet.getAmount());

        // Update status
        bet.setStatus(BetStatus.CANCELLED);

        return betRepository.save(bet);
    }

    public void deleteBet(Long id) {
        Bet bet = findBet(id);

        // Only allow deletion if bet is in CANCELLED status
        if (bet.getStatus() != BetStatus.CANCELLED) {
            throw new IllegalStateException("Only cancelled bets can be deleted");
        }

        betRepository.deleteById(id);
    }

    private String generateReference() {
        // Generate a unique reference code (BET-UUID)
        return "BET-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
