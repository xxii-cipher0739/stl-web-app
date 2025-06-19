package xyz.playground.stl_web_app.Constants;

public enum Action {
    CREATE_GAME("Created Game:"),
    UPDATE_PENDING_GAME("Updated game with pending status:"),
    UPDATE_ONGOING_GAME("Updated game with ongoing status:"),
    UPDATE_FOR_COMPLETION_GAME("Updated game with for completion status:"),
    START_GAME("Started game:"),
    FOR_COMPLETION_GAME("Processed game for completion:"),
    COMPLETE_GAME(""),
    CANCEL_GAME("Cancelled game:"),
    CREATE_REQUEST("Created request:"),
    UPDATE_REQUEST("Updated request:"),
    SUBMIT_REQUEST("Submitted request:"),
    CANCEL_REQUEST("Cancel request:"),
    REJECT_REQUEST("Reject request:"),
    APPROVE_REQUEST("Approve request:")
    ;

    private String value;

    Action(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
