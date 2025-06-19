package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;
import xyz.playground.stl_web_app.Constants.Action;
import xyz.playground.stl_web_app.Constants.TransactionFlow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Date
    @Column
    private LocalDateTime datetimeStamp;

    //Reference (BET/REQ/REF/GAME)
    @Column
    private String reference;

    //Actor (Name)
    @Column
    private Long actorId;

    @Transient
    private String actorName;

    //Target(Name)
    @Column
    private Long targetId;

    @Transient
    private String targetName;

    //Wallet Movement (In, Out, N/A)
    @Column
    private TransactionFlow transactionFlow;

    //Amount
    @Column
    private BigDecimal amount;

    //Transaction Description
    @Column
    private Action action;

    public Transaction() {
    }

    public Transaction(Long id,
                       LocalDateTime datetimeStamp,
                       String reference,
                       Long actorId,
                       String actorName,
                       Long targetId,
                       String targetName,
                       TransactionFlow transactionFlow,
                       BigDecimal amount,
                       Action action) {
        this.id = id;
        this.datetimeStamp = datetimeStamp;
        this.reference = reference;
        this.actorId = actorId;
        this.actorName = actorName;
        this.targetId = targetId;
        this.targetName = targetName;
        this.transactionFlow = transactionFlow;
        this.amount = amount;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDatetimeStamp() {
        return datetimeStamp;
    }

    public void setDatetimeStamp(LocalDateTime datetimeStamp) {
        this.datetimeStamp = datetimeStamp;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public TransactionFlow getTransactionFlow() {
        return transactionFlow;
    }

    public void setTransactionFlow(TransactionFlow transactionFlow) {
        this.transactionFlow = transactionFlow;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
