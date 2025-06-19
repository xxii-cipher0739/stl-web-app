package xyz.playground.stl_web_app.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.playground.stl_web_app.Model.Bet;

public interface BetRepository extends JpaRepository<Bet, Long> {

    @Query("SELECT b FROM Bet b WHERE b.reference LIKE %:reference%")
    Page<Bet> findByReferenceContaining(@Param("reference") String reference, Pageable pageable);

    @Query("SELECT b FROM Bet b WHERE b.createdBy = :userId")
    Page<Bet> findByUserInvolved(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT b FROM Bet b WHERE b.createdBy = :userId AND b.reference LIKE %:reference%")
    Page<Bet> findByUserInvolvedAndReferenceContaining(@Param("userId") Long userId, @Param("reference") String reference, Pageable pageable);
}
