import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input and output for Zikiai.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = " _____ _ _    _       _\n"
            + "|__  /(_) | _(_) __ _(_)\n"
            + "  / / | | |/ / |/ _` | |\n"
            + " / /_ | |   <| | (_| | |\n"
            + "/____||_|_|\\_\\_|\\__,_|_|";

    private final Scanner scanner;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Zikiai.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /**
     * Returns whether another command is available from the user.
     *
     * @return true when another input line can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return next complete input line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task task whose status changed
     */
    public void showTaskMarked(Task task) {
        System.out.println(SEPARATOR);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("    " + task.getDescription());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task task whose status changed
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(SEPARATOR);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("    " + task.getDescription());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays confirmation that a task was removed.
     *
     * @param task task that was removed
     * @param taskCount number of remaining tasks
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(SEPARATOR);
        System.out.println("Noted. I've removed this task:");
        System.out.println("    " + task.getDescription());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(SEPARATOR);
    }

    /**
     * Displays every task using one-based numbering.
     *
     * @param tasks tasks to display
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(SEPARATOR);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i).getDescription());
        }
        System.out.println(SEPARATOR);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task task that was added
     * @param taskCount current number of tasks
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(SEPARATOR);
        System.out.println("Got it. I've added this task:");
        System.out.println("    " + task.getDescription());
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(SEPARATOR);
    }

    /**
     * Displays a user-facing error message.
     *
     * @param exception error to display
     */
    public void showError(ZikiaiException exception) {
        System.out.println(SEPARATOR);
        System.out.println("OOPSSSIES!!! " + exception.getMessage());
        System.out.println(SEPARATOR);
    }

    /**
     * Displays the chatbot's farewell message.
     */
    public void showGoodbye() {
        System.out.println(SEPARATOR);
        System.out.println("okay, bai bai");
        System.out.println(SEPARATOR);
    }

    /**
     * Releases the input scanner when the chatbot stops.
     */
    public void close() {
        scanner.close();
    }
}
