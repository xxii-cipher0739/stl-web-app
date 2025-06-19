package xyz.playground.stl_web_app.Constants;

public enum TransactionType {
    BET("BET"),
    REQUEST("REQ"),
    REFUND("REF"),
    GAME("GAME");

    private final String value;

    TransactionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
