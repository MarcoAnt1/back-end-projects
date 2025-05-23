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
        objectWriter.registerModule(new JavaTimeModule());
        objectWriter.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectWriter.enable(SerializationFeature.INDENT_OUTPUT);
    }


    public void track() throws JsonProcessingException {
        System.out.println("Start Task Tracker");

        var scanner = new Scanner(System.in);
        var input = "";


        while (!Objects.equals(input.toLowerCase(), "exit")) {
            var shouldSaveJson = true;
            input = scanner.nextLine();

            var command = input.split(" ")[0];
            var value = input.split(" ")[1];

            switch (command.toUpperCase()) {
                case "ADD":
                    tasks.add(value);
                    break;
                case "UPDATE":
                    var newDescription = input.split(" ")[2];
                    tasks.updateDescription(value, newDescription);
                    break;
                case "DELETE":
                    tasks.delete(value);
                case "MARK-IN-PROGRESS":
                    tasks.updateStatus(value, false);
                    break;
                case "MARK-DONE":
                    tasks.updateStatus(value, true);
                    break;
                default:
                    shouldSaveJson = false;
                    break;
            }

            if (shouldSaveJson) {
                saveJson();
            }
        }
    }

    private void saveJson() throws JsonProcessingException {
        String json = objectWriter.writeValueAsString(tasks);

        try (FileWriter file = new FileWriter("tasks.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(json);
    }
}
