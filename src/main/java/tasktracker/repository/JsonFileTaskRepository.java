package tasktracker.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import tasktracker.core.Tasks;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileTaskRepository implements TaskRepository {
    private final ObjectMapper objectMapper;
    private final String fileName;

    public JsonFileTaskRepository(ObjectMapper objectMapper, String fileName) {
        this.objectMapper = objectMapper;
        this.fileName = fileName;
    }

    @Override
    public Tasks loadTasks() throws IOException {
        var filePath = Paths.get(fileName);
        if (!Files.exists(filePath)) {
            return new Tasks();
        }

        var jsonFile = filePath.toFile();
        try {
            return objectMapper.readValue(jsonFile, Tasks.class);
        } catch (IOException  e) {
            throw new IOException("Error parsing tasks file JSON: " + fileName + ". " + e.getMessage(), e);
        }
    }

    @Override
    public void saveTasks(Tasks tasks) throws IOException {
        try (var fileWriter = new FileWriter(fileName)) {
            String json = objectMapper.writeValueAsString(tasks);
            fileWriter.write(json);
        } catch (JsonProcessingException e) {
            System.err.println("Error converting tasks to JSON for saving: " + e.getMessage());
        }
    }
}
