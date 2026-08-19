import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves the chatbot's tasks using a local data file.
 */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "zikiai.txt");

    /**
     * Writes the complete task list to the data file, replacing its old contents.
     *
     * @param tasks tasks to save
     * @throws IOException if the directory or file cannot be written
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(FILE_PATH.getParent());

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toDataString());
        }
        Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file
     * @throws IOException if the data file cannot be read
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split("\\s*\\|\\s*", -1);
            String taskHeader = parts[0];
            Task task;

            if (taskHeader.startsWith("[T]")) {
                task = new Todo(parts[1]);
            } else if (taskHeader.startsWith("[D]")) {
                task = new Deadline(parts[1], parts[2]);
            } else if (taskHeader.startsWith("[E]")) {
                task = new Event(parts[1], parts[2], parts[3]);
            } else {
                throw new IOException("Unknown task type in data file: " + taskHeader);
            }

            if (taskHeader.contains("[X]")) {
                task.markAsDone();
            }
            tasks.add(task);
        }
        return tasks;
    }
}
