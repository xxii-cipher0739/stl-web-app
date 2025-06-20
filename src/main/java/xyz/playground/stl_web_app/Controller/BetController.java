package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.BetStatus;
import xyz.playground.stl_web_app.Constants.GameType;
import xyz.playground.stl_web_app.Model.Bet;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class BetController {

    private final String NEW_BET = "newBet";
    private final String BETS = "bets";

    private final String DEFAULT_BET_SORT = "dateTimeCreated";
    private final String VAR_WALLET_BALANCE = "walletBalance";
    private final String VAR_SELECTED_GAME_TYPE = "selectedGameType";
    private final String VAR_BET_LIST = "betList";
    private final String VAR_BET_STATUSES = "betStatuses";
    private final String VAR_GAME_TYPES = "gameTypes";
    private final String VAR_GAMES = "games";
    private final String VAR_USERS = "users";

    private final String ENDPOINT_BETS = "/bets";
    private final String ENDPOINT_BETS_ADD = "/bets/add";
    private final String ENDPOINT_BETS_EDIT = "/bets/edit/{id}";
    private final String ENDPOINT_BETS_UPDATE = "/bets/update/{id}";
    private final String ENDPOINT_BETS_CONFIRM = "/bets/confirm/{id}";
    private final String ENDPOINT_BETS_CANCEL = "/bets/cancel/{id}";
    private final String ENDPOINT_BET_LIST = "bets/list";
    private final String ENDPOINT_BET_FORM = "bets/form";
    private final String REDIRECT_BETS = "redirect:/bets";

    private final String BETS_TITLE = "Bets - View";
    private final String BETS_ADD_TITLE = "Bets - Place Bet";
    private final String BETS_EDIT_TITLE = "Bets - Edit Bet";

    private final String ERROR_ADD_BET = "Error placing bet: ";
    private final String ERROR_UPDATE_BET = "Error updating bet: ";
    private final String ERROR_CONFIRM_BET = "Error updating bet: ";
    private final String ERROR_CANCEL_BET = "Error cancelling bet: ";
    private final String SUCCESSFUL_ADD_BET = "Bet placed successfully";
    private final String SUCCESSFUL_UPDATE_BET = "Bet updated successfully";
    private final String SUCCESSFUL_CONFIRM_BET = "Bet confirmed successfully";
    private final String SUCCESSFUL_CANCEL_BET = "Bet cancelled successfully";


    @Autowired
    private BetService betService;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CommonUtilsService commonUtilsService;

    @GetMapping(ENDPOINT_BETS)
    public String listBets(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_BET_SORT) String sort,
            @RequestParam(defaultValue = DESC) String direction,
            @RequestParam(required = false) String search,
            Model model) {

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.getCurrentUserId(auth);

        // Create pageable
        Pageable pageable = commonUtilsService.getPageable(direction, sort, size, page);

        Page<Bet> betPage;

        // If admin, show all bets
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(ROLE_ + ADMIN_ROLE))) {
            betPage = betService.searchBets(search, pageable);
        } else {
            // For regular users, show only their bets
            betPage = betService.searchRequestsByUser(currentUserId, search, pageable);
        }

        // Get all users for display purposes (admin only)
        List<User> users = userService.getAllUsers();

        // Get wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        //DP: Search for game and update Bet details (game type and game schedule)
        List<Game> games = new ArrayList<>();
        for (Bet bet : betPage.getContent()) {
            boolean isFound = false;
            Game matchedGame = new Game();

            for (Game game : games) {
                if (Objects.equals(game.getId(), bet.getGameId())) {
                    matchedGame = game;
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                matchedGame = gameService.findGame(bet.getGameId());
                games.add(matchedGame);
            }

            bet.setGameType(matchedGame.getGameTypeValue());
            bet.setGameSchedule(matchedGame.getScheduleDateTime());
        }

        model.addAttribute(VAR_BET_LIST, betPage.getContent());
        model.addAttribute(VAR_USERS, users);
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_BET_STATUSES, BetStatus.values());
        model.addAttribute(PAGE, betPage);
        model.addAttribute(SEARCH, search);
        model.addAttribute(SORT, sort);
        model.addAttribute(DIRECTION, direction);
        model.addAttribute(PAGE_TITLE, BETS_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_LIST);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_BETS_ADD)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String showAddForm(Model model) {
        // Get current user
        Long currentUserId = userService.getCurrentUserId();

        Bet bet = new Bet();
        bet.setCreatedBy(currentUserId);

        // Get active games for dropdown
        List<Game> activeGames = gameService.getOngoingGames();

        // Get wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        model.addAttribute(NEW_BET, bet);
        model.addAttribute(VAR_GAMES, activeGames);
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_GAME_TYPES, GameType.values());
        model.addAttribute(PAGE_TITLE, BETS_ADD_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_BETS_ADD)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String addBet(@ModelAttribute Bet bet, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> {
                    betService.createBet(value, userService.getCurrentUserId());
                },
                bet,
                redirectAttributes,
                SUCCESSFUL_ADD_BET,
                ERROR_ADD_BET,
                REDIRECT_BETS);
    }

    @GetMapping(ENDPOINT_BETS_EDIT)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String showEditForm(@PathVariable Long id, Model model) {
        // Get current user
        Long currentUserId = userService.getCurrentUserId();

        Bet bet = betService.getAndValidateBet(id);

        // Get active games for dropdown
        List<Game> activeGames = gameService.getOngoingGames();

        // Get the game type for the selected game
        Game selectedGame = gameService.findGame(bet.getGameId());

        // Get wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        model.addAttribute(NEW_BET, bet);
        model.addAttribute(VAR_GAMES, activeGames);
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_SELECTED_GAME_TYPE, selectedGame.getGameType());
        model.addAttribute(VAR_GAME_TYPES, GameType.values());
        model.addAttribute(PAGE_TITLE, BETS_EDIT_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_BETS_UPDATE)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String updateBet(@PathVariable Long id, @ModelAttribute Bet bet, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> betService.updateBet(value, bet),
                id,
                redirectAttributes,
                SUCCESSFUL_UPDATE_BET,
                ERROR_UPDATE_BET,
                REDIRECT_BETS);
    }

    @GetMapping(ENDPOINT_BETS_CONFIRM)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String confirmBet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> betService.confirmBet(value),
                id,
                redirectAttributes,
                SUCCESSFUL_CONFIRM_BET,
                ERROR_CONFIRM_BET,
                REDIRECT_BETS);
    }

    @GetMapping(ENDPOINT_BETS_CANCEL)
    @PreAuthorize(ROLE_HAS_COLLECTOR)
    public String cancelBet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> betService.cancelBet(value),
                id,
                redirectAttributes,
                SUCCESSFUL_CANCEL_BET,
                ERROR_CANCEL_BET,
                REDIRECT_BETS);
    }


}
