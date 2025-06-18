package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;
import xyz.playground.stl_web_app.Constants.GameStatus;
import xyz.playground.stl_web_app.Constants.GameType;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime scheduleDateTime;

    @Column(nullable = false)
    private LocalDateTime cutOffDateTime;

    @Column(nullable = false)
    private String gameType;

    @Column
    private String winningCombination;

    @Column
    private double betAmountThreshold;

    @Column
    private double betThresholdPercentage;

    @Column
    private GameStatus status;

    public Game() {
    }

    public Game(Long id,
                LocalDateTime scheduleDateTime,
                LocalDateTime cutOffDateTime,
                String gameType,
                String winningCombination,
                double betAmountThreshold,
                double betThresholdPercentage,
                GameStatus status) {

        this.id = id;
        this.scheduleDateTime = scheduleDateTime;
        this.cutOffDateTime = cutOffDateTime;
        this.gameType = gameType;
        this.winningCombination = winningCombination;
        this.betAmountThreshold = betAmountThreshold;
        this.betThresholdPercentage = betThresholdPercentage;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getScheduleDateTime() {
        return scheduleDateTime;
    }

    public void setScheduleDateTime(LocalDateTime scheduleDateTime) {
        this.scheduleDateTime = scheduleDateTime;
    }

    public LocalDateTime getCutOffDateTime() {
        return cutOffDateTime;
    }

    public void setCutOffDateTime(LocalDateTime cutOffDateTime) {
        this.cutOffDateTime = cutOffDateTime;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getWinningCombination() {
        return winningCombination;
    }

    public void setWinningCombination(String winningCombination) {
        this.winningCombination = winningCombination;
    }

    public GameStatus getStatus() {
        return status;
    }

    public double getBetAmountThreshold() {
        return betAmountThreshold;
    }

    public void setBetAmountThreshold(double betAmountThreshold) {
        this.betAmountThreshold = betAmountThreshold;
    }

    public double getBetThresholdPercentage() {
        return betThresholdPercentage;
    }

    public void setBetThresholdPercentage(double betThresholdPercentage) {
        this.betThresholdPercentage = betThresholdPercentage;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public String getGameTypeValue() {
        return GameType.valueOf(gameType).getValue();
    }
}
