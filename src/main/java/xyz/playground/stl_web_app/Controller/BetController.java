package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Service.BetService;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Service.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class BetController {

    private final String NEW_BET = "newBet";
    private final String BETS = "bets";

    private final String VAR_BET_LIST = "betList";
    private final String VAR_BET_STATUSES = "betStatuses";
    private final String VAR_GAMES = "games";
    private final String VAR_USERS = "users";

    private final String ENDPOINT_BETS = "/bets";
    private final String ENDPOINT_BETS_ADD = "/bets/add";
    private final String ENDPOINT_BETS_EDIT = "/bets/edit/{id}";
    private final String ENDPOINT_BETS_UPDATE = "/bets/update/{id}";
    private final String ENDPOINT_BETS_CANCEL = "/bets/cancel/{id}";
    private final String ENDPOINT_BETS_DELETE = "/bets/delete/{id}";
    private final String ENDPOINT_BET_LIST = "bets/list";
    private final String ENDPOINT_BET_FORM = "bets/form";
    private final String REDIRECT_BETS = "redirect:/bets";

    private final String BETS_TITLE = "Bets - View";
    private final String BETS_ADD_TITLE = "Bets - Place Bet";
    private final String BETS_EDIT_TITLE = "Bets - Edit Bet";

    private final String ERROR_ADD_BET = "Error placing bet: ";
    private final String ERROR_UPDATE_BET = "Error updating bet: ";
    private final String ERROR_CANCEL_BET = "Error cancelling bet: ";
    private final String ERROR_DELETE_BET = "Error deleting bet: ";
    private final String SUCCESSFUL_ADD_BET = "Bet placed successfully";
    private final String SUCCESSFUL_UPDATE_BET = "Bet updated successfully";
    private final String SUCCESSFUL_CANCEL_BET = "Bet cancelled successfully";
    private final String SUCCESSFUL_DELETE_BET = "Bet deleted successfully";

    @Autowired
    private BetService betService;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(ENDPOINT_BETS)
    public String listBets(Model model) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        List<Bet> bets;

        // If admin, show all bets
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            bets = betService.getAllBets();
        } else {
            // For regular users, show only their bets
            bets = betService.getBetsByUser(currentUserId);
        }

        // Get all users for display purposes (admin only)
        List<User> users = userRepository.findAll();

        //DP: Search for game and update Bet details (game type and game schedule)
        List<Game> games = new ArrayList<>();
        for (Bet bet : bets) {
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

        model.addAttribute(VAR_BET_LIST, bets);
        model.addAttribute(VAR_USERS, users);
        model.addAttribute(VAR_BET_STATUSES, BetStatus.values());
        model.addAttribute(PAGE_TITLE, BETS_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_LIST);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_BETS_ADD)
    @PreAuthorize("hasRole('COLLECTOR')")
    public String showAddForm(Model model) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        Bet bet = new Bet();
        bet.setCreatedBy(currentUserId);

        // Get active games for dropdown
        List<Game> activeGames = gameService.getActiveGames();

        model.addAttribute(NEW_BET, bet);
        model.addAttribute(VAR_GAMES, activeGames);
        model.addAttribute("gameTypes", GameType.values());
        model.addAttribute(PAGE_TITLE, BETS_ADD_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_BETS_ADD)
    @PreAuthorize("hasRole('COLLECTOR')")
    public String addBet(@ModelAttribute Bet bet, RedirectAttributes redirectAttributes) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long currentUserId = userDetails.getId();

            System.out.println("bet: " + bet.getBettor());
            betService.createBet(bet, currentUserId);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_ADD_BET);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_ADD_BET + e.getMessage());
        }
        return REDIRECT_BETS;
    }

    @GetMapping(ENDPOINT_BETS_EDIT)
    @PreAuthorize("hasRole('COLLECTOR')")
    public String showEditForm(@PathVariable Long id, Model model) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        Bet bet = betService.findBet(id);

        // Check if user owns this bet or is admin
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !bet.getCreatedBy().equals(currentUserId)) {
            throw new IllegalArgumentException("You don't have permission to edit this bet");
        }

        // Get active games for dropdown
        List<Game> activeGames = gameService.getActiveGames();

        // Get the game type for the selected game
        Game selectedGame = gameService.findGame(bet.getGameId());

        model.addAttribute(NEW_BET, bet);
        model.addAttribute(VAR_GAMES, activeGames);
        model.addAttribute("selectedGameType", selectedGame.getGameType());
        model.addAttribute("gameTypes", GameType.values());
        model.addAttribute(PAGE_TITLE, BETS_EDIT_TITLE);
        model.addAttribute(ACTIVE_TAB, BETS);
        model.addAttribute(VIEW_NAME, ENDPOINT_BET_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_BETS_UPDATE)
    @PreAuthorize("hasRole('COLLECTOR')")
    public String updateBet(@PathVariable Long id, @ModelAttribute Bet bet, RedirectAttributes redirectAttributes) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long currentUserId = userDetails.getId();

            System.out.println("bet: " + bet.getBettor());

            // Check if user owns this bet or is admin
            Bet existingBet = betService.findBet(id);
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin && !existingBet.getCreatedBy().equals(currentUserId)) {
                throw new IllegalArgumentException("You don't have permission to update this bet");
            }

            betService.updateBet(id, bet);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_UPDATE_BET);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_UPDATE_BET + e.getMessage());
        }
        return REDIRECT_BETS;
    }

    @GetMapping(ENDPOINT_BETS_CANCEL)
    @PreAuthorize("hasRole('COLLECTOR')")
    public String cancelBet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            Long currentUserId = userDetails.getId();

            // Check if user owns this bet or is admin
            Bet existingBet = betService.findBet(id);
            boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin && !existingBet.getCreatedBy().equals(currentUserId)) {
                throw new IllegalArgumentException("You don't have permission to cancel this bet");
            }

            betService.cancelBet(id);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_CANCEL_BET);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_CANCEL_BET + e.getMessage());
        }
        return REDIRECT_BETS;
    }

    @GetMapping(ENDPOINT_BETS_DELETE)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String deleteBet(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            betService.deleteBet(id);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_DELETE_BET);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_DELETE_BET + e.getMessage());
        }
        return REDIRECT_BETS;
    }
}
