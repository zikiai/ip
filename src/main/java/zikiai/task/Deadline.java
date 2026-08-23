package zikiai.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task.
     * @param deadline deadline date for the task.
     */
    public Deadline(String description, LocalDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String getDescription() {
        return "[D]" + super.getDescription()
                + " (by: " + deadline.format(DISPLAY_FORMAT) + ")";
    }

    @Override
    public String toDataString() {
        return "[D][" + getStatusIcon() + "] | " + getDescriptionText() + " | " + deadline;
    }
}
