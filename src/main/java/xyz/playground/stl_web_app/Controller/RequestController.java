package xyz.playground.stl_web_app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.RequestStatus;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Model.User;
import xyz.playground.stl_web_app.Repository.UserRepository;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Service.RequestService;

import java.util.ArrayList;
import java.util.List;

import static xyz.playground.stl_web_app.Constants.StringConstants.ROLE_HAS_ANY_COLLECTOR_AND_DISPATCHER;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listRequests(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = getCurrentUserId(auth);

        List<Request> requests;

        // If admin, show all requests
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            requests = requestService.getAllRequests();
        } else {
            // For regular users, show requests they're involved in
            requests = requestService.getRequestsByRequestedBy(currentUserId);
            requests.addAll(requestService.getRequestsByRequestedTo(currentUserId));
        }

        // Get all users for display purposes
        List<User> users = userRepository.findAll();

        model.addAttribute("requests", requests);
        model.addAttribute("users", users);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("requestStatuses", RequestStatus.values());
        model.addAttribute("pageTitle", "Requests - STL Web App");
        model.addAttribute("activeTab", "requests");
        model.addAttribute("viewName", "requests/list");
        return "layout/main";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Request request = new Request();

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = getCurrentUserId(auth);

        // Set the requestedBy field to current user
        request.setRequestedBy(currentUserId);

        // Get current user
        List<User> requestByUser = new ArrayList<>();
        requestByUser.add(
                userRepository.findById(currentUserId)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + currentUserId)));

        // Get all dispatcher and admins only for requestTo
        List<User> requestToUsers = userRepository.findDispatchersAndAdmins();

        model.addAttribute("request", request);
        model.addAttribute("requestByUser", requestByUser);
        model.addAttribute("requestToUsers", requestToUsers);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("requestStatuses", RequestStatus.values());
        model.addAttribute("pageTitle", "Add Request - STL Web App");
        model.addAttribute("activeTab", "requests");
        model.addAttribute("viewName", "requests/form");
        return "layout/main";
    }

    @PostMapping("/add")
    @PreAuthorize(ROLE_HAS_ANY_COLLECTOR_AND_DISPATCHER)
    public String addRequest(@ModelAttribute Request request, RedirectAttributes redirectAttributes) {
        try {

            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long currentUserId = getCurrentUserId(auth);

            // Set the requestedBy field to current user
            request.setRequestedBy(currentUserId);

            requestService.createRequest(request);
            redirectAttributes.addFlashAttribute("successMessage", "Request created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Request request = requestService.getRequestById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request Id:" + id));

        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        // Get all users for the dropdown
        List<User> users = userRepository.findAll();

        model.addAttribute("request", request);
        model.addAttribute("users", users);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("requestStatuses", RequestStatus.values());
        model.addAttribute("pageTitle", "Edit Request - STL Web App");
        model.addAttribute("activeTab", "requests");
        model.addAttribute("viewName", "requests/form");
        return "layout/main";
    }

    @PostMapping("/update/{id}")
    public String updateRequest(@PathVariable Long id, @ModelAttribute Request request, RedirectAttributes redirectAttributes) {
        try {
            // Get current user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long currentUserId = getCurrentUserId(auth);

            // Set the requestedBy field to current user
            request.setRequestedBy(currentUserId);

            requestService.updateRequest(request);
            redirectAttributes.addFlashAttribute("successMessage", "Request updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @GetMapping("/approve/{id}")
    public String approveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.approveRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request approved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @GetMapping("/reject/{id}")
    public String rejectRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request rejected successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error rejecting request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    @GetMapping("/cancel/{id}")
    public String cancelRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            requestService.cancelRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request cancelled successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error cancelling request: " + e.getMessage());
        }
        return "redirect:/requests";
    }

    private Long getCurrentUserId(Authentication auth) {
        // Get current user
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}
