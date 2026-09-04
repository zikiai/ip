package zikiai.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Todo;

/**
 * Recognizes commands and converts their text into validated values and tasks.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";

    /**
     * Creates a parser for recognizing and validating Zikiai commands.
     */
    public Parser() {
    }

    /**
     * Returns whether the user wants to exit.
     *
     * @param input complete user input.
     * @return true for the bye command.
     */
    public boolean isByeCommand(String input) {
        return input.equals(BYE_COMMAND);
    }

    /**
     * Returns whether the input is a valid mark command shape.
     *
     * @param input complete user input.
     * @return true when the command contains a numeric task number.
     */
    public boolean isMarkCommand(String input) {
        return isNumberedCommand(input, MARK_COMMAND);
    }

    /**
     * Returns whether the input is a valid unmark command shape.
     *
     * @param input complete user input.
     * @return true when the command contains a numeric task number.
     */
    public boolean isUnmarkCommand(String input) {
        return isNumberedCommand(input, UNMARK_COMMAND);
    }

    /**
     * Returns whether the input is a valid delete command shape.
     *
     * @param input complete user input.
     * @return true when the command contains a numeric task number.
     */
    public boolean isDeleteCommand(String input) {
        return isNumberedCommand(input, DELETE_COMMAND);
    }

    /**
     * Returns whether the user requested the task list.
     *
     * @param input complete user input.
     * @return true for the list command.
     */
    public boolean isListCommand(String input) {
        return input.equals(LIST_COMMAND);
    }

    /**
     * Returns whether the input begins a find command.
     *
     * @param input complete user input.
     * @return true for a find command, including an empty one.
     */
    public boolean isFindCommand(String input) {
        return isCommandWithOptionalArguments(input, FIND_COMMAND);
    }

    /**
     * Returns whether the input begins a todo command.
     *
     * @param input complete user input.
     * @return true for a todo command, including an empty one.
     */
    public boolean isTodoCommand(String input) {
        return isCommandWithOptionalArguments(input, TODO_COMMAND);
    }

    /**
     * Returns whether the input begins a deadline command.
     *
     * @param input complete user input.
     * @return true for a deadline command, including an empty one.
     */
    public boolean isDeadlineCommand(String input) {
        return isCommandWithOptionalArguments(input, DEADLINE_COMMAND);
    }

    /**
     * Returns whether the input begins an event command.
     *
     * @param input complete user input.
     * @return true for an event command, including an empty one.
     */
    public boolean isEventCommand(String input) {
        return isCommandWithOptionalArguments(input, EVENT_COMMAND);
    }

    /**
     * Extracts and validates the one-based task number in a command.
     *
     * @param input command containing a task number.
     * @param taskCount current number of tasks.
     * @return corresponding zero-based task index.
     * @throws ZikiaiException if the number is too large or does not identify a task.
     */
    public int parseTaskIndex(String input, int taskCount) throws ZikiaiException {
        String numberText = input.substring(input.indexOf(' ')).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ZikiaiException("That task number is too large.");
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new ZikiaiException("That task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Extracts and validates the keyword in a find command.
     *
     * @param input complete find command.
     * @return keyword to search for.
     * @throws ZikiaiException if the keyword is empty.
     */
    public String parseFindKeyword(String input) throws ZikiaiException {
        String keyword = input.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new ZikiaiException("Please enter a keyword to find.");
        }
        return keyword;
    }

    /**
     * Creates a todo from a validated command.
     *
     * @param input complete todo command.
     * @return parsed todo.
     * @throws ZikiaiException if its description is empty or cannot be stored safely.
     */
    public Todo parseTodo(String input) throws ZikiaiException {
        if (input.equals(TODO_COMMAND)) {
            throw new ZikiaiException("The description of a todo cannot be empty.");
        }

        String description = input.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new ZikiaiException("The description of a todo cannot be empty.");
        }
        validateStorageText(description);
        return new Todo(description);
    }

    /**
     * Creates a deadline from a validated command.
     *
     * @param input complete deadline command.
     * @return parsed deadline task.
     * @throws ZikiaiException if required fields or a valid ISO date are missing.
     */
    public Deadline parseDeadline(String input) throws ZikiaiException {
        if (input.equals(DEADLINE_COMMAND)) {
            throw new ZikiaiException("The description of a deadline cannot be empty.");
        }

        String details = input.substring(DEADLINE_COMMAND.length()).trim();
        int byIndex = details.indexOf(BY_MARKER);
        if (byIndex == -1) {
            throw new ZikiaiException("Please specify a deadline using /by.");
        }

        String description = details.substring(0, byIndex).trim();
        String deadlineText = details.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || deadlineText.isEmpty()) {
            throw new ZikiaiException("Please provide both a task and a deadline.");
        }
        validateStorageText(description, deadlineText);
        return new Deadline(description, parseDeadlineDate(deadlineText));
    }

    /**
     * Creates an event from a validated command.
     *
     * @param input complete event command.
     * @return parsed event task.
     * @throws ZikiaiException if required fields are missing or cannot be stored safely.
     */
    public Event parseEvent(String input) throws ZikiaiException {
        if (input.equals(EVENT_COMMAND)) {
            throw new ZikiaiException("The description of an event cannot be empty.");
        }

        String details = input.substring(EVENT_COMMAND.length()).trim();
        int fromIndex = details.indexOf(FROM_MARKER);
        int toIndex = fromIndex == -1
                ? -1
                : details.indexOf(TO_MARKER, fromIndex + FROM_MARKER.length());
        if (fromIndex == -1 || toIndex == -1) {
            throw new ZikiaiException("Please specify an event using /from and /to.");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new ZikiaiException(
                    "Please provide an event, a start time, and an end time.");
        }
        validateStorageText(description, from, to);
        return new Event(description, from, to);
    }

    /**
     * Returns whether the input is a command followed by a numeric task number.
     */
    private boolean isNumberedCommand(String input, String command) {
        return input.matches(command + " \\d+");
    }

    /**
     * Returns whether the input is a command with or without arguments.
     */
    private boolean isCommandWithOptionalArguments(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Parses a deadline written in the ISO yyyy-MM-dd format.
     *
     * @param deadlineText deadline supplied by the user.
     * @return parsed deadline date.
     * @throws ZikiaiException if the text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String deadlineText) throws ZikiaiException {
        try {
            return LocalDate.parse(deadlineText);
        } catch (DateTimeParseException exception) {
            throw new ZikiaiException(
                    "Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.",
                    exception);
        }
    }

    /**
     * Rejects the field separator because it would make saved data ambiguous.
     * Varargs lets the same check accept one todo field, two deadline fields,
     * or three event fields without separate overloads or caller-created arrays.
     *
     * @param values task fields to validate; every supplied field is checked.
     * @throws ZikiaiException if any field contains the reserved separator.
     */
    private void validateStorageText(String... values) throws ZikiaiException {
        for (String value : values) {
            if (value.contains("|")) {
                throw new ZikiaiException("Task details cannot contain the | character.");
            }
        }
    }
}
