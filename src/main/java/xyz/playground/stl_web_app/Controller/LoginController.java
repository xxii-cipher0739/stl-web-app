package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.playground.stl_web_app.Model.*;
import xyz.playground.stl_web_app.Service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class LoginController {

    private final String VAR_NEXT_GAME = "nextGame";
    private final String VAR_WALLET_BALANCE = "walletBalance";
    private final String VAR_PENDING_REQUEST = "pendingRequests";
    private final String VAR_PENDING_REQUEST_COUNT = "pendingRequestsCount";
    private final String VAR_RECENT_TRANSACTIONS = "recentTransactions";

    private final String DEFAULT_TRANSACTION_SORT = "datetimeStamp";

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

    @Autowired
    private CommonUtilsService commonUtilsService;

    @GetMapping(ENDPOINT_LOGIN)
    public String login() {
        return LOGIN;
    }

    // Add to LoginController.java
    @GetMapping(ENDPOINT_DASHBOARD)
    public String dashboard(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_TRANSACTION_SORT) String sort,
            @RequestParam(defaultValue = DESC) String direction,
            @RequestParam(required = false) String search,
            Model model) {

        //Get current User
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.getCurrentUserId(auth);

        //Get Pageable
        Pageable pageable = commonUtilsService.getPageable(direction, sort, size, page);

        Page<Transaction> transactionPage
                =
                (auth.getAuthorities()
                        .stream()
                        .anyMatch(a -> a.getAuthority().equals(ROLE_ + ADMIN_ROLE))
                )
                // If admin, show all transactions
                ? transactionService.searchTransactions(search, pageable)
                // For regular users, show only their transactions
                : transactionService.searchTransactionsByUser(currentUserId, search, pageable);

        List<User> users = new ArrayList<>();

        //For setting actor user name
        for (Transaction transaction : transactionPage.getContent()) {
            boolean isFound = false;
            User matchedUser = new User();

            for (User user: users) {
                if (Objects.equals(user.getId(), transaction.getActorId())) {
                    matchedUser = user;
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                matchedUser = userService.getUserById(transaction.getActorId());
                users.add(matchedUser);
            }

            transaction.setActorName(matchedUser.getName());
        }

        //For setting target user name
        for (Transaction transaction : transactionPage.getContent()) {

            if (null == transaction.getTargetId()) {
                transaction.setTargetName("N/A");
                continue;
            }

            boolean isFound = false;
            User matchedUser = new User();

            for (User user: users) {
                if (Objects.equals(user.getId(), transaction.getTargetId())) {
                    matchedUser = user;
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                matchedUser = userService.getUserById(transaction.getTargetId());
                users.add(matchedUser);
            }

            transaction.setTargetName(matchedUser.getName());
        }

        //TODO: Improve query for pending request

        //Get pending requests for current user
        List<Request> pendingRequests = requestService.getPendingRequestsForUser(currentUserId);

        // Get upcoming games
        List<Game> upcomingGames = gameService.getUpcomingGames();
        Game nextGame = upcomingGames.isEmpty() ? null : upcomingGames.get(0);

        // Get current wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        model.addAttribute(VAR_RECENT_TRANSACTIONS, transactionPage.getContent());
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_PENDING_REQUEST, pendingRequests);
        model.addAttribute(VAR_PENDING_REQUEST_COUNT, pendingRequests.size());
        model.addAttribute(VAR_NEXT_GAME, nextGame);
        model.addAttribute(PAGE, transactionPage);
        model.addAttribute(SEARCH, search);
        model.addAttribute(SORT, sort);
        model.addAttribute(DIRECTION, direction);
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

