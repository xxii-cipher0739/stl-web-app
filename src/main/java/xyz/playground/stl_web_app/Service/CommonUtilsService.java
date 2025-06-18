package xyz.playground.stl_web_app.Service;

import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.TransactionType;

import java.util.UUID;

@Service
public class CommonUtilsService {

    public String generateReference(TransactionType transactionType) {
        return transactionType.getValue() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
