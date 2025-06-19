package xyz.playground.stl_web_app.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.Action;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Constants.TransactionFlow;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static xyz.playground.stl_web_app.Constants.StringConstants.ADMIN_ROLE;

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

    @Autowired
    private CommonUtilsService commonUtilsService;

    public Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));
    }

    public List<Request> getPendingRequestsForUser(Long userId) {
        return requestRepository.findPendingRequestsForUser(userId);
    }

    public Page<Request> searchRequests(String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return requestRepository.findAll(pageable);
        }
        return requestRepository.findByReferenceContaining(reference.trim(), pageable);
    }

    public Page<Request> searchRequestsByUser(Long userId, String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return requestRepository.findByUserInvolved(userId, pageable);
        }
        return requestRepository.findByUserInvolvedAndReferenceContaining(userId, reference.trim(), pageable);
    }

    @Transactional
    public void createRequest(Request request) {

        // Generate reference if not provided
        if (request.getReference() == null || request.getReference().isEmpty()) {
            request.setReference(commonUtilsService.generateReference(TransactionType.REQUEST));
        }

        //Set current user as requestor
        request.setRequestedBy(userService.getCurrentUserId());

        // Set processed to false for new requests
        request.setProcessed(false);
        // Set Date Time now as created date
        request.setDateTimeCreated(LocalDateTime.now());

        // Set initial status to PENDING
        request.setStatus(RequestStatus.PENDING);

        //Save Transaction
        transactionService.saveTransaction(request, Action.CREATE_REQUEST);

        //Save request
        requestRepository.save(request);
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

    @Transactional
    public void updateRequest(Request request) {

        if(request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot update request not on pending status.");
        }

        //Save Transaction
        transactionService.saveTransaction(request, Action.UPDATE_REQUEST);

        //Save Request
        requestRepository.save(request);
    }

    @Transactional
    public void processRequest(Long id, RequestStatus status) {

        //Get request
        Request request = getRequestById(id);

        //Validate if request is already processed
        validateIfIsProcessed(request);

        //Update process and status
        if (RequestStatus.SUBMITTED != status) {
            request.setProcessed(true);
        }

        request.setStatus(status);

        //Transfer amount if approved
        if (RequestStatus.APPROVED == status) {
            walletService.transferAmount(
                    request.getRequestedBy(),
                    request.getRequestedTo(),
                    request.getAmount());

            transactionService.saveTransaction(request, Action.APPROVE_REQUEST, TransactionFlow.IN, request.getRequestedBy());

            User searchUser = userService.getUserById(request.getRequestedTo());

            if (!searchUser.hasRole(ADMIN_ROLE)) {
                transactionService.saveTransaction(request, Action.APPROVE_REQUEST, TransactionFlow.OUT, request.getRequestedTo());
            }
        }

        if (RequestStatus.SUBMITTED == status) {
            transactionService.saveTransaction(request, Action.SUBMIT_REQUEST);
        }

        if (RequestStatus.REJECTED == status) {
            transactionService.saveTransaction(request, Action.REJECT_REQUEST);
        }

        if (RequestStatus.CANCELLED == status) {
            transactionService.saveTransaction(request, Action.CANCEL_REQUEST);
        }

        //Save request
        requestRepository.save(request);
    }

    private void validateIfIsProcessed(Request request) {
        if (request.isProcessed()) {
            throw new IllegalStateException("Request already processed.");
        }
    }

}
