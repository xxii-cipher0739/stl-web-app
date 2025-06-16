package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Service.UserService;

import java.util.HashSet;
import java.util.Set;

import static xyz.playground.stl_web_app.Constants.StringConstants.ACTIVE_TAB;
import static xyz.playground.stl_web_app.Constants.StringConstants.PAGE_TITLE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VIEW_NAME;
import static xyz.playground.stl_web_app.Constants.StringConstants.MAIN_LAYOUT;
import static xyz.playground.stl_web_app.Constants.StringConstants.ROLE_HAS_ADMIN;
import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_SUCCESS_MESSAGE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_ERROR_MESSAGE;

@Controller
@PreAuthorize(ROLE_HAS_ADMIN)
public class UserController {

    private final String NEW_USER = "newUser";
    private final String USERS = "users";

    private final String VAR_USER_LIST = "userList";

    private final String ENDPOINT_USERS = "/users";
    private final String ENDPOINT_USERS_ADD = "/users/add";
    private final String ENDPOINT_USERS_EDIT = "/users/edit/{id}";
    private final String ENDPOINT_USERS_UPDATE = "/users/update/{id}";
    private final String ENDPOINT_USERS_DELETE = "/users/delete/{id}";
    private final String ENDPOINT_USER_LIST = "users/list";
    private final String ENDPOINT_USER_FORM = "users/form";
    private final String REDIRECT_USERS = "redirect:/users";

    private final String USERS_TITLE = "Users - View";
    private final String USERS_ADD_TITLE = "Users - Add User";
    private final String USERS_EDIT_TITLE = "Users - Edit User";

    private final String INVALID_USER_ID = "Invalid user id:";
    private final String ERROR_ADD_USER = "Error creating user: ";
    private final String ERROR_UPDATE_USER = "Error updating user: ";
    private final String ERROR_DELETE_USER = "Error deleting user: ";
    private final String SUCCESSFUL_ADD_USER = "User created successfully";
    private final String SUCCESSFUL_UPDATE_USER = "User updated successfully";
    private final String SUCCESSFUL_DELETE_USER = "User deleted successfully";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(ENDPOINT_USERS)
    public String listUsers(Model model) {
        model.addAttribute(VAR_USER_LIST, userRepository.findAll());
        model.addAttribute(PAGE_TITLE, USERS_TITLE);
        model.addAttribute(ACTIVE_TAB, USERS);
        model.addAttribute(VIEW_NAME, ENDPOINT_USER_LIST);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_USERS_ADD)
    public String showAddForm(Model model) {
        model.addAttribute(NEW_USER, new User());
        model.addAttribute(PAGE_TITLE, USERS_ADD_TITLE);
        model.addAttribute(ACTIVE_TAB, USERS);
        model.addAttribute(VIEW_NAME, ENDPOINT_USER_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_USERS_ADD)
    public String addUser(@ModelAttribute User user, @RequestParam String role, RedirectAttributes redirectAttributes) {
        try {
            Set<String> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);

            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_ADD_USER);

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_ADD_USER + e.getMessage());
        }

        return REDIRECT_USERS;
    }

    @GetMapping(ENDPOINT_USERS_EDIT)
    public String showEditForm(@PathVariable Long id, Model model) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_USER_ID + id));

        model.addAttribute(NEW_USER, user);
        model.addAttribute(PAGE_TITLE, USERS_EDIT_TITLE);
        model.addAttribute(ACTIVE_TAB, USERS);
        model.addAttribute(VIEW_NAME, ENDPOINT_USER_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_USERS_UPDATE)
    public String updateUser(@PathVariable Long id, @ModelAttribute User user,
                             @RequestParam String role, @RequestParam(required = false) String updatePassword,
                             RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException(INVALID_USER_ID + id));

            existingUser.setName(user.getName());

            if (updatePassword != null && !updatePassword.isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(updatePassword));
            }

            Set<String> roles = new HashSet<>();
            roles.add(role);
            existingUser.setRoles(roles);
            existingUser.setEnabled(user.isEnabled());

            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_UPDATE_USER);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_UPDATE_USER + e.getMessage());
        }

        return REDIRECT_USERS;
    }

    @GetMapping(ENDPOINT_USERS_DELETE)
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, SUCCESSFUL_DELETE_USER);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, ERROR_DELETE_USER + e.getMessage());
        }
        return REDIRECT_USERS;
    }
}
