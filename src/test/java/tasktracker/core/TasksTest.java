package tasktracker.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasktracker.exception.TaskNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TasksTest {

    private Tasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new Tasks();
    }

    @Test
    void shouldAddTaskAndIncrementId() {
        Task task1 = tasks.add("First task");
        Task task2 = tasks.add("Second task");

        assertNotNull(task1);
        assertNotNull(task2);
        assertEquals(1L, task1.getId());
        assertEquals(2L, task2.getId());
        assertEquals(2, tasks.getAllTasks().size());
        assertEquals(3L, tasks.getNextUniqueId());
    }

    @Test
    void shouldUpdateDescriptionOfExistingTask() {
        Task task = tasks.add("Old description");
        tasks.updateDescription(task.getId(), "New description");

        assertEquals("New description", tasks.getAllTasks().get(0).getDescription());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistentTaskDescription() {
        assertThrows(TaskNotFoundException.class, () -> tasks.updateDescription(99L, "Non existent"));
    }

    @Test
    void shouldUpdateStatusOfExistingTask() {
        Task task = tasks.add("Task to complete");
        tasks.updateStatus(task.getId(), true);

        assertEquals(TaskStatus.DONE, tasks.getAllTasks().get(0).getStatus());

        tasks.updateStatus(task.getId(), false);
        assertEquals(TaskStatus.IN_PROGRESS, tasks.getAllTasks().get(0).getStatus());
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenUpdatingNonExistentTaskStatus() {
        assertThrows(TaskNotFoundException.class, () -> tasks.updateStatus(99L, true));
    }

    @Test
    void shouldDeleteExistingTask() {
        Task task1 = tasks.add("Task 1");
        Task task2 = tasks.add("Task 2");

        tasks.delete(task1.getId());

        assertEquals(1, tasks.getAllTasks().size());
        assertFalse(tasks.getAllTasks().contains(task1));
        assertTrue(tasks.getAllTasks().contains(task2));
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenDeletingNonExistentTask() {
        tasks.add("Existing task");
        assertThrows(TaskNotFoundException.class, () -> tasks.delete(99L));
        assertEquals(1, tasks.getAllTasks().size());
    }


    @Test
    void shouldGetAllTasks() {
        tasks.add("Task A");
        tasks.add("Task B");
        assertEquals(2, tasks.getAllTasks().size());
    }

    @Test
    void shouldGetTasksByStatus() {
        tasks.add("Task 1");
        Task task2 = tasks.add("Task 2");
        tasks.updateStatus(task2.getId(), true);

        assertEquals(1, tasks.getTasksByStatus(TaskStatus.TODO).size());
        assertEquals(1, tasks.getTasksByStatus(TaskStatus.DONE).size());
        assertTrue(tasks.getTasksByStatus(TaskStatus.IN_PROGRESS).isEmpty());
    }

    @Test
    void shouldSynchronizeNextUniqueIdAfterLoading() {
        tasks.add("Task with ID 1"); // Initial ID is 1
        tasks.add("Task with ID 2");

        Task highIdTask = new Task(10L, "High ID task");
        tasks.getTasks().add(highIdTask);
        tasks.synchronizeNextUniqueId();

        assertEquals(11L, tasks.getNextUniqueId());
    }

    @Test
    void shouldSynchronizeNextUniqueIdForEmptyTasks() {
        tasks.synchronizeNextUniqueId();

        assertEquals(1L, tasks.getNextUniqueId());
    }

    @Test
    void shouldHandleSetterForNextUniqueId() {
        tasks.setNextUniqueId(50L);

        assertEquals(50L, tasks.getNextUniqueId());
    }
}