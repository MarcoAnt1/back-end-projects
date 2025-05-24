package tasktracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vavr.Tuple;
import io.vavr.Tuple3;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class TaskTracker {
    private Tasks tasks;
    private final ObjectMapper objectMapper;
    private final CommandParser commandParser;
    private static final String TASKS_FILE_NAME = "tasks.json";

    public TaskTracker() {
        tasks = new Tasks();
        objectMapper = new ObjectMapper();
        commandParser = new CommandParser();
        configObjectWriter();
        loadTasksFromFile();
    }

    public void track() throws JsonProcessingException {
        System.out.println("Start Task Tracker. Type 'exit' to quit.");
        System.out.println("Commands: ADD \"desc\", UPDATE <id> \"desc\", DELETE <id>, MARK-IN-PROGRESS <id>, MARK-DONE <id>, LIST [ALL|DONE|IN_PROGRESS]");

        try (var scanner = new Scanner(System.in)) {
            String input;

            while (true) {
                System.out.print(">");
                input = scanner.nextLine();

                if (Objects.equals(input.toLowerCase(), "exit")) {
                    System.out.println("Exiting Task Tracker. Goodbye!");
                    break;
                }

                var shouldSaveJson = true;

                try {
                    var parsedCommand = commandParser.parse(input);

                    switch (parsedCommand.command()) {
                        case "ADD":
                            handleAddCommand(parsedCommand);
                            break;
                        case "UPDATE":
                            handleUpdateCommand(parsedCommand);
                            break;
                        case "DELETE":
                            handleDeleteCommand(parsedCommand);
                            break;
                        case "MARK-IN-PROGRESS":
                            handleMarkInProgressCommand(parsedCommand);
                            break;
                        case "MARK-DONE":
                            handleMarkDoneCommand(parsedCommand);
                            break;
                        case "LIST":
                            handleListCommand(parsedCommand);
                            break;
                        default:
                            System.err.println("Unknown command: " + parsedCommand.command());
                            shouldSaveJson = false;
                            break;
                    }

                } catch (InvalidCommandException | TaskNotFoundException e) {
                    System.err.println("Error: " + e.getMessage());
                    shouldSaveJson = false;
                } catch (Exception e) {
                    System.err.println("An unexpected error occurred: " + e.getMessage());
                    shouldSaveJson = false;
                }

                if (shouldSaveJson) {
                    saveJson();
                }
            }
        }
    }

    private void configObjectWriter() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private void loadTasksFromFile() {
        var filePath = Paths.get(TASKS_FILE_NAME);
        if (!Files.exists(filePath)) {
            tasks.synchronizeNextUniqueId();
            return;
        }

        try {
            var jsonFile = filePath.toFile();
            tasks = objectMapper.readValue(jsonFile, Tasks.class);
            tasks.synchronizeNextUniqueId();
        } catch (IOException  e) {
            System.err.println("Error loading tasks from file: " + e.getMessage());
            this.tasks = new Tasks();
            tasks.synchronizeNextUniqueId();
        }
    }

    private void handleAddCommand(ParsedCommand command) throws InvalidCommandException {
        var newTask = tasks.add(command.description().orElseThrow(() -> new InvalidCommandException("Description is required for ADD command.")));
        System.out.println("Task added with ID: " + newTask.getId());
    }

    private void handleUpdateCommand(ParsedCommand command) throws InvalidCommandException {
        Long id = command.id().orElseThrow(() -> new InvalidCommandException("ID is required for UPDATE command."));
        String description = command.description().orElseThrow(() -> new InvalidCommandException("Description is required for UPDATE command."));
        tasks.updateDescription(id, description);
        System.out.println("Task ID " + id + " description updated.");
    }

    private void handleDeleteCommand(ParsedCommand command) throws InvalidCommandException {
        Long id = command.id().orElseThrow(() -> new InvalidCommandException("ID is required for UPDATE command."));
        boolean deleted = tasks.delete(id);
        if (deleted) {
            System.out.println("Task ID " + id + " deleted.");
        } else {
            System.out.println("Task ID " + id + " not found for deletion.");
        }
    }

    private void handleMarkInProgressCommand(ParsedCommand command) throws InvalidCommandException, TaskNotFoundException {
        Long id = command.id()
                .orElseThrow(() -> new InvalidCommandException("ID is required for MARK-IN-PROGRESS command."));
        tasks.updateStatus(id, false);
        System.out.println("Task ID " + id + " marked in progress.");
    }

    private void handleMarkDoneCommand(ParsedCommand command) throws InvalidCommandException, TaskNotFoundException {
        Long id = command.id()
                .orElseThrow(() -> new InvalidCommandException("ID is required for MARK-DONE command."));
        tasks.updateStatus(id, true);
        System.out.println("Task ID " + id + " marked done.");
    }

    private void handleListCommand(ParsedCommand command) {
        // Description field of ParsedCommand is reused for status string for LIST
        Optional<String> statusString = command.description();

        List<Task> tasksToDisplay;
        if (statusString.isEmpty() || statusString.get().equalsIgnoreCase("ALL")) {
            tasksToDisplay = tasks.getAll();
        } else {
            try {
                TaskStatus status = TaskStatus.valueOf(statusString.get().toUpperCase());
                tasksToDisplay = tasks.getTasksByStatus(status);
            } catch (IllegalArgumentException e) {
                System.err.println("Invalid status for LIST command. Use ALL, DONE, or IN_PROGRESS.");
                tasksToDisplay = List.of(); // Return empty list on invalid status
            }
        }

        if (tasksToDisplay.isEmpty()) {
            System.out.println("No tasks to show for this filter.");
        } else {
            tasksToDisplay.forEach(System.out::println);
        }
    }

    private void saveJson() {
        try (var fileWriter = new FileWriter(TASKS_FILE_NAME)) {
            String json = objectMapper.writeValueAsString(tasks);
            fileWriter.write(json);
            fileWriter.flush();
        } catch (JsonProcessingException e) {
            System.err.println("Error converting tasks to JSON for saving: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing tasks to file " + TASKS_FILE_NAME + ": " + e.getMessage());
        }
    }
}
