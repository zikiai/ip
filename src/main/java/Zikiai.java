/**
 * Runs the Zikiai chatbot and responds to task commands entered by the user.
 */
public class Zikiai {
    public static void main(String[] args) {
        Ui ui = new Ui();
        Parser parser = new Parser();
        ui.showWelcome();

        Storage storage = new Storage();
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (ZikiaiException e) {
            ui.showError(e);
            ui.close();
            return;
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            if (parser.isByeCommand(input)) {
                break;
            }

            try {
                if (parser.isMarkCommand(input)) {
                    int taskIndex = parser.parseTaskIndex(input, tasks.size());

                    Task task = tasks.markAsDone(taskIndex);
                    storage.save(tasks);

                    ui.showTaskMarked(task);
                    continue;
                }

                if (parser.isUnmarkCommand(input)) {
                    int taskIndex = parser.parseTaskIndex(input, tasks.size());

                    Task task = tasks.markAsNotDone(taskIndex);
                    storage.save(tasks);

                    ui.showTaskUnmarked(task);
                    continue;
                }

                if (parser.isDeleteCommand(input)) {
                    int taskIndex = parser.parseTaskIndex(input, tasks.size());
                    Task deletedTask = tasks.delete(taskIndex);
                    storage.save(tasks);

                    ui.showTaskDeleted(deletedTask, tasks.size());
                    continue;
                }

                if (parser.isListCommand(input)) {
                    ui.showTaskList(tasks);
                    continue;
                }

                if (parser.isTodoCommand(input)) {
                    Task todo = parser.parseTodo(input);
                    tasks.add(todo);
                    storage.save(tasks);
                    ui.showTaskAdded(todo, tasks.size());
                    continue;
                }

                if (parser.isDeadlineCommand(input)) {
                    Task deadlineTask = parser.parseDeadline(input);
                    tasks.add(deadlineTask);
                    storage.save(tasks);
                    ui.showTaskAdded(deadlineTask, tasks.size());
                    continue;
                }

                if (parser.isEventCommand(input)) {
                    Task event = parser.parseEvent(input);
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

}
