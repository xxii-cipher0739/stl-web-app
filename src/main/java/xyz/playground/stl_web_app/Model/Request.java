package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;
import xyz.playground.stl_web_app.Constants.RequestStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column
    private LocalDateTime dateTimeCreated;

    @Column
    private Long requestedBy;

    @Column
    private Long requestedTo;

    @Column
    private BigDecimal amount;

    @Column
    private boolean processed;

    @Column
    private RequestStatus status;

    public Request() {
    }

    public Request(Long id, String reference, LocalDateTime dateTimeCreated, Long requestedBy, Long requestedTo, BigDecimal amount, boolean processed, RequestStatus status) {
        this.id = id;
        this.reference = reference;
        this.dateTimeCreated = dateTimeCreated;
        this.requestedBy = requestedBy;
        this.requestedTo = requestedTo;
        this.amount = amount;
        this.processed = processed;
        this.status = status;
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

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public Long getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Long requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Long getRequestedTo() {
        return requestedTo;
    }

    public void setRequestedTo(Long requestedTo) {
        this.requestedTo = requestedTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }
}
