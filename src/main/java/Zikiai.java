import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Runs the Zikiai chatbot and responds to task commands entered by the user.
 */
public class Zikiai {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String banner = " _____ _ _    _       _\n"
                + "|__  /(_) | _(_) __ _(_)\n"
                + "  / / | | |/ / |/ _` | |\n"
                + " / /_ | |   <| | (_| | |\n"
                + "/____||_|_|\\_\\_|\\__,_|_|";
        String line = "_".repeat(60);
        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hello! I'm Zikiai.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Storage storage = new Storage();
        List<Task> tasks;
        try {
            tasks = storage.load();
        } catch (ZikiaiException e) {
            printError(e, line);
            scanner.close();
            return;
        }

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
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

                    System.out.println(line);
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("    " + task.getDescription());
                    System.out.println(line);
                    continue;
                }

                if (input.matches("unmark \\d+")) {
                    String numberText = input.substring(7);
                    int taskIndex = parseTaskIndex(numberText, tasks.size());

                    Task task = tasks.get(taskIndex);
                    task.markAsNotDone();
                    storage.save(tasks);

                    System.out.println(line);
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("    " + task.getDescription());
                    System.out.println(line);
                    continue;
                }

                if (input.matches("delete \\d+")) {
                    String numberText = input.substring(7);
                    int taskIndex = parseTaskIndex(numberText, tasks.size());
                    Task deletedTask = tasks.remove(taskIndex);
                    storage.save(tasks);

                    System.out.println(line);
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("    " + deletedTask.getDescription());
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(line);
                    continue;
                }

                if (input.equals("list")) {
                    System.out.println(line);
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i).getDescription());
                    }
                    System.out.println(line);
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
                    printTaskAdded(todo, tasks.size(), line);
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
                    printTaskAdded(deadlineTask, tasks.size(), line);
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
                    printTaskAdded(event, tasks.size(), line);
                    continue;
                }
                throw new ZikiaiException("I'm sorrieeee, but I don't know what that means :-(");
            } catch (ZikiaiException e) {
                printError(e, line);
            }
        }

        System.out.println(line);
        System.out.println("okay, bai bai");
        System.out.println(line);
        scanner.close();
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
     * Prints the confirmation shown after a task is added.
     *
     * @param task task that was added
     * @param taskCount current number of stored tasks
     * @param line separator used by the text interface
     */
    private static void printTaskAdded(Task task, int taskCount, String line) {
        System.out.println(line);
        System.out.println("Got it. I've added this task:");
        System.out.println("    " + task.getDescription());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(line);
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

    /**
     * Prints a user-facing chatbot error.
     *
     * @param exception error to display
     * @param line separator used by the text interface
     */
    private static void printError(ZikiaiException exception, String line) {
        System.out.println(line);
        System.out.println("OOPSSSIES!!! " + exception.getMessage());
        System.out.println(line);
    }
}
