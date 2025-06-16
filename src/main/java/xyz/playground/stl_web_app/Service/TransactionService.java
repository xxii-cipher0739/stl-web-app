package xyz.playground.stl_web_app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.CustomUserDetails;
import xyz.playground.stl_web_app.Model.Transaction;
import xyz.playground.stl_web_app.Repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(String reference, TransactionType type, BigDecimal amount, String status) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long currentUserId = userDetails.getId();

        Transaction transaction = new Transaction();

        transaction.setReference(reference);
        transaction.setTransactionType(type.name());
        transaction.setPerformedBy(currentUserId);
        transaction.setAmount(amount);
        transaction.setDatetimeStamp(LocalDateTime.now());
        transaction.setAction(status);

        return transactionRepository.save(transaction);
    }

}
