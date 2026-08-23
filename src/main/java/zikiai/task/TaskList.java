package zikiai.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns the chatbot's task collection and provides operations on it.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a defensive copy of the loaded tasks.
     *
     * @param tasks tasks loaded from storage.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index zero-based task index.
     * @return task whose status changed.
     */
    public Task markAsDone(int index) {
        Task task = tasks.get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index zero-based task index.
     * @return task whose status changed.
     */
    public Task markAsNotDone(int index) {
        Task task = tasks.get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based task index.
     * @return requested task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword keyword to search for.
     * @return matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescriptionText().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the current number of tasks.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }
}
