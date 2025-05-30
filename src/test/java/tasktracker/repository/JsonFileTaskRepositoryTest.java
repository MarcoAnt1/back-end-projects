package tasktracker.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasktracker.core.Task;
import tasktracker.core.Tasks;
import tasktracker.core.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileTaskRepositoryTest {

    private JsonFileTaskRepository repository;

    @TempDir()
    Path tempDir;

    @BeforeEach
    void setUp() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String dataFileName = "test_tasks.json";

        repository = new JsonFileTaskRepository(objectMapper, tempDir.resolve(dataFileName).toString());
    }

    @Test
    void shouldLoadEmptyTasksIfFileDoesNotExist() throws IOException {
        Tasks loadedTasks = repository.loadTasks();
        assertNotNull(loadedTasks);
        assertTrue(loadedTasks.getAllTasks().isEmpty());
        assertEquals(1L, loadedTasks.getNextUniqueId());
    }

    @Test
    void shouldSaveAndLoadTasksCorrectly() throws IOException {
        Tasks originalTasks = new Tasks();
        Task task1 = originalTasks.add("Task A");
        Task task2 = originalTasks.add("Task B");
        originalTasks.updateStatus(task1.getId(), true);

        repository.saveTasks(originalTasks);

        Path expectedFilePath = tempDir.resolve("test_tasks.json");
        assertTrue(Files.exists(expectedFilePath));
        assertTrue(Files.size(expectedFilePath) > 0);

        Tasks loadedTasks = repository.loadTasks();

        assertNotNull(loadedTasks);
        assertEquals(2, loadedTasks.getAllTasks().size());
        assertEquals(3L, loadedTasks.getNextUniqueId());

        List<Task> loadedTaskList = loadedTasks.getAllTasks();
        assertEquals(task1.getId(), loadedTaskList.get(0).getId());
        assertEquals(task1.getDescription(), loadedTaskList.get(0).getDescription());
        assertEquals(TaskStatus.DONE, loadedTaskList.get(0).getStatus());

        assertEquals(task2.getId(), loadedTaskList.get(1).getId());
        assertEquals(task2.getDescription(), loadedTaskList.get(1).getDescription());
        assertEquals(TaskStatus.TODO, loadedTaskList.get(1).getStatus());
    }

    @Test
    void shouldHandleCorruptJsonFileDuringLoad() throws IOException {
        Path corruptFilePath = tempDir.resolve("test_tasks.json");
        Files.createDirectories(corruptFilePath.getParent());
        Files.writeString(corruptFilePath, "{ \"tasks\": [ { \"id\": \"not-a-number\" } ] }");

        IOException thrown = assertThrows(IOException.class, () -> repository.loadTasks());
        assertTrue(thrown.getMessage().contains("Error parsing tasks file JSON"));
    }

    @Test
    void shouldHandleEmptyTasksBeingSaved() throws IOException {
        Tasks emptyTasks = new Tasks();

        repository.saveTasks(emptyTasks);

        Path expectedFilePath = tempDir.resolve("test_tasks.json");
        assertTrue(Files.exists(expectedFilePath));
        assertFalse(Files.readString(expectedFilePath).isEmpty());
        assertTrue(Files.readString(expectedFilePath).contains("\"tasks\" : [ ]"));
    }
}