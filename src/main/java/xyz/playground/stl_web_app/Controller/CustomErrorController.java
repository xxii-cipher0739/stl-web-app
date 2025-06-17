package xyz.playground.stl_web_app.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private final String ENDPOINT_ERROR = "/error";

    @RequestMapping(ENDPOINT_ERROR)
    public String handleError(HttpServletRequest request, Model model) {

        final String ERROR = "error";
        final String VAR_STATUS = "status";
        final String VAR_ERROR = "error";
        final String VAR_MESSAGE = "message";

        final String PAGE_NOT_FOUND = "Page Not Found";
        final String PAGE_NOT_FOUND_MESSAGE = "The page you are looking for does not exist.";

        final String ACCESS_DENIED = "Access Denied";
        final String ACCESS_DENIED_MESSAGE = "You don't have permission to access this resource.";

        final String INTERNAL_SERVER_ERROR = "Internal Server Error";
        final String INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong on our end. Please try again later.";

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            model.addAttribute(VAR_STATUS, statusCode);

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute(VAR_ERROR, PAGE_NOT_FOUND);
                model.addAttribute(VAR_MESSAGE, PAGE_NOT_FOUND_MESSAGE);
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute(VAR_ERROR, ACCESS_DENIED);
                model.addAttribute(VAR_MESSAGE, ACCESS_DENIED_MESSAGE);
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute(VAR_ERROR, INTERNAL_SERVER_ERROR);
                model.addAttribute(VAR_MESSAGE, INTERNAL_SERVER_ERROR_MESSAGE);
            }
        }

        return ERROR;
    }
}
