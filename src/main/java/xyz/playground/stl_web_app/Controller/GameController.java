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

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public String listGames(Model model) {
        model.addAttribute("games", gameService.getAllGames());
        model.addAttribute("pageTitle", "Games - STL Web App");
        model.addAttribute("activeTab", "games");
        model.addAttribute("viewName", "games/list");
        return "layout/main";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        Game game = new Game();
        // Set default values for new game
        game.setScheduleDateTime(LocalDateTime.now().plusDays(1));
        game.setCutOffDateTime(LocalDateTime.now().plusDays(1).minusHours(1));
        game.setEnabled(true);

        model.addAttribute("game", game);
        model.addAttribute("gameTypes", GameType.values());
        model.addAttribute("pageTitle", "Add Game - STL Web App");
        model.addAttribute("activeTab", "games");
        model.addAttribute("viewName", "games/form");
        return "layout/main";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addGame(@ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        try {
            gameService.createGame(game);
            redirectAttributes.addFlashAttribute("successMessage", "Game created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating game: " + e.getMessage());
        }
        return "redirect:/games";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Game game = gameService.getGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid game Id:" + id));

        // Format dates for datetime-local input
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedScheduleDate = game.getScheduleDateTime().format(formatter);
        String formattedCutOffDate = game.getCutOffDateTime().format(formatter);

        model.addAttribute("game", game);
        model.addAttribute("formattedScheduleDate", formattedScheduleDate);
        model.addAttribute("formattedCutOffDate", formattedCutOffDate);
        model.addAttribute("gameTypes", GameType.values());
        model.addAttribute("pageTitle", "Edit Game - STL Web App");
        model.addAttribute("activeTab", "games");
        model.addAttribute("viewName", "games/form");
        return "layout/main";
    }


    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateGame(@PathVariable Long id, @ModelAttribute Game game, RedirectAttributes redirectAttributes) {
        try {
            gameService.updateGame(id, game);
            redirectAttributes.addFlashAttribute("successMessage", "Game updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating game: " + e.getMessage());
        }
        return "redirect:/games";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gameService.deleteGame(id);
            redirectAttributes.addFlashAttribute("successMessage", "Game deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting game: " + e.getMessage());
        }
        return "redirect:/games";
    }

    @GetMapping("/execute/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String executeGame(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            gameService.executeGame(id);
            redirectAttributes.addFlashAttribute("successMessage", "Game executed successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error executing game: " + e.getMessage());
        }
        return "redirect:/games";
    }
}

