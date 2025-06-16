package xyz.playground.stl_web_app.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.playground.stl_web_app.Model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByIdAndEnabledTrue(Long id);

    @Query("SELECT u FROM User u WHERE 'DISPATCHER' MEMBER OF u.roles OR 'ADMIN' MEMBER OF u.roles")
    List<User> findDispatchersAndAdmins();
}
