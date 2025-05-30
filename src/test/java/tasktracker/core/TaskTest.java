package tasktracker.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Open PR");
    }

    @Test
    void shouldCreateTaskWithCorrectProperties() {
        assertEquals(1L, task.getId());
        assertEquals("Open PR", task.getDescription());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
        assertEquals(task.getCreatedAt(), task.getUpdatedAt());
    }

    @Test
    void shouldUpdateDescription() {
        task.setDescription("Buy milk and bread");

        assertEquals("Buy milk and bread", task.getDescription());
    }

    @Test
    void shouldMarkAsDone() {
        task.markAsDone();

        assertEquals(TaskStatus.DONE, task.getStatus());
    }

    @Test
    void shouldMarkAsProgress() {
        task.markAsDone();
        Instant doneTime = task.getUpdatedAt();

        task.markAsProgress();

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
    }

    @Test
    void shouldNotBeEqualIfIdsAreDifferent() {
        Task differentTask = new Task(2L, "Buy groceries");

        assertNotEquals(task, differentTask);
        assertNotEquals(task.hashCode(), differentTask.hashCode());
    }

    @Test
    void shouldHandleNullDescription() {
        Task nullDescTask = new Task(3L, null);

        assertNull(nullDescTask.getDescription());
    }
}
