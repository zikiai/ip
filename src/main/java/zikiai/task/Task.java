package zikiai.task;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    private boolean isDone;
    private final String description;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description description of the task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the task description together with its completion status.
     *
     * @return formatted task, such as {@code [X] read book}
     */
    public String getDescription() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the unformatted text used to describe this task.
     *
     * @return raw task description
     */
    protected String getDescriptionText() {
        return description;
    }

    /**
     * Returns this task in the format used by the data file.
     * Subclasses override this method to include their task type and details.
     *
     * @return storage representation of this task
     */
    public String toDataString() {
        return "[?][" + getStatusIcon() + "] | " + description;
    }
}
