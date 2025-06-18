package xyz.playground.stl_web_app.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.playground.stl_web_app.Constants.TransactionType;

import java.util.UUID;
import java.util.function.Consumer;

import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_ERROR_MESSAGE;
import static xyz.playground.stl_web_app.Constants.StringConstants.VAR_SUCCESS_MESSAGE;

@Service
public class CommonUtilsService {

    public String generateReference(TransactionType transactionType) {
        return transactionType.getValue() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public  <T> String handleRequest (Consumer<T> consumer,
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
