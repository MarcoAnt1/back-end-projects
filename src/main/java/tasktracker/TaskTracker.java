package tasktracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class TaskTracker {
    private final Tasks tasks;
    private final ObjectMapper objectWriter;

    public TaskTracker() {
        tasks = new Tasks();
        objectWriter = new ObjectMapper();
        configObjectWriter();
    }

    public void track() throws JsonProcessingException {
        System.out.println("Start Task Tracker");

        var scanner = new Scanner(System.in);
        String input = "";

        while (!Objects.equals(input.toLowerCase(), "exit")) {
            var shouldSaveJson = true;
            var description = "";
            var command = "";
            var value = "";

            input = scanner.nextLine();

            if (input.contains("\"")) {
                var indexOfDescription = input.indexOf("\"");
                description = input.substring(indexOfDescription + 1, input.length() - 1);
                input = input.substring(0, indexOfDescription - 1);
            }

            var commands = input.split(" ");
            if (commands.length > 1) {
                value = commands[1];
            }

            command = commands[0];

            switch (command.toUpperCase()) {
                case "ADD":
                    tasks.add(description);
                    break;
                case "UPDATE":
                    tasks.updateDescription(Long.parseLong(value), description);
                    break;
                case "DELETE":
                    tasks.delete(Long.parseLong(value));
                case "MARK-IN-PROGRESS":
                    tasks.updateStatus(Long.parseLong(value), false);
                    break;
                case "MARK-DONE":
                    tasks.updateStatus(Long.parseLong(value), true);
                    break;
                case "LIST":
                    if (value.isEmpty()) {
                        tasks.listAllTasks();
                    } else {
                        tasks.listTasksByStatus(TaskStatus.valueOf(value.toUpperCase()));
                    }
                    shouldSaveJson = false;
                    break;
                default:
                    System.out.println("Command does not exist");
                    shouldSaveJson = false;
                    break;
            }

            if (shouldSaveJson) {
                saveJson();
            }
        }
    }

    private void saveJson() {
        try (FileWriter file = new FileWriter("tasks.json")) {
            String json = objectWriter.writeValueAsString(tasks);
            file.write(json);
            file.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println("Error when try to write file. Try again later.");
        }
    }

    private void configObjectWriter() {
        objectWriter.registerModule(new JavaTimeModule());
        objectWriter.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectWriter.enable(SerializationFeature.INDENT_OUTPUT);
    }
}
