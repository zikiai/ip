package zikiai.task;

/**
 * Represents a task without an associated date or time.
 */
public class Todo extends Task {

    /**
     * Creates an incomplete todo with the given description.
     *
     * @param description description of the todo
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getDescription() {
        return "[T]" + super.getDescription();
    }

    @Override
    public String toDataString() {
        return "[T][" + getStatusIcon() + "] | " + getDescriptionText();
    }
}
