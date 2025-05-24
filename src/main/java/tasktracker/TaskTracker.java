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
                    String command = parsedCommand.command();
                    Optional<Long> id = parsedCommand.id();
                    Optional<String> description = parsedCommand.description();

                    switch (command.toUpperCase()) {
                        case "ADD":
                            var newTask = tasks.add(description.orElseThrow(() -> new InvalidCommandException("Description is required for ADD command.")));
                            System.out.println("Task added with ID: " + newTask.getId());
                            break;
                        case "UPDATE":
                            tasks.updateDescription(
                                    id.orElseThrow(() -> new InvalidCommandException("ID is required for UPDATE command.")),
                                    description.orElseThrow(() -> new InvalidCommandException("Description is required for UPDATE command."))
                            );
                            System.out.println("Task ID " + id.get() + " description updated.");
                            break;
                        case "DELETE":
                            boolean deleted = tasks.delete(id.orElseThrow(() -> new InvalidCommandException("ID is required for DELETE command.")));
                            if (deleted) {
                                System.out.println("Task ID " + id.get() + " deleted.");
                            } else {
                                System.out.println("Failed to delete task ID " + id.get() + ".");
                            }
                            break;
                        case "MARK-IN-PROGRESS":
                            tasks.updateStatus(id.orElseThrow(() -> new InvalidCommandException("ID is required for MARK-IN-PROGRESS command.")), false);
                            System.out.println("Task ID " + id.get() + " marked in progress.");
                            break;
                        case "MARK-DONE":
                            tasks.updateStatus(id.orElseThrow(() -> new InvalidCommandException("ID is required for DONE command.")), true);
                            System.out.println("Task ID " + id.get() + " marked done.");
                            break;
                        case "LIST":
                            shouldSaveJson = false;
                            List<Task> tasksToDisplay;
                            if (description.isEmpty() || description.get().equalsIgnoreCase("ALL")) {
                                tasksToDisplay = tasks.getAll();
                            } else {
                                try {
                                    TaskStatus status = TaskStatus.valueOf(description.get().toUpperCase());
                                    tasksToDisplay = tasks.getTasksByStatus(status);
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Invalid status for LIST command. Use ALL, DONE, or IN_PROGRESS.");
                                    tasksToDisplay = List.of();
                                }
                            }

                            if (tasksToDisplay.isEmpty()) {
                                System.out.println("No tasks to show for this filter.");
                            } else {
                                tasksToDisplay.forEach(System.out::println);
                            }
                            break;
                        default:
                            System.err.println("Unknown command: " + command);
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

    private Tuple3<String, String, String> getUserInputs(String input) {
        String description = "";
        String value = "";

        if (input.contains("\"")) {
            var indexOfDescription = input.indexOf("\"");
            description = input.substring(indexOfDescription + 1, input.length() - 1);
            input = input.substring(0, indexOfDescription - 1);
        }

        var commands = input.split(" ");
        if (commands.length > 1) {
            value = commands[1];
        }

        String command = commands[0];
        return Tuple.of(command, value, description);
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
