package xyz.playground.stl_web_app.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Request;

import java.util.UUID;
import java.util.function.Consumer;

import static xyz.playground.stl_web_app.Constants.StringConstants.*;

@Service
public class CommonUtilsService {

    public String generateReference(TransactionType transactionType) {
        return transactionType.getValue() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public Pageable getPageable(String direction, String sort, int size, int page) {

        // Create pageable
        Sort.Direction sortDirection = direction.equalsIgnoreCase(DESC) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(sortDirection, sort));

    }
    public <T> String handleRequest (Consumer<T> consumer,
                                      T param,
                                      RedirectAttributes redirectAttributes,
                                      String successMessage,
                                      String errorMessage,
                                      String redirectPage) {
        try {
            consumer.accept(param);
            redirectAttributes.addFlashAttribute(VAR_SUCCESS_MESSAGE, successMessage);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(VAR_ERROR_MESSAGE, errorMessage + e.getMessage());
        }
        return redirectPage;
    }
}
