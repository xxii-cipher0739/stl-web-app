package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
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

    public void createRequest(Request request) {

        // Generate reference if not provided
        if (request.getReference() == null || request.getReference().isEmpty()) {
            request.setReference(generateReference());
        }

        //Set current user as requestor
        request.setRequestedBy(userService.getCurrentUserId());

        // Set processed to false for new requests
        request.setProcessed(false);
        // Set Date Time now as created date
        request.setDateTimeCreated(LocalDateTime.now());

        // Set initial status to PENDING
        request.setStatus(RequestStatus.PENDING);

        //Save request
        Request createdRequest = requestRepository.save(request);

        //Save transaction
        transactionService.createTransaction(
                createdRequest.getReference(),
                TransactionType.REQUEST,
                createdRequest.getAmount(),
                createdRequest.getStatus().name());

    }

    public Request validateUpdateRequest(Long id, Request request) {

        //Get Request
        Request existingRequest = getRequestById(id);

        //Validate if request is already processed
        validateIfIsProcessed(request);

        //Update requestedBy, status, and date time created
        request.setRequestedBy(existingRequest.getRequestedBy());
        request.setStatus(existingRequest.getStatus());
        request.setDateTimeCreated(existingRequest.getDateTimeCreated());

        return request;
    }
    public void updateRequest(Request request) {
        requestRepository.save(request);
    }

    public void processRequest(Long id, RequestStatus status) {

        //Get request
        Request request = getRequestById(id);

        //Validate if request is already processed
        validateIfIsProcessed(request);

        //Update process and status
        request.setProcessed(true);
        request.setStatus(status);

        //Transfer amount if approved
        if (RequestStatus.APPROVED == status) {
            walletService.transferAmount(
                    request.getRequestedBy(),
                    request.getRequestedTo(),
                    request.getAmount()
            );
        }

        //Save request
        requestRepository.save(request);

        //Save transaction
        transactionService.createTransaction(
                request.getReference(),
                TransactionType.REQUEST,
                request.getAmount(),
                request.getStatus().name());
        
    }

    private void validateIfIsProcessed(Request request) {
        if (request.isProcessed()) {
            throw new IllegalStateException("Request already processed.");
        }
    }

    private String generateReference() {
        // Generate a unique reference code (REQ-UUID)
        return TransactionType.REQUEST.getValue() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
