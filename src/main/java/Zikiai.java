import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Runs the Zikiai chatbot and responds to task commands entered by the user.
 */
public class Zikiai {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Task> tasks = new ArrayList<>();

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

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (input.equals("bye")) {
                break;
            }

            if (input.matches("mark \\d+")) {
                String numberText = input.substring(5);
                int taskNumber = Integer.parseInt(numberText);
                int taskIndex = taskNumber - 1;
                if (taskIndex < 0 || taskIndex >= tasks.size()) {
                    System.out.println(line);
                    System.out.println("That task number does not exist.");
                    System.out.println(line);
                    continue;
                }

                Task task = tasks.get(taskIndex);
                task.markAsDone();

                System.out.println(line);
                System.out.println("Nice! I've marked this task as done:");
                System.out.println("    " + task.getDescription());
                System.out.println(line);
                continue;
            }

            if (input.matches("unmark \\d+")) {
                String numberText = input.substring(7);
                int taskNumber = Integer.parseInt(numberText);
                int taskIndex = taskNumber - 1;
                if (taskIndex < 0 || taskIndex >= tasks.size()) {
                    System.out.println(line);
                    System.out.println("That task number does not exist.");
                    System.out.println(line);
                    continue;
                }

                Task task = tasks.get(taskIndex);
                task.markAsNotDone();

                System.out.println(line);
                System.out.println("OK, I've marked this task as not done yet:");
                System.out.println("    " + task.getDescription());
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
                System.out.println(line);
                System.out.println("The description of a todo cannot be empty.");
                System.out.println(line);
                continue;
            }

            if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                if (description.isEmpty()) {
                    System.out.println(line);
                    System.out.println("The description of a todo cannot be empty.");
                    System.out.println(line);
                    continue;
                }

                Task todo = new Todo(description);
                tasks.add(todo);
                System.out.println(line);
                System.out.println("Got it. I've added this task:");
                System.out.println("    " + todo.getDescription());
                System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                System.out.println(line);
                continue;
            }

            tasks.add(new Task(input));
            System.out.println(line);
            System.out.println("    added: " + input);
            System.out.println(line);
        }
        System.out.println(line);
        System.out.println("okay, bai bai");
        System.out.println(line);
        scanner.close();
    }
}
