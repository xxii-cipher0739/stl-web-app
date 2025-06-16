package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long ownerId;

    @Column
    private BigDecimal balance;

    public Wallet() {
    }

    public Wallet(Long id, Long ownerId, BigDecimal balance) {
        this.id = id;
        this.ownerId = ownerId;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }
}
