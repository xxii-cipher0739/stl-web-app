package xyz.playground.stl_web_app.Model;

import jakarta.persistence.*;
import xyz.playground.stl_web_app.Constants.GameType;

import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Column(nullable = false)
    private LocalDateTime scheduleDateTime;

    @Column(nullable = false)
    private LocalDateTime cutOffDateTime;

    @Column(nullable = false)
    private String gameType;

    private boolean executed = false;

    private boolean enabled = true;

    public Game() {
    }

    public Game(Long id, String uuid, LocalDateTime scheduleDateTime, LocalDateTime cutOffDateTime, String gameType, boolean executed, boolean enabled) {
        this.id = id;
        this.uuid = uuid;
        this.scheduleDateTime = scheduleDateTime;
        this.cutOffDateTime = cutOffDateTime;
        this.gameType = gameType;
        this.executed = executed;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGameTypeValue() {
        return GameType.valueOf(gameType).getValue();
    }
}
