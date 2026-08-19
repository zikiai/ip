import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the chatbot's tasks to a local data file.
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
}
