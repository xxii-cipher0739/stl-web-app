package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Repository.RequestRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public Optional<Request> getRequestByReference(String reference) {
        return requestRepository.findByReference(reference);
    }

    public List<Request> getRequestsByRequestedBy(Long userId) {
        return requestRepository.findByRequestedBy(userId);
    }

    public List<Request> getRequestsByRequestedTo(Long userId) {
        return requestRepository.findByRequestedTo(userId);
    }

    public List<Request> getPendingRequestsForUser(Long userId) {
        return requestRepository.findPendingRequestsForUser(userId);
    }

    public List<Request> getProcessedRequests() {
        return requestRepository.findByProcessed(true);
    }

    public List<Request> getPendingRequests() {
        return requestRepository.findByProcessed(false);
    }

    public Request createRequest(Request request) {
        // Generate reference if not provided
        if (request.getReference() == null || request.getReference().isEmpty()) {
            request.setReference(generateReference());
        }

        // Set processed to false for new requests
        request.setProcessed(false);

        // Set initial status to PENDING
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
        }

        return requestRepository.save(request);
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }

    public Request approveRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        request.setProcessed(true);
        request.setStatus(RequestStatus.APPROVED);
        return requestRepository.save(request);
    }

    public Request rejectRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        request.setProcessed(true);
        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }

    public Request cancelRequest(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        request.setProcessed(true);
        request.setStatus(RequestStatus.CANCELLED);
        return requestRepository.save(request);
    }

    public void deleteRequest(Long id) {
        requestRepository.deleteById(id);
    }

    private String generateReference() {
        // Generate a unique reference code (REQ-UUID)
        return "REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
