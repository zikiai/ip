package zikiai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zikiai.storage.Storage;

/**
 * Tests the shared command-response boundary used by the GUI and console.
 */
class ZikiaiTest {
    @TempDir
    private Path directory;

    private Zikiai newSession() {
        return new Zikiai(new Storage(directory.resolve("tasks.txt")));
    }

    @Test
    void getResponse_allTaskTypes_savedAndLoaded() {
        Zikiai bot = newSession();
        assertTrue(bot.canAcceptCommands());
        assertEquals("Hello! I'm Zikiai.\nWhat can I do for you?", bot.getWelcome());
        assertEquals("Got it. I've added this task:\n    [T][ ] read book\n"
                + "Now you have 1 tasks in the list.", bot.getResponse("todo read book"));
        assertTrue(bot.getResponse("deadline return book /by 2026-08-30")
                .contains("[D][ ] return book (by: Aug 30 2026)"));
        assertTrue(bot.getResponse("event meeting /from 2pm /to 4pm")
                .contains("[E][ ] meeting (from: 2pm to: 4pm)"));
        assertEquals(bot.getResponse("list"), newSession().getResponse("list"));
    }

    @Test
    void getResponse_statusAndDeletion_savedAcrossSessions() {
        Zikiai bot = newSession();
        bot.getResponse("todo first");
        bot.getResponse("todo second");
        assertEquals("Nice! I've marked this task as done:\n    [T][X] second", bot.getResponse("mark 2"));
        assertTrue(newSession().getResponse("list").contains("2.[T][X] second"));
        assertEquals("OK, I've marked this task as not done yet:\n    [T][ ] second", bot.getResponse("unmark 2"));
        assertEquals("Noted. I've removed this task:\n    [T][ ] first\nNow you have 1 tasks in the list.",
                bot.getResponse("delete 1"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] second", newSession().getResponse("list"));
    }

    @Test
    void getResponse_invalidCommands_preserveStateAndRecover() {
        Zikiai bot = newSession();
        bot.getResponse("todo keep me");
        String original = bot.getResponse("list");
        String[] invalidInputs = {"todo", "deadline bad /by Sunday", "event bad", "mark 99",
            "unmark 0", "delete 99", "find", "unknown", ""};
        for (String input : invalidInputs) {
            assertTrue(bot.getResponse(input).startsWith("OOPSSSIES!!! "), input);
            assertEquals(original, bot.getResponse("list"), input);
        }
        assertEquals("There are none!", bot.getResponse("find missing"));
        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] keep me", bot.getResponse("find keep"));
        assertEquals(original, newSession().getResponse("list"));
    }

    @Test
    void getResponse_bye_blocksFurtherChanges() {
        Zikiai bot = newSession();
        assertEquals("okay, bai bai", bot.getResponse("bye"));
        assertFalse(bot.canAcceptCommands());
        assertEquals("okay, bai bai", bot.getResponse("todo should not be added"));
        assertEquals("Here are the tasks in your list:", newSession().getResponse("list"));
    }

    @Test
    void constructor_corruptFile_blocksInputWithoutOverwriting() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "invalid saved data\n");
        Zikiai bot = newSession();
        assertFalse(bot.canAcceptCommands());
        assertEquals("OOPSSSIES!!! I couldn't load the saved tasks because line 1 is invalid.", bot.getWelcome());
        assertEquals(bot.getWelcome(), bot.getResponse("todo do not overwrite"));
        assertEquals("invalid saved data\n", Files.readString(file));
    }

    @Test
    void getResponse_saveFailure_returnsErrorInsteadOfSuccess() throws IOException {
        Path parent = directory.resolve("blocked");
        Zikiai bot = new Zikiai(new Storage(parent.resolve("tasks.txt")));
        Files.writeString(parent, "not a directory");
        assertEquals("OOPSSSIES!!! I couldn't save your tasks to the data file.", bot.getResponse("todo test"));
        assertTrue(bot.canAcceptCommands());
        assertEquals("not a directory", Files.readString(parent));
    }
}
