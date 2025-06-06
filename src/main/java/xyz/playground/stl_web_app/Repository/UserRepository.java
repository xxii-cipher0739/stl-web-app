package xyz.playground.stl_web_app.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.playground.stl_web_app.Model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
