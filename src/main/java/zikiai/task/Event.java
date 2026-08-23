package zikiai.task;

/**
 * Represents a task that takes place over a period of time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description description of the event.
     * @param from start time of the event.
     * @param to end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getDescription() {
        return "[E]" + super.getDescription()
                + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toDataString() {
        return "[E][" + getStatusIcon() + "] | " + getDescriptionText()
                + " | " + from + " | " + to;
    }
}
