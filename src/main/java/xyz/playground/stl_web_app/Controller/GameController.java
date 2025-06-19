package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.GameType;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Service.CommonUtilsService;
import xyz.playground.stl_web_app.Service.GameService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;


@Controller
public class GameController {

    private final String NEW_GAME = "newGame";
    private final String GAMES = "games";

    private final String DEFAULT_GAME_SORT = "reference";
    private final String VAR_GAME_LIST = "gameList";
    private final String VAR_GAME_TYPES = "gameTypes";
    private final String VAR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm";
    private final String VAR_FORMATTED_SCHEDULED_DATE = "formattedScheduleDate";
    private final String VAR_FORMATTED_CUT_OFF_DATE = "formattedCutOffDate";

    private final String ENDPOINT_GAMES = "/games";
    private final String ENDPOINT_GAMES_ADD = "/games/add";
    private final String ENDPOINT_GAMES_EDIT = "/games/edit/{id}";
    private final String ENDPOINT_GAMES_UPDATE = "/games/update/{id}";
    private final String ENDPOINT_GAMES_FOR_COMPLETION = "/games/for_completion/{id}";
    private final String ENDPOINT_GAMES_COMPLETE = "/games/complete/{id}";
    private final String ENDPOINT_GAMES_START = "/games/start/{id}";
    private final String ENDPOINT_GAMES_CANCEL = "/games/cancel/{id}";

    private final String ENDPOINT_GAME_LIST = "games/list";
    private final String ENDPOINT_GAME_FORM = "games/form";
    private final String REDIRECT_GAMES = "redirect:/games";

    private final String GAMES_TITLE = "Games - View";
    private final String GAMES_ADD_TITLE = "Games - Add Game";
    private final String GAMES_EDIT_TITLE ="Games - Edit Game";

    private final String ERROR_ADD_GAME = "Failed creating game: ";
    private final String ERROR_UPDATE_GAME = "Failed updating game: ";
    private final String ERROR_COMPLETE_GAME = "Failed completing game: ";
    private final String ERROR_START_GAME = "Failed starting game: ";
    private final String ERROR_CANCEL_GAME = "Failed cancelling game: ";
    private final String ERROR_FOR_COMPLETION_GAME = "Failed updating game to for completion: ";
    private final String SUCCESSFUL_ADD_GAME = "Game created successfully";
    private final String SUCCESSFUL_UPDATE_GAME = "Game updated successfully";
    private final String SUCCESSFUL_COMPLETE_GAME = "Game completed successfully";
    private final String SUCCESSFUL_START_GAME = "Game started successfully";
    private final String SUCCESSFUL_CANCEL_GAME = "Game cancelled successfully";
    private final String SUCCESSFUL_FOR_COMPLETION_GAME = "Game set for completion successfully";

    @Autowired
    private GameService gameService;

    @Autowired
    private CommonUtilsService commonUtilsService;

    @GetMapping(ENDPOINT_GAMES)
    public String listGames(@RequestParam(defaultValue = DEFAULT_PAGE) int page,
                            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
                            @RequestParam(defaultValue = DEFAULT_GAME_SORT) String sort,
                            @RequestParam(defaultValue = DESC) String direction,
                            @RequestParam(required = false) String search,
                            Model model) {

        // Create pageable
        Sort.Direction sortDirection = direction.equalsIgnoreCase(DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        Page<Game> gamePage = gameService.searchGames(search, pageable);

        model.addAttribute(VAR_GAME_LIST, gamePage.getContent());
        model.addAttribute(PAGE, gamePage);
        model.addAttribute(SEARCH, search);
        model.addAttribute(SORT, sort);
        model.addAttribute(DIRECTION, direction);
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

        //Set format to enable display in date picker
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
        return commonUtilsService.handleRequest(
                value -> gameService.createGame(value),
                game,
                redirectAttributes,
                SUCCESSFUL_ADD_GAME,
                ERROR_ADD_GAME,
                REDIRECT_GAMES);
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

    @GetMapping(ENDPOINT_GAMES_START)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String startGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> gameService.startGame(value),
                id,
                redirectAttributes,
                SUCCESSFUL_START_GAME,
                ERROR_START_GAME,
                REDIRECT_GAMES);
    }

    @GetMapping(ENDPOINT_GAMES_FOR_COMPLETION)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String processGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> gameService.processGame(value),
                id,
                redirectAttributes,
                SUCCESSFUL_FOR_COMPLETION_GAME,
                ERROR_FOR_COMPLETION_GAME,
                REDIRECT_GAMES);
    }

    @GetMapping(ENDPOINT_GAMES_COMPLETE)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String completeGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> gameService.completeGame(value),
                id,
                redirectAttributes,
                SUCCESSFUL_COMPLETE_GAME,
                ERROR_COMPLETE_GAME,
                REDIRECT_GAMES);
    }

    @GetMapping(ENDPOINT_GAMES_CANCEL)
    @PreAuthorize(ROLE_HAS_ADMIN)
    public String cancelGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return commonUtilsService.handleRequest(
                value -> gameService.cancelGame(value),
                id,
                redirectAttributes,
                SUCCESSFUL_CANCEL_GAME,
                ERROR_CANCEL_GAME,
                REDIRECT_GAMES);
    }
}

