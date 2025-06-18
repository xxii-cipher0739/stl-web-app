package xyz.playground.stl_web_app.Constants;

public enum GameType {
    TWO_DIGIT("2 Digit", 1, 31, 2),
    THREE_DIGIT("3 Digit", 0, 9, 3),
    FOUR_DIGIT("4 Digit", 0, 9, 4),
    LAST_TWO_DIGIT("Last 2 Digit" ,0, 9, 2);

    private final String value;
    private final int minRange;
    private final int maxRange;
    private final int size;

    GameType(String value, int minRange, int maxRange, int size) {
        this.value = value;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.size = size;
    }

    public String getValue() {
        return value;
    }

    public int getMinRange() {
        return minRange;
    }

    public int getMaxRange() {
        return maxRange;
    }

    public int getSize() {
        return size;
    }
}

