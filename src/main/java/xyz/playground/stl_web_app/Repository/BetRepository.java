package xyz.playground.stl_web_app.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.playground.stl_web_app.Constants.BetStatus;
import xyz.playground.stl_web_app.Model.Bet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BetRepository extends JpaRepository<Bet, Long> {
    Optional<Bet> findByReference(String reference);

    List<Bet> findByCreatedBy(Long userId);

    List<Bet> findByGameId(Long gameId);

    List<Bet> findByStatus(BetStatus status);

    @Query("SELECT b FROM Bet b WHERE b.createdBy = :userId AND b.dateTimeCreated >= :startDate AND b.dateTimeCreated <= :endDate")
    List<Bet> findByCreatedByAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(b) FROM Bet b WHERE b.createdBy = :userId AND b.dateTimeCreated >= :startDate AND b.dateTimeCreated <= :endDate")
    Long countBetsByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
