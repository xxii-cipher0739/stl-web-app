package xyz.playground.stl_web_app.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.BetStatus;
import xyz.playground.stl_web_app.Constants.GameStatus;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Bet;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Repository.BetRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BetService {

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtilsService commonUtilsService;

    @Autowired
    private NumberPickValidationService numberPickValidationService;

    public Bet findBet(Long id) {
        return betRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bet Id: " + id));
    }

    public Page<Bet> searchBets(String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return betRepository.findAll(pageable);
        }
        return betRepository.findByReferenceContaining(reference.trim(), pageable);
    }

    public Page<Bet> searchRequestsByUser(Long userId, String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return betRepository.findByUserInvolved(userId, pageable);
        }
        return betRepository.findByUserInvolvedAndReferenceContaining(userId, reference.trim(), pageable);
    }

    public Bet getAndValidateBet(Long id) {

        //Get current user
        Long currentUserId = userService.getCurrentUserId();

        // Check if user owns this bet
        Bet existingBet = findBet(id);
        if (!existingBet.getCreatedBy().equals(currentUserId)) {
            throw new IllegalArgumentException("You don't have permission to modify this bet.");
        }

        // Only allow processing if bet is still in PLACED status
        if (existingBet.getStatus() != BetStatus.PLACED) {
            throw new IllegalStateException("Cannot process bet with status: " + existingBet.getStatus());
        }

        return existingBet;
    }

    @Transactional
    public Bet createBet(Bet bet, Long userId) {

        // Validate game exists and is active
        Game game = gameService.findGame(bet.getGameId());

        if (game.getStatus() != GameStatus.ONGOING) {
            throw new IllegalStateException("Game status is not ongoing.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(game.getCutOffDateTime())) {
            throw new IllegalStateException("Betting period has ended for this game");
        }

        // Validate bet numbers based on game type
        numberPickValidationService.validateNumberPicks(bet.getBetNumbers(), game.getGameType());

        // Generate reference if not provided
        if (bet.getReference() == null || bet.getReference().isEmpty()) {
            bet.setReference(commonUtilsService.generateReference(TransactionType.BET));
        }

        // Set created by and date
        bet.setCreatedBy(userId);
        bet.setDateTimeCreated(now);

        // Set initial status
        bet.setStatus(BetStatus.PLACED);

        // Deduct amount from wallet
        walletService.deductWallet(userId, bet.getAmount());

        return betRepository.save(bet);
    }

    @Transactional
    public void updateBet(Long id, Bet bet) {

        Bet existingBet = getAndValidateBet(id);

        // Check if game is still open for betting
        Game game = gameService.findGame(existingBet.getGameId());

        if (game.getStatus() == GameStatus.COMPLETED || LocalDateTime.now().isAfter(game.getCutOffDateTime())) {
            throw new IllegalStateException("Game is no longer open for betting");
        }

        // Validate bet numbers based on game type
        numberPickValidationService.validateNumberPicks(bet.getBetNumbers(), game.getGameType());

        // Handle amount change
        if (bet.getAmount().compareTo(existingBet.getAmount()) != 0) {
            BigDecimal difference = bet.getAmount().subtract(existingBet.getAmount());
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                // Additional amount needed
                walletService.deductWallet(existingBet.getCreatedBy(), difference);
            } else {
                // Refund excess
                walletService.increaseWallet(existingBet.getCreatedBy(), difference.abs());
            }
        }

        // Update fields
        existingBet.setBettor(bet.getBettor());
        existingBet.setAmount(bet.getAmount());
        existingBet.setBetNumbers(bet.getBetNumbers());

        betRepository.save(existingBet);
    }

    @Transactional
    public void cancelBet(Long id) {

        Bet bet = getAndValidateBet(id);

        // Refund amount to wallet
        walletService.increaseWallet(bet.getCreatedBy(), bet.getAmount());

        // Update status
        bet.setStatus(BetStatus.CANCELLED);

        betRepository.save(bet);
    }

    @Transactional
    public void confirmBet(long id) {
        Bet bet = getAndValidateBet(id);

        bet.setStatus(BetStatus.CONFIRMED);

        //TODO add receipt processing

        betRepository.save(bet);
    }

}
