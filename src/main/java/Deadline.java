/**
 * Represents a task that must be completed by a deadline.
 */
public class Deadline extends Task {
    private final String deadline;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description description of the task
     * @param deadline deadline for the task
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = deadline;
    }

    @Override
    public String getDescription() {
        return "[D]" + super.getDescription() + " (by: " + deadline + ")";
    }
}
