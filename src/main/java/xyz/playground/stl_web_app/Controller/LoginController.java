package xyz.playground.stl_web_app.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activeTab", "dashboard");
        model.addAttribute("viewName", "dashboard");
        return "layout/main";
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

}

