package xyz.playground.stl_web_app.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.playground.stl_web_app.Constants.Action;
import xyz.playground.stl_web_app.Constants.TransactionFlow;
import xyz.playground.stl_web_app.Constants.TransactionType;
import xyz.playground.stl_web_app.Model.Game;
import xyz.playground.stl_web_app.Model.Request;
import xyz.playground.stl_web_app.Model.Transaction;
import xyz.playground.stl_web_app.Repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    UserService userService;

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction createTransaction(String reference, TransactionType type, BigDecimal amount, String status) {
        return null;
    }

    public void saveTransaction(Request request, Action action) {

        Transaction transaction = new Transaction();

        transaction.setReference(request.getReference());
        transaction.setTransactionFlow(TransactionFlow.NOT_APPLICABLE);
        transaction.setTargetId(request.getRequestedTo());
        transaction.setAmount(request.getAmount());
        transaction.setAction(action);

        saveTransaction(transaction);
    }

    public void saveTransaction(Request request, Action action, TransactionFlow transactionFlow, Long targetUser) {

        Transaction transaction = new Transaction();

        transaction.setReference(request.getReference());
        transaction.setTransactionFlow(transactionFlow);
        transaction.setTargetId(targetUser);
        transaction.setAmount(request.getAmount());
        transaction.setAction(action);

        saveTransaction(transaction);
    }

    public void saveTransaction(Game game, Action action) {

        Transaction transaction = new Transaction();

        transaction.setReference(game.getReference());
        transaction.setTransactionFlow(TransactionFlow.NOT_APPLICABLE);
        transaction.setAmount(new BigDecimal(0));
        transaction.setAction(action);

        saveTransaction(transaction);
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {

        //Set Date
        transaction.setDatetimeStamp(LocalDateTime.now());
        transaction.setActorId(userService.getCurrentUserId());

        transactionRepository.save(transaction);
    }
    public List<Transaction> getRecentTransactions(int limit) {
        return transactionRepository.findRecentTransactions(limit);
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Transaction> getRecentTransactionsByUserId(Long userId, int limit) {
        return transactionRepository.findRecentTransactionsByUserId(userId, limit);
    }

    public Page<Transaction> searchTransactions(String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return transactionRepository.findAll(pageable);
        }
        return transactionRepository.findByReferenceContaining(reference.trim(), pageable);
    }

    public Page<Transaction> searchTransactionsByUser(Long userId, String reference, Pageable pageable) {
        if (reference == null || reference.trim().isEmpty()) {
            return transactionRepository.findByUserInvolved(userId, pageable);
        }
        return transactionRepository.findByUserInvolvedAndReferenceContaining(userId, reference.trim(), pageable);
    }

}
