package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Users");
        model.addAttribute("activeTab", "users");
        model.addAttribute("viewName", "users/list");
        return "layout/main";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Add User");
        model.addAttribute("activeTab", "users");
        model.addAttribute("viewName", "users/form");
        return "layout/main";
    }


    @PostMapping("/add")
    public String addUser(@ModelAttribute User user, @RequestParam String role) {
        Set<String> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("activeTab", "users");
        model.addAttribute("viewName", "users/form");
        return "layout/main";
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user,
                             @RequestParam String role, @RequestParam(required = false) String updatePassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        existingUser.setName(user.getName());
        existingUser.setUsername(user.getUsername());

        if (updatePassword != null && !updatePassword.isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatePassword));
        }

        Set<String> roles = new HashSet<>();
        roles.add(role);
        existingUser.setRoles(roles);
        existingUser.setEnabled(user.isEnabled());

        userRepository.save(existingUser);
        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
