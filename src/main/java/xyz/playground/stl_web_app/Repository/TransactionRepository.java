package xyz.playground.stl_web_app.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.playground.stl_web_app.Model.Transaction;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t ORDER BY t.datetimeStamp DESC LIMIT :limit")
    List<Transaction> findRecentTransactions(@Param("limit") int limit);

    @Query("SELECT t FROM Transaction t WHERE t.actorId = :userId OR t.targetId = :userId ORDER BY t.datetimeStamp DESC")
    List<Transaction> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long performedBy);

    @Query("SELECT t FROM Transaction t WHERE t.actorId = :userId OR t.targetId = :userId ORDER BY t.datetimeStamp DESC LIMIT :limit")
    List<Transaction> findRecentTransactionsByUserId(@Param("userId") Long performedBy, @Param("limit") int limit);

    // Pagination methods
    Page<Transaction> findAll(Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.reference LIKE %:reference%")
    Page<Transaction> findByReferenceContaining(@Param("reference") String reference, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.actorId = :userId OR t.targetId = :userId)")
    Page<Transaction> findByUserInvolved(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.actorId = :userId OR t.targetId = :userId) AND t.reference LIKE %:reference%")
    Page<Transaction> findByUserInvolvedAndReferenceContaining(@Param("userId") Long userId, @Param("reference") String reference, Pageable pageable);

}
