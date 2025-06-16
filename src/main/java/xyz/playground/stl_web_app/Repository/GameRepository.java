package xyz.playground.stl_web_app.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.playground.stl_web_app.Model.Game;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g WHERE g.scheduleDateTime > :now AND g.enabled = true ORDER BY g.scheduleDateTime ASC")
    List<Game> findUpcomingGames(LocalDateTime now);

    @Query("SELECT g FROM Game g WHERE g.cutOffDateTime > :now AND g.enabled = true ORDER BY g.scheduleDateTime ASC")
    List<Game> findActiveGames(LocalDateTime now);
}
