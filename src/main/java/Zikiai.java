import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Runs the Zikiai chatbot and responds to task commands entered by the user.
 */
public class Zikiai {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage();
        List<Task> tasks;
        try {
            tasks = storage.load();
        } catch (ZikiaiException e) {
            ui.showError(e);
            ui.close();
            return;
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (input.equals("bye")) {
                break;
            }

            try {
                if (input.matches("mark \\d+")) {
                    String numberText = input.substring(5);
                    int taskIndex = parseTaskIndex(numberText, tasks.size());

                    Task task = tasks.get(taskIndex);
                    task.markAsDone();
                    storage.save(tasks);

                    ui.showTaskMarked(task);
                    continue;
                }

                if (input.matches("unmark \\d+")) {
                    String numberText = input.substring(7);
                    int taskIndex = parseTaskIndex(numberText, tasks.size());

                    Task task = tasks.get(taskIndex);
                    task.markAsNotDone();
                    storage.save(tasks);

                    ui.showTaskUnmarked(task);
                    continue;
                }

                if (input.matches("delete \\d+")) {
                    String numberText = input.substring(7);
                    int taskIndex = parseTaskIndex(numberText, tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    storage.save(tasks);

                    ui.showTaskDeleted(deletedTask, tasks.size());
                    continue;
                }

                if (input.equals("list")) {
                    ui.showTaskList(tasks);
                    continue;
                }

                if (input.equals("todo")) {
                    throw new ZikiaiException("The description of a todo cannot be empty.");
                }

                if (input.startsWith("todo ")) {
                    String description = input.substring(5).trim();
                    if (description.isEmpty()) {
                        throw new ZikiaiException("The description of a todo cannot be empty.");
                    }
                    validateStorageText(description);

                    Task todo = new Todo(description);
                    tasks.add(todo);
                    storage.save(tasks);
                    ui.showTaskAdded(todo, tasks.size());
                    continue;
                }

                if (input.equals("deadline")) {
                    throw new ZikiaiException("The description of a deadline cannot be empty.");
                }

                if (input.startsWith("deadline ")) {
                    String details = input.substring(9).trim();
                    int byIndex = details.indexOf("/by");

                    if (byIndex == -1) {
                        throw new ZikiaiException("Please specify a deadline using /by.");
                    }

                    String description = details.substring(0, byIndex).trim();
                    String deadlineText = details.substring(byIndex + 3).trim();
                    if (description.isEmpty() || deadlineText.isEmpty()) {
                        throw new ZikiaiException("Please provide both a task and a deadline.");
                    }
                    validateStorageText(description, deadlineText);
                    LocalDate deadline = parseDeadline(deadlineText);

                    Task deadlineTask = new Deadline(description, deadline);
                    tasks.add(deadlineTask);
                    storage.save(tasks);
                    ui.showTaskAdded(deadlineTask, tasks.size());
                    continue;
                }

                if (input.equals("event")) {
                    throw new ZikiaiException("The description of an event cannot be empty.");
                }

                if (input.startsWith("event ")) {
                    String details = input.substring(6).trim();
                    int fromIndex = details.indexOf("/from");
                    int toIndex = fromIndex == -1
                            ? -1
                            : details.indexOf("/to", fromIndex + 5);

                    if (fromIndex == -1 || toIndex == -1) {
                        throw new ZikiaiException("Please specify an event using /from and /to.");
                    }

                    String description = details.substring(0, fromIndex).trim();
                    String from = details.substring(fromIndex + 5, toIndex).trim();
                    String to = details.substring(toIndex + 3).trim();
                    if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                        throw new ZikiaiException(
                                "Please provide an event, a start time, and an end time.");
                    }
                    validateStorageText(description, from, to);

                    Task event = new Event(description, from, to);
                    tasks.add(event);
                    storage.save(tasks);
                    ui.showTaskAdded(event, tasks.size());
                    continue;
                }
                throw new ZikiaiException("I'm sorrieeee, but I don't know what that means :-(");
            } catch (ZikiaiException e) {
                ui.showError(e);
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    /**
     * Converts a displayed task number into a validated zero-based list index.
     *
     * @param numberText number supplied with a mark, unmark, or delete command
     * @param taskCount current number of stored tasks
     * @return zero-based index of the requested task
     * @throws ZikiaiException if the number is too large or does not identify a task
     */
    private static int parseTaskIndex(String numberText, int taskCount) throws ZikiaiException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new ZikiaiException("That task number is too large.");
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new ZikiaiException("That task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Parses a deadline written in the ISO yyyy-MM-dd format.
     *
     * @param deadlineText deadline supplied by the user
     * @return parsed deadline date
     * @throws ZikiaiException if the text is not a valid ISO date
     */
    private static LocalDate parseDeadline(String deadlineText) throws ZikiaiException {
        try {
            return LocalDate.parse(deadlineText);
        } catch (DateTimeParseException e) {
            throw new ZikiaiException(
                    "Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.", e);
        }
    }

    /**
     * Rejects the field separator because it would make saved data ambiguous.
     *
     * @param values task fields that will be written to storage
     * @throws ZikiaiException if any field contains the reserved separator
     */
    private static void validateStorageText(String... values) throws ZikiaiException {
        for (String value : values) {
            if (value.contains("|")) {
                throw new ZikiaiException("Task details cannot contain the | character.");
            }
        }
    }

}
