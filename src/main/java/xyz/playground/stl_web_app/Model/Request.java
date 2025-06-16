package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column
    private Long requestedBy;

    @Column
    private Long requestedTo;

    @Column
    private boolean processed;

    public Request(Long id, String reference, Long requestedBy, Long requestedTo, boolean processed) {
        this.id = id;
        this.reference = reference;
        this.requestedBy = requestedBy;
        this.requestedTo = requestedTo;
        this.processed = processed;
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

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
