package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(String name, String username, String password, String role) {

        Set<String> roles = new HashSet<>();
        roles.add(role);

        User createdUser = userRepository.save(new User(name,
                username,
                passwordEncoder.encode(password),
                roles)
        );

        //walletService.createWallet(createdUser.getId());

        return createdUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllDispatchAndAdmins() {
        return userRepository.findDispatchersAndAdmins();
    }

    public User getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));

        return user;
    }

    public User getActiveUser(Long id) {
        User user = getUserById(id);

        if (!user.isEnabled()) {
            throw new IllegalStateException("User is inactive");
        }

        return user;
    }

    public CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public CustomUserDetails getCurrentUser(Authentication authentication) {
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public Long getCurrentUserId(Authentication authentication) {
        return getCurrentUser(authentication).getId();
    }


}
