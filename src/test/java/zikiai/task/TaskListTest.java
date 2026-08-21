package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection ownership and state-changing operations in {@link TaskList}.
 */
class TaskListTest {

    @Test
    void constructor_emptyList_sizeIsZero() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("read book"));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).getDescription());
    }

    @Test
    void add_validTask_taskStoredAtEnd() {
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("return book");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(second, tasks.get(1));
    }

    @Test
    void delete_middleTask_taskReturnedAndRemainingTasksShifted() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task third = new Todo("third");
        TaskList tasks = new TaskList(List.of(first, second, third));

        Task deleted = tasks.delete(1);

        assertSame(second, deleted);
        assertEquals(2, tasks.size());
        assertSame(third, tasks.get(1));
    }

    @Test
    void delete_indexOutsideList_exceptionThrownWithoutChangingSize() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
        assertEquals(1, tasks.size());
    }

    @Test
    void markAsDone_incompleteTask_statusChangedAndTaskReturned() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        Task markedTask = tasks.markAsDone(0);

        assertSame(task, markedTask);
        assertEquals("[T][X] read book", tasks.get(0).getDescription());
    }

    @Test
    void markAsNotDone_completedTask_statusReversedAndTaskReturned() {
        Task task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(List.of(task));

        Task unmarkedTask = tasks.markAsNotDone(0);

        assertSame(task, unmarkedTask);
        assertEquals("[T][ ] read book", tasks.get(0).getDescription());
    }

    @Test
    void get_indexOutsideList_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
    }
}
