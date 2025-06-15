package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Service.GameService;

import java.util.List;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class LoginController {

    @Autowired
    private GameService gameService;

    @GetMapping(ENDPOINT_LOGIN)
    public String login() {
        return LOGIN;
    }

    // Add to LoginController.java
    @GetMapping(ENDPOINT_DASHBOARD)
    public String dashboard(Model model) {

        // Get upcoming games
        List<Game> upcomingGames = gameService.getUpcomingGames();
        Game nextGame = upcomingGames.isEmpty() ? null : upcomingGames.get(0);

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

