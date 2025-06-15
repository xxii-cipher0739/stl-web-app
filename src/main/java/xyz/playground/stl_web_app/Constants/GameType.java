package xyz.playground.stl_web_app.Constants;

public enum GameType {
    TWO_DIGIT("2 Digit"),
    THREE_DIGIT("3 Digit"),
    FOUR_DIGIT("4 Digit"),
    LAST_TWO_DIGIT("Last 2 Digit");

    private final String value;

    GameType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

