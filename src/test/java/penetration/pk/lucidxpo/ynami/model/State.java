package penetration.pk.lucidxpo.ynami.model;

public enum State {
    OPEN("open"),
    CLOSED("closed"),
    FILTERED("filtered"),
    TIMEDOUT("timedout");

    private final String text;

    State(final String text) {
        this.text = text;
    }

    public static State fromString(final String text) {
        if (text != null) {
            for (final State state : values()) {
                if (text.equalsIgnoreCase(state.text)) {
                    return state;
                }
            }
        }
        throw new IllegalArgumentException("Cannot parse port state: " + text);
    }
}