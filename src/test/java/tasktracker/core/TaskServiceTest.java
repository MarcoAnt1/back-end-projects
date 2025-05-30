package tasktracker.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tasktracker.exception.TaskNotFoundException;
import tasktracker.repository.TaskRepository;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository mockTaskRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() throws IOException {
        var initialTasks = new Tasks();
        Task initialTask = initialTasks.add("Existing task");
        initialTasks.updateStatus(initialTask.getId(), true);

        when(mockTaskRepository.loadTasks()).thenReturn(initialTasks);

        taskService = new TaskService(mockTaskRepository);
    }

    @Test
    void shouldAddTaskAndSave() {
        Task newTask = taskService.addTask("New task description");

        assertNotNull(newTask);
        assertEquals("New task description", newTask.getDescription());
        assertEquals(2L, newTask.getId());

        try {
            verify(mockTaskRepository, times(1)).saveTasks(any(Tasks.class));
        } catch (IOException e) {
            fail("IOException should not occur during verification");
        }
    }

    @Test
    void shouldUpdateTaskDescriptionAndSave() throws TaskNotFoundException {
        taskService.updateTaskDescription(1L, "Updated description");

        assertEquals("Updated description", taskService.getAllTasks().get(0).getDescription());
        try {
            verify(mockTaskRepository, times(1)).saveTasks(any(Tasks.class));
        } catch (IOException e) {
            fail("IOException should not occur during verification");
        }
    }

    @Test
    void shouldUpdateTaskStatusAndSave() throws TaskNotFoundException {
        Task addedTask = taskService.addTask("Task to mark");
        clearInvocations(mockTaskRepository);

        taskService.updateTaskStatus(addedTask.getId(), true);

        assertEquals(TaskStatus.DONE, taskService.getTasksByStatus(TaskStatus.DONE).get(0).getStatus());
        try {
            verify(mockTaskRepository, times(1)).saveTasks(any(Tasks.class));
        } catch (IOException e) {
            fail("IOException should not occur during verification");
        }
    }

    @Test
    void shouldHandleLoadTasksFailure() throws IOException {
        when(mockTaskRepository.loadTasks()).thenThrow(new IOException("Simulated load error"));

        TaskService serviceWithLoadError = new TaskService(mockTaskRepository);

        assertTrue(serviceWithLoadError.getAllTasks().isEmpty());
    }

    @Test
    void shouldHandleSaveTasksFailure() throws TaskNotFoundException {
        try {
            doThrow(new IOException("Simulated save error")).when(mockTaskRepository).saveTasks(any(Tasks.class));
        } catch (IOException e) {
            fail("Should not throw IOException during mock setup");
        }

        taskService.addTask("This should fail to save");

        try {
            verify(mockTaskRepository, times(1)).saveTasks(any(Tasks.class));
        } catch (IOException e) {
            // Expected to catch this from the mock call
        }
    }

    @Test
    void shouldGetCorrectTasksList() {
        List<Task> allTasks = taskService.getAllTasks();
        assertFalse(allTasks.isEmpty());
        assertEquals(1, allTasks.size());
        assertEquals("Existing task", allTasks.get(0).getDescription());
    }

    @Test
    void shouldGetTasksByStatus() {
        taskService.addTask("New TODO task");
        List<Task> doneTasks = taskService.getTasksByStatus(TaskStatus.DONE);
        List<Task> todoTasks = taskService.getTasksByStatus(TaskStatus.TODO);

        assertEquals(1, doneTasks.size());
        assertEquals(1, todoTasks.size());
        assertEquals(TaskStatus.DONE, doneTasks.get(0).getStatus());
        assertEquals(TaskStatus.TODO, todoTasks.get(0).getStatus());
    }
}
