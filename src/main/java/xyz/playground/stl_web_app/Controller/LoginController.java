package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Service.GameService;
import xyz.playground.stl_web_app.Service.RequestService;

import java.util.List;

import static xyz.playground.stl_web_app.Constants.StringConstants.ACTIVE_TAB;
import static xyz.playground.stl_web_app.Constants.StringConstants.PAGE_TITLE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VIEW_NAME;
import static xyz.playground.stl_web_app.Constants.StringConstants.MAIN_LAYOUT;

@Controller
public class LoginController {

    private final String VAR_NEXT_GAME = "nextGame";
    private final String VAR_PENDING_REQUEST = "pendingRequests";
    private final String VAR_PENDING_REQUEST_COUNT = "pendingRequestsCount";

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

    @GetMapping(ENDPOINT_LOGIN)
    public String login() {
        return LOGIN;
    }

    // Add to LoginController.java
    @GetMapping(ENDPOINT_DASHBOARD)
    public String dashboard(Model model) {

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        // Get pending requests for current user
        List<Request> pendingRequests = requestService.getPendingRequestsForUser(currentUserId);

        // Get upcoming games
        List<Game> upcomingGames = gameService.getUpcomingGames();
        Game nextGame = upcomingGames.isEmpty() ? null : upcomingGames.get(0);

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

