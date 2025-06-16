package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
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

        // Set Date Time now as created date
        request.setDateTimeCreated(LocalDateTime.now());

        // Set processed to false for new requests
        request.setProcessed(false);

        // Set initial status to PENDING
        if (request.getStatus() == null) {
            request.setStatus(RequestStatus.PENDING);
        }

        Request createdRequest = requestRepository.save(request);

        transactionService.createTransaction(
                createdRequest.getReference(),
                TransactionType.REQUEST,
                createdRequest.getAmount(),
                createdRequest.getStatus().name());

        return createdRequest;
    }

    public Request updateRequest(Request request) {
        return requestRepository.save(request);
    }

    public Request approveRequest(Long id) {
        return processRequest(id, RequestStatus.APPROVED);
    }

    public Request rejectRequest(Long id) {
        return processRequest(id, RequestStatus.REJECTED);
    }

    public Request cancelRequest(Long id) {
        return processRequest(id, RequestStatus.CANCELLED);
    }

    private String generateReference() {
        // Generate a unique reference code (REQ-UUID)
        return TransactionType.REQUEST.getValue() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Request processRequest(Long id, RequestStatus status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        request.setProcessed(true);
        request.setStatus(status);

        if (RequestStatus.APPROVED == status) {
            walletService.adjustWallets(request.getRequestedBy(), request.getRequestedTo(), request.getAmount());
        }

        transactionService.createTransaction(
                request.getReference(),
                TransactionType.REQUEST,
                request.getAmount(),
                request.getStatus().name());

        return requestRepository.save(request);
    }
}
