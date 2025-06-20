package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;
import xyz.playground.stl_web_app.Constants.BetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    @Column
    private Long gameId;

    @Column
    private LocalDateTime dateTimeCreated;

    @Column
    private Long createdBy;

    @Column
    private String bettor;

    @Column
    private BigDecimal amount;

    @Column
    private String betNumbers;

    @Column
    private BetStatus status;

    @Transient
    private String gameType;

    @Transient
    private LocalDateTime gameSchedule;

    public Bet() {
    }

    public Bet(Long id, String reference, Long gameId, LocalDateTime dateTimeCreated, String bettor, BigDecimal amount, Long createdBy, String betNumbers, BetStatus status) {
        this.id = id;
        this.reference = reference;
        this.gameId = gameId;
        this.dateTimeCreated = dateTimeCreated;
        this.bettor = bettor;
        this.createdBy = createdBy;
        this.amount = amount;
        this.betNumbers = betNumbers;
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

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public String getBettor() {
        return bettor;
    }

    public void setBettor(String bettor) {
        this.bettor = bettor;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBetNumbers() {
        return betNumbers;
    }

    public void setBetNumbers(String betNumbers) {
        this.betNumbers = betNumbers;
    }

    public BetStatus getStatus() {
        return status;
    }

    public void setStatus(BetStatus status) {
        this.status = status;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public LocalDateTime getGameSchedule() {
        return gameSchedule;
    }

    public void setGameSchedule(LocalDateTime gameSchedule) {
        this.gameSchedule = gameSchedule;
    }
}
