package xyz.playground.stl_web_app.Constants;

public enum GameStatus {
    PENDING("Pending"),
    ONGOING("Ongoing"),
    FOR_COMPLETION("For Completion"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String value;

    GameStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
