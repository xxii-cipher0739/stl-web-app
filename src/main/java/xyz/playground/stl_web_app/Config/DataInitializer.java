package xyz.playground.stl_web_app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.playground.stl_web_app.Constants.Role;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Service.UserService;
import xyz.playground.stl_web_app.Service.WalletService;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Override
    public void run(String... args) {
        // Only create default users if no users exist
        if (userRepository.count() == 0) {

            User user1 = userService.createUser("Administrator","admin", "password1234", Role.ADMIN.toString());
            User user2 = userService.createUser("Test Dispatcher","user_dispatcher", "password1234", Role.DISPATCHER.toString());
            User user3 = userService.createUser("Test Collector","user_collector", "password1234", Role.COLLECTOR.toString());

            walletService.createWallet(user1.getId());
            walletService.createWallet(user2.getId());
            walletService.createWallet(user3.getId());

        }
    }
}
