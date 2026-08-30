package zikiai.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Todo;

/**
 * Tests the non-trivial command parsing and validation performed by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTaskIndex_firstTask_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(0, parser.parseTaskIndex("mark 1", 3));
    }

    @Test
    void parseTaskIndex_lastTask_zeroBasedIndexReturned() throws ZikiaiException {
        assertEquals(2, parser.parseTaskIndex("delete 3", 3));
    }

    @Test
    void parseTaskIndex_zero_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("mark 0", 3));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_numberBeyondList_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("unmark 4", 3));

        assertEquals("That task number does not exist.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_numberTooLargeForInteger_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTaskIndex("delete 999999999999999999999", 3));

        assertEquals("That task number is too large.", exception.getMessage());
    }

    @Test
    void parseFindKeyword_validCommand_trimmedKeywordReturned() throws ZikiaiException {
        assertEquals("return book", parser.parseFindKeyword("find   return book   "));
    }

    @Test
    void parseFindKeyword_emptyCommand_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseFindKeyword("find"));

        assertEquals("Please enter a keyword to find.", exception.getMessage());
    }

    @Test
    void parseFindKeyword_whitespaceOnly_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseFindKeyword("find   "));

        assertEquals("Please enter a keyword to find.", exception.getMessage());
    }

    @Test
    void parseTodo_validCommand_trimmedTodoReturned() throws ZikiaiException {
        Todo todo = parser.parseTodo("todo    read book   ");

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void parseTodo_emptyDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTodo("todo   "));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    void parseTodo_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseTodo("todo read | book"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseDeadline_validLeapDay_formattedDeadlineReturned() throws ZikiaiException {
        Deadline deadline = parser.parseDeadline("deadline submit report /by 2024-02-29");

        assertEquals("[D][ ] submit report (by: Feb 29 2024)", deadline.getDescription());
        assertEquals("[D][ ] | submit report | 2024-02-29", deadline.toDataString());
    }

    @Test
    void parseDeadline_missingByMarker_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report 2026-08-23"));

        assertEquals("Please specify a deadline using /by.", exception.getMessage());
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline /by 2026-08-23"));

        assertEquals("Please provide both a task and a deadline.", exception.getMessage());
    }

    @Test
    void parseDeadline_missingDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report /by"));

        assertEquals("Please provide both a task and a deadline.", exception.getMessage());
    }

    @Test
    void parseDeadline_impossibleDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit report /by 2025-02-29"));

        assertEquals(
                "Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline submit | report /by 2026-08-23"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_validCommand_trimmedEventReturned() throws ZikiaiException {
        Event event = parser.parseEvent("event   team meeting /from Mon 2pm /to 4pm   ");

        assertEquals(
                "[E][ ] team meeting (from: Mon 2pm to: 4pm)", event.getDescription());
        assertEquals(
                "[E][ ] | team meeting | Mon 2pm | 4pm", event.toDataString());
    }

    @Test
    void parseDeadline_separatorInDate_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseDeadline("deadline report /by 2026-08-23 | noon"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_separatorInDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team | meeting /from 2pm /to 4pm"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_separatorInEndTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event meeting /from 2pm /to 4pm | later"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseEvent_missingToMarker_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon 2pm"));

        assertEquals("Please specify an event using /from and /to.", exception.getMessage());
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event /from Mon 2pm /to 4pm"));

        assertEquals(
                "Please provide an event, a start time, and an end time.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingStartTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from /to 4pm"));

        assertEquals(
                "Please provide an event, a start time, and an end time.",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingEndTime_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon 2pm /to"));

        assertEquals(
                "Please provide an event, a start time, and an end time.",
                exception.getMessage());
    }

    @Test
    void parseEvent_reservedSeparator_exceptionThrown() {
        ZikiaiException exception = assertThrows(
                ZikiaiException.class, () -> parser.parseEvent("event team meeting /from Mon | 2pm /to 4pm"));

        assertEquals("Task details cannot contain the | character.", exception.getMessage());
    }
}
