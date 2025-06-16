package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Model.Wallet;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Repository.WalletRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

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

        walletService.createWallet(createdUser.getId());

        return createdUser;
    }

    public User findActiveUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id:" + id));
    }
}
