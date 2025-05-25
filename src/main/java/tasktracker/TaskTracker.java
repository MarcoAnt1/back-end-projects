package tasktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tasktracker.cli.CliApplication;
import tasktracker.cli.CommandParser;
import tasktracker.command.CommandDispatcher;
import tasktracker.core.TaskService;
import tasktracker.repository.JsonFileTaskRepository;

public class TaskTracker {
    private static final String TASKS_FILE_NAME = "main/java/tasktracker/tasks.json";

    public static void track() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        var taskRepository = new JsonFileTaskRepository(objectMapper, TASKS_FILE_NAME);
        var commandParser = new CommandParser();
        var commandDispatcher = new CommandDispatcher();
        var taskService = new TaskService(taskRepository);

        var app = new CliApplication(commandParser, commandDispatcher, taskService);
        app.run();
    }
}
