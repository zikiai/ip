package zikiai;

import zikiai.exception.ZikiaiException;
import zikiai.parser.Parser;
import zikiai.storage.Storage;
import zikiai.task.Task;
import zikiai.task.TaskList;
import zikiai.ui.Ui;

/**
 * Processes commands for both the console and the graphical chatbot.
 */
public class Zikiai {
    private final Storage storage;
    private final Parser parser = new Parser();
    private TaskList tasks;
    private String loadingError;
    private boolean isExit;

    /**
     * Creates a chatbot using the default task file.
     */
    public Zikiai() {
        this(new Storage());
    }

    /**
     * Loads a chatbot session from the supplied storage.
     * A failed load blocks commands to protect the existing file.
     *
     * @param storage storage used by this session.
     */
    public Zikiai(Storage storage) {
        this.storage = storage;
        try {
            tasks = new TaskList(storage.load());
        } catch (ZikiaiException exception) {
            loadingError = Ui.formatError(exception);
        }
    }

    /**
     * Returns the initial greeting or a loading error.
     */
    public String getWelcome() {
        return loadingError == null ? Ui.getGreeting() : loadingError;
    }

    /**
     * Returns whether this session can accept another command.
     */
    public boolean canAcceptCommands() {
        return loadingError == null && !isExit;
    }

    /**
     * Processes one command and returns its reply without reading or printing console text.
     *
     * @param input complete command entered by the user.
     * @return confirmation, task list, or user-facing error.
     */
    public String getResponse(String input) {
        if (loadingError != null) {
            return loadingError;
        }
        if (isExit) {
            return Ui.formatGoodbye();
        }
        if (parser.isByeCommand(input)) {
            isExit = true;
            return Ui.formatGoodbye();
        }
        try {
            if (parser.isMarkCommand(input)) {
                Task task = tasks.markAsDone(parser.parseTaskIndex(input, tasks.size()));
                storage.save(tasks);
                return Ui.formatTaskMarked(task);
            }
            if (parser.isUnmarkCommand(input)) {
                Task task = tasks.markAsNotDone(parser.parseTaskIndex(input, tasks.size()));
                storage.save(tasks);
                return Ui.formatTaskUnmarked(task);
            }
            if (parser.isDeleteCommand(input)) {
                Task task = tasks.delete(parser.parseTaskIndex(input, tasks.size()));
                storage.save(tasks);
                return Ui.formatTaskDeleted(task, tasks.size());
            }
            if (parser.isListCommand(input)) {
                return Ui.formatTaskList(tasks);
            }
            if (parser.isFindCommand(input)) {
                return Ui.formatMatchingTasks(tasks.find(parser.parseFindKeyword(input)));
            }
            if (parser.isTodoCommand(input)) {
                return addTask(parser.parseTodo(input));
            }
            if (parser.isDeadlineCommand(input)) {
                return addTask(parser.parseDeadline(input));
            }
            if (parser.isEventCommand(input)) {
                return addTask(parser.parseEvent(input));
            }
            throw new ZikiaiException("I'm sorrieeee, but I don't know what that means :-(");
        } catch (ZikiaiException exception) {
            return Ui.formatError(exception);
        }
    }

    /**
     * Adds and saves a validated task before reporting success.
     */
    private String addTask(Task task) throws ZikiaiException {
        tasks.add(task);
        storage.save(tasks);
        return Ui.formatTaskAdded(task, tasks.size());
    }

    /**
     * Runs the optional console interface used by the console regression tests.
     *
     * @param args command-line arguments; currently unused.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Zikiai zikiai = new Zikiai();
        if (!zikiai.canAcceptCommands()) {
            ui.showResponse(zikiai.getWelcome());
            ui.close();
            return;
        }
        while (zikiai.canAcceptCommands() && ui.hasNextCommand()) {
            ui.showResponse(zikiai.getResponse(ui.readCommand()));
        }
        if (zikiai.canAcceptCommands()) {
            ui.showResponse(Ui.formatGoodbye());
        }
        ui.close();
    }
}
