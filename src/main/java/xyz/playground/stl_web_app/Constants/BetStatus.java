package xyz.playground.stl_web_app.Constants;

public enum BetStatus {
    PLACED("Placed"),
    CONFIRM("Confirmed"),
    CANCELLED("Cancelled"),
    WON("Won"),
    LOST("Lost");

    private final String value;

    BetStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
