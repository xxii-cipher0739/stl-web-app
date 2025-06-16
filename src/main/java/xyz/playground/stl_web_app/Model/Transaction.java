package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String userName;

    @Column
    private String reference;

    @Column
    private String transactionType;

    @Column
    private Long performedBy;

    @Column
    private BigDecimal amount;

    @Column
    private LocalDateTime datetimeStamp;

    @Column
    private String action;

    public Transaction() {
    }

    public Transaction(Long id, String reference, String transactionType, Long performedBy, BigDecimal amount, LocalDateTime datetimeStamp, String action) {
        this.id = id;
        this.reference = reference;
        this.transactionType = transactionType;
        this.performedBy = performedBy;
        this.amount = amount;
        this.datetimeStamp = datetimeStamp;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDatetimeStamp() {
        return datetimeStamp;
    }

    public void setDatetimeStamp(LocalDateTime datetimeStamp) {
        this.datetimeStamp = datetimeStamp;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
