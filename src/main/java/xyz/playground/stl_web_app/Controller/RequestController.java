package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Service.CommonUtilsService;
import xyz.playground.stl_web_app.Service.RequestService;
import xyz.playground.stl_web_app.Service.UserService;
import xyz.playground.stl_web_app.Service.WalletService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static xyz.playground.stl_web_app.Constants.RequestStatus.*;
import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller

public class RequestController {

    private final String NEW_REQUEST = "newRequest";
    private final String REQUESTS = "requests";
    private final String USERS = "users";
    private final String DEFAULT_REQUEST_SORT = "dateTimeCreated";

    private final String VAR_WALLET_BALANCE = "walletBalance";
    private final String VAR_REQUESTS_LIST = "requestList";
    private final String VAR_REQUESTS_STATUSES = "requestStatuses";
    private final String VAR_CURRENT_USER_ID = "currentUserId";
    private final String VAR_REQUEST_BY_USERS = "requestByUser";
    private final String VAR_REQUEST_TO_USERS = "requestToUsers";

    private final String ENDPOINT_REQUESTS = "/requests";
    private final String ENDPOINT_ADD_REQUESTS = "/requests/add";
    private final String ENDPOINT_EDIT_REQUESTS = "/requests/edit/{id}";
    private final String ENDPOINT_UPDATE_REQUESTS = "/requests/update/{id}";
    private final String ENDPOINT_APPROVE_REQUESTS = "/requests/approve/{id}";
    private final String ENDPOINT_REJECT_REQUESTS = "/requests/reject/{id}";
    private final String ENDPOINT_CANCEL_REQUESTS = "/requests/cancel/{id}";

    private final String ENDPOINT_REQUESTS_LIST = "requests/list";
    private final String ENDPOINT_REQUESTS_FORM = "requests/form";
    private final String REDIRECT_REQUESTS = "redirect:/requests";

    private final String REQUEST_TITLE = "Requests - View";
    private final String REQUEST_TITLE_ADD = "Request - Add Request";
    private final String REQUEST_TITLE_EDIT ="Request - Edit Request";

    private final String ERROR_ADD_REQUEST = "Error creating request: ";
    private final String ERROR_UPDATE_REQUEST = "Error updating request: ";
    private final String ERROR_APPROVE_REQUEST = "Error approving request: ";
    private final String ERROR_REJECT_REQUEST = "Error rejecting request: ";
    private final String ERROR_CANCEL_REQUEST = "Error cancelling request: ";

    private final String SUCCESSFUL_ADD_REQUEST = "Request created successfully";
    private final String SUCCESSFUL_UPDATE_REQUEST = "Request updated successfully";
    private final String SUCCESSFUL_APPROVE_REQUEST = "Request approved successfully";
    private final String SUCCESSFUL_REJECT_REQUEST = "Request rejected successfully";
    private final String SUCCESSFUL_CANCEL_REQUEST = "Request cancelled successfully";

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CommonUtilsService commonUtilsService;

    @GetMapping(ENDPOINT_REQUESTS)
    public String listRequests(
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(defaultValue = DEFAULT_REQUEST_SORT) String sort,
            @RequestParam(defaultValue = DESC) String direction,
            @RequestParam(required = false) String search,
            Model model) {

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = userService.getCurrentUserId(auth);

        //Get Pageable
        Pageable pageable = commonUtilsService.getPageable(direction, sort, size, page);

        Page<Request> requestPage;

        // If admin, show all requests
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            requestPage = requestService.searchRequests(search, pageable);
        } else {
            // For regular users, show requests they're involved in
            requestPage = requestService.searchRequestsByUser(currentUserId, search, pageable);
        }

        //Get wallet balance
        BigDecimal balance = walletService.getWalletBalance(currentUserId);

        // Get all users for display purposes
        List<User> users = userService.getAllUsers();

        model.addAttribute(VAR_REQUESTS_LIST, requestPage.getContent());
        model.addAttribute(USERS, users);
        model.addAttribute(VAR_WALLET_BALANCE, balance);
        model.addAttribute(VAR_CURRENT_USER_ID, currentUserId);
        model.addAttribute(VAR_REQUESTS_STATUSES, RequestStatus.values());
        model.addAttribute(PAGE, requestPage);
        model.addAttribute(SEARCH, search);
        model.addAttribute(SORT, sort);
        model.addAttribute(DIRECTION, direction);
        model.addAttribute(PAGE_TITLE, REQUEST_TITLE);
        model.addAttribute(ACTIVE_TAB, REQUESTS);
        model.addAttribute(VIEW_NAME, ENDPOINT_REQUESTS_LIST);
        return MAIN_LAYOUT;
    }

    @GetMapping(ENDPOINT_ADD_REQUESTS)
    public String showAddForm(Model model) {

        Request request = new Request();

        Long currentUserId = userService.getCurrentUserId();

        // Set the requestedBy field to current user
        request.setRequestedBy(currentUserId);

        // Get current user
        List<User> requestByUser = new ArrayList<>();
        requestByUser.add(userService.getActiveUser(currentUserId));

        // Get all dispatcher and admins only for requestTo
        List<User> requestToUsers = userService.getAllDispatchAndAdmins();

        model.addAttribute(NEW_REQUEST, request);
        model.addAttribute(VAR_REQUEST_BY_USERS, requestByUser);
        model.addAttribute(VAR_REQUEST_TO_USERS, requestToUsers);
        model.addAttribute(VAR_CURRENT_USER_ID, currentUserId);
        model.addAttribute(VAR_REQUESTS_STATUSES, RequestStatus.values());
        model.addAttribute(PAGE_TITLE, REQUEST_TITLE_ADD);
        model.addAttribute(ACTIVE_TAB, REQUESTS);
        model.addAttribute(VIEW_NAME, ENDPOINT_REQUESTS_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_ADD_REQUESTS)
    @PreAuthorize(ROLE_HAS_ANY_COLLECTOR_AND_DISPATCHER)
    public String addRequest(@ModelAttribute Request request, RedirectAttributes redirectAttributes) {
        return handleRequest(
                value -> requestService.createRequest(value),
                request,
                redirectAttributes,
                SUCCESSFUL_ADD_REQUEST,
                ERROR_ADD_REQUEST);
    }

    @GetMapping(ENDPOINT_EDIT_REQUESTS)
    public String showEditForm(@PathVariable Long id, Model model) {
        Request request = requestService.getRequestById(id);

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        // Get current user
        List<User> requestByUser = new ArrayList<>();
        requestByUser.add(userService.getActiveUser(currentUserId));

        // Get all dispatcher and admins only for requestTo
        List<User> requestToUsers = userService.getAllDispatchAndAdmins();

        model.addAttribute(NEW_REQUEST, request);
        model.addAttribute(VAR_REQUEST_BY_USERS, requestByUser);
        model.addAttribute(VAR_REQUEST_TO_USERS, requestToUsers);
        model.addAttribute(VAR_CURRENT_USER_ID, currentUserId);
        model.addAttribute(VAR_REQUESTS_STATUSES, RequestStatus.values());
        model.addAttribute(PAGE_TITLE, REQUEST_TITLE_EDIT);
        model.addAttribute(ACTIVE_TAB, REQUESTS);
        model.addAttribute(VIEW_NAME, ENDPOINT_REQUESTS_FORM);
        return MAIN_LAYOUT;
    }

    @PostMapping(ENDPOINT_UPDATE_REQUESTS)
    public String updateRequest(@PathVariable Long id, @ModelAttribute Request request, RedirectAttributes redirectAttributes) {

        return handleRequest(
                value -> requestService.updateRequest(value),
                requestService.validateUpdateRequest(id, request),
                redirectAttributes,
                SUCCESSFUL_UPDATE_REQUEST,
                ERROR_UPDATE_REQUEST);
    }

    @GetMapping(ENDPOINT_APPROVE_REQUESTS)
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleRequest(
                value -> requestService.processRequest(value, APPROVED),
                id,
                redirectAttributes,
                SUCCESSFUL_APPROVE_REQUEST,
                ERROR_APPROVE_REQUEST);
    }

    @GetMapping(ENDPOINT_REJECT_REQUESTS)
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleRequest(
                value -> requestService.processRequest(value, REJECTED),
                id,
                redirectAttributes,
                SUCCESSFUL_REJECT_REQUEST,
                ERROR_REJECT_REQUEST);
    }

    @GetMapping(ENDPOINT_CANCEL_REQUESTS)
    public String cancelRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return handleRequest(
                value -> requestService.processRequest(value, CANCELLED),
                id,
                redirectAttributes,
                SUCCESSFUL_CANCEL_REQUEST,
                ERROR_CANCEL_REQUEST);
    }

    private <T> String handleRequest (Consumer<T> consumer, T param, RedirectAttributes redirectAttributes, String successMessage, String errorMessage) {
        try {
            consumer.accept(param);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, successMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, errorMessage + e.getMessage());
        }
        return REDIRECT_REQUESTS;
    }
}
