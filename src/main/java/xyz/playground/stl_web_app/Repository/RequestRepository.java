package xyz.playground.stl_web_app.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.playground.stl_web_app.Model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByReference(String reference);

    List<Request> findByRequestedBy(Long userId);

    List<Request> findByRequestedTo(Long userId);

    List<Request> findByProcessed(boolean processed);

    @Query("SELECT r FROM Request r WHERE r.requestedTo = :userId AND r.processed = false")
    List<Request> findPendingRequestsForUser(@Param("userId") Long userId);

    // Pagination methods
    Page<Request> findAll(Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.reference LIKE %:reference%")
    Page<Request> findByReferenceContaining(@Param("reference") String reference, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE (r.requestedBy = :userId OR r.requestedTo = :userId)")
    Page<Request> findByUserInvolved(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE (r.requestedBy = :userId OR r.requestedTo = :userId) AND r.reference LIKE %:reference%")
    Page<Request> findByUserInvolvedAndReferenceContaining(@Param("userId") Long userId, @Param("reference") String reference, Pageable pageable);
}
