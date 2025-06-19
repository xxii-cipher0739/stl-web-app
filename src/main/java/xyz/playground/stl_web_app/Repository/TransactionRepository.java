package xyz.playground.stl_web_app.Repository;

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

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :performedBy ORDER BY t.datetimeStamp DESC")
    List<Transaction> findByUserIdOrderByCreatedAtDesc(@Param("performedBy") Long performedBy);

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :performedBy ORDER BY t.datetimeStamp DESC LIMIT :limit")
    List<Transaction> findRecentTransactionsByUserId(@Param("performedBy") Long performedBy, @Param("limit") int limit);

}
