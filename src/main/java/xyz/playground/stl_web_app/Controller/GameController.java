package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.GameType;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Service.GameService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static xyz.playground.stl_web_app.Constants.StringConstants.ACTIVE_TAB;
import static xyz.playground.stl_web_app.Constants.StringConstants.PAGE_TITLE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VIEW_NAME;
import static xyz.playground.stl_web_app.Constants.StringConstants.MAIN_LAYOUT;
import static xyz.playground.stl_web_app.Constants.StringConstants.ROLE_HAS_ADMIN;
import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_SUCCESS_MESSAGE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_ERROR_MESSAGE;


@Controller
public class GameController {

    private final String NEW_GAME = "newGame";
    private final String GAMES = "games";

    private final String VAR_GAME_LIST = "gameList";
    private final String VAR_GAME_TYPES = "gameTypes";
    private final String VAR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private final String VAR_FORMATTED_SCHEDULED_DATE = "formattedScheduleDate";
    private final String VAR_FORMATTED_CUT_OFF_DATE = "formattedCutOffDate";

    private final String ENDPOINT_GAMES = "/games";
    private final String ENDPOINT_GAMES_ADD = "/games/add";
    private final String ENDPOINT_GAMES_EDIT = "/games/edit/{id}";
    private final String ENDPOINT_GAMES_UPDATE = "/games/update/{id}";
    private final String ENDPOINT_GAMES_DELETE = "/games/delete/{id}";
    private final String ENDPOINT_GAMES_EXECUTE = "/games/execute/{id}";
    private final String ENDPOINT_GAME_LIST = "games/list";
    private final String ENDPOINT_GAME_FORM = "games/form";
    private final String REDIRECT_GAMES = "redirect:/games";

    private final String GAMES_TITLE = "Games - View";
    private final String GAMES_ADD_TITLE = "Games - Add Game";
    private final String GAMES_EDIT_TITLE ="Games - Edit Game";

    private final String ERROR_ADD_GAME = "Error creating game: ";
    private final String ERROR_UPDATE_GAME = "Error updating game: ";
    private final String ERROR_DELETE_GAME = "Error deleting game: ";
    private final String ERROR_EXECUTE_GAME = "Error executing game: ";
    private final String SUCCESSFUL_ADD_GAME = "Game created successfully";
    private final String SUCCESSFUL_UPDATE_GAME = "Game updated successfully";
    private final String SUCCESSFUL_DELETE_GAME = "Game deleted successfully";
    private final String SUCCESSFUL_EXECUTE_GAME = "Game executed successfully";

    @Autowired
    private GameService gameService;

    @GetMapping(ENDPOINT_GAMES)
    public String listGames(Model model) {
        model.addAttribute(VAR_GAME_LIST, gameService.getAllGames());
        model.addAttribute(PAGE_TITLE, GAMES_TITLE);
        model.addAttribute(ACTIVE_TAB, GAMES);
        model.addAttribute(VIEW_NAME, ENDPOINT_GAME_LIST);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_GAMES_ADD)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String showAddForm(Model model) {

        Game game = new Game();

        // Set default values for new game
        game.setScheduleDateTime(LocalDateTime.now().plusDays(1));
        game.setCutOffDateTime(LocalDateTime.now().plusDays(1).minusHours(1));
        //By default set to false
        game.setEnabled(false);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(VAR_DATE_FORMAT);
        String formattedScheduleDate = game.getScheduleDateTime().format(formatter);
        String formattedCutOffDate = game.getCutOffDateTime().format(formatter);

        model.addAttribute(NEW_GAME, game);
        model.addAttribute(VAR_FORMATTED_SCHEDULED_DATE, formattedScheduleDate);
        model.addAttribute(VAR_FORMATTED_CUT_OFF_DATE, formattedCutOffDate);
        model.addAttribute(VAR_GAME_TYPES, GameType.values());
        model.addAttribute(PAGE_TITLE, GAMES_ADD_TITLE);
        model.addAttribute(ACTIVE_TAB, GAMES);
        model.addAttribute(VIEW_NAME, ENDPOINT_GAME_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_GAMES_ADD)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String addGame(@ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        try {
            gameService.createGame(game);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_ADD_GAME);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_ADD_GAME + e.getMessage());
        }
        return REDIRECT_GAMES;
    }

    @GetMapping(ENDPOINT_GAMES_EDIT)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String showEditForm(@PathVariable Long id, Model model) {
        Game game = gameService.findGame(id);

        // Format dates for datetime-local input
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(VAR_DATE_FORMAT);
        String formattedScheduleDate = game.getScheduleDateTime().format(formatter);
        String formattedCutOffDate = game.getCutOffDateTime().format(formatter);

        model.addAttribute(NEW_GAME, game);
        model.addAttribute(VAR_FORMATTED_SCHEDULED_DATE, formattedScheduleDate);
        model.addAttribute(VAR_FORMATTED_CUT_OFF_DATE, formattedCutOffDate);
        model.addAttribute(VAR_GAME_TYPES, GameType.values());
        model.addAttribute(PAGE_TITLE, GAMES_EDIT_TITLE);
        model.addAttribute(ACTIVE_TAB, GAMES);
        model.addAttribute(VIEW_NAME, ENDPOINT_GAME_FORM);
        return MAIN_LAYOUT;
    }


    @PostMapping(ENDPOINT_GAMES_UPDATE)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String updateGame(@PathVariable Long id, @ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        try {
            gameService.updateGame(id, game);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_UPDATE_GAME);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_UPDATE_GAME + e.getMessage());
        }
        return REDIRECT_GAMES;
    }

    @GetMapping(ENDPOINT_GAMES_DELETE)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String deleteGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gameService.deleteGame(id);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_DELETE_GAME);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_DELETE_GAME + e.getMessage());
        }
        return REDIRECT_GAMES;
    }

    @GetMapping(ENDPOINT_GAMES_EXECUTE)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String executeGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gameService.executeGame(id);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_EXECUTE_GAME);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_EXECUTE_GAME + e.getMessage());
        }
        return REDIRECT_GAMES;
    }
}

