package xyz.playground.stl_web_app.Repository;

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
}
