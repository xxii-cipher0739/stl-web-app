package xyz.playground.stl_web_app.Controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(ENDPOINT_ERROR)
    public String handleError(HttpServletRequest request, Model model) {
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

        return "error";
    }
}
