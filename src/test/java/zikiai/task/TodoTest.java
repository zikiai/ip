package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the display and storage representations of todo tasks.
 */
class TodoTest {

    @Test
    void constructor_newTodo_incompleteRepresentationsReturned() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }

    @Test
    void markAsDone_incompleteTodo_doneRepresentationsReturned() {
        Todo todo = new Todo("read book");

        todo.markAsDone();

        assertEquals("[T][X] read book", todo.getDescription());
        assertEquals("[T][X] | read book", todo.toDataString());
    }

    @Test
    void markAsNotDone_completedTodo_incompleteRepresentationsRestored() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        todo.markAsNotDone();

        assertEquals("[T][ ] read book", todo.getDescription());
        assertEquals("[T][ ] | read book", todo.toDataString());
    }
}
