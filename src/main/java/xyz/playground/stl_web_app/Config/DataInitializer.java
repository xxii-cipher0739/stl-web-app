package xyz.playground.stl_web_app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Service.UserService;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) {
        // Only create default users if no users exist
        if (userRepository.count() == 0) {
            userService.createUser("admin", "admin123", "ADMIN");
            userService.createUser("user", "user123", "USER");
        }
    }
}
