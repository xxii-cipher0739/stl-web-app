package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Model.Transaction;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Service.*;

import java.math.BigDecimal;
import java.util.List;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class LoginController {

    private final String VAR_NEXT_GAME = "nextGame";
    private final String VAR_WALLET_BALANCE = "walletBalance";
    private final String VAR_PENDING_REQUEST = "pendingRequests";
    private final String VAR_PENDING_REQUEST_COUNT = "pendingRequestsCount";
    private final String VAR_RECENT_TRANSACTIONS = "recentTransactions";
    private final String VAR_UNKNOWN_USER = "Unknown User";

    private final String LOGIN = "login";
    private final String DASHBOARD = "dashboard";

    private final String ENDPOINT_BASE = "/";
    private final String ENDPOINT_LOGIN = "/login";
    private final String ENDPOINT_DASHBOARD = "/dashboard";
    private final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private final String DASHBOARD_TITLE = "Dashboard - Game Web App";

    @Autowired
    private GameService gameService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping(ENDPOINT_LOGIN)
    public String login() {
        return LOGIN;
    }

    // Add to LoginController.java
    @GetMapping(ENDPOINT_DASHBOARD)
    public String dashboard(Model model) {

        //Get current User
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.getCurrentUserId(auth);

        // Get recent transactions
        List<Transaction> recentTransactions
                =
                (auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_ + ADMIN_ROLE))
                )
                // If admin, show all transactions
                ? transactionService.getRecentTransactions(15)
                // For regular users, show only their transactions
                : transactionService.getRecentTransactionsByUserId(currentUserId, 15);


        // Enhance transactions with users name
        for (Transaction transaction : recentTransactions) {
            User user = userService.getUserById(transaction.getPerformedBy());
            transaction.setUserName((user != null) ? user.getName() : VAR_UNKNOWN_USER);
        }

        //TODO: Improve query for pending request

        //Get pending requests for current user
        List<Request> pendingRequests = requestService.getPendingRequestsForUser(currentUserId);

        // Get upcoming games
        List<Game> upcomingGames = gameService.getUpcomingGames();
        Game nextGame = upcomingGames.isEmpty() ? null : upcomingGames.get(0);

        // Get current wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        model.addAttribute(VAR_RECENT_TRANSACTIONS, recentTransactions);
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_PENDING_REQUEST, pendingRequests);
        model.addAttribute(VAR_PENDING_REQUEST_COUNT, pendingRequests.size());
        model.addAttribute(VAR_NEXT_GAME, nextGame);
        model.addAttribute(PAGE_TITLE, DASHBOARD_TITLE);
        model.addAttribute(ACTIVE_TAB, DASHBOARD);
        model.addAttribute(VIEW_NAME, DASHBOARD);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_BASE)
    public String home() {
        return REDIRECT_DASHBOARD;
    }

}

