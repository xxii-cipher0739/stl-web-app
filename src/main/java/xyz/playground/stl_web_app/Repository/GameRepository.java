package xyz.playground.stl_web_app.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Model.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    // Pagination methods
    Page<Game> findAll(Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.reference LIKE %:reference%")
    Page<Game> findByReferenceContaining(@Param("reference") String reference, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE g.scheduleDateTime > :now ORDER BY g.scheduleDateTime ASC")
    List<Game> findUpcomingGames(LocalDateTime now);

    @Query("SELECT g FROM Game g WHERE g.cutOffDateTime > :now ORDER BY g.scheduleDateTime ASC")
    List<Game> findActiveGames(LocalDateTime now);
}
