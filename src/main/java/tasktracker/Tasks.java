package tasktracker;

import java.util.ArrayList;
import java.util.List;

public class Tasks {
    public final List<Task> tasks;
    private Long uniqueId;

    public Tasks() {
        tasks = new ArrayList<>();
    }

    public void setUniqueId() {
        if (tasks.isEmpty()) {
            uniqueId = 1L;
        } else {
            uniqueId = tasks.get(tasks.size() - 1).getId() + 1;
        }
    }

    public void add(String description) {
        var newTask = new Task(uniqueId++, description);
        tasks.add(newTask);
        System.out.println("Task added successfully (ID: " + newTask.getId() + ")");
    }

    public void updateDescription(Long value, String description) {
        var updated = false;
        for (var task: tasks) {
            if (task.getId().equals(value)) {
                task.setDescription(description);
                updated = true;
            }
        }

        if (!updated) {
            System.out.println("Id not found");
        }
    }

    public void updateStatus(Long value, boolean done) {
        var updated = false;
        for (var task: tasks) {
            if (task.getId().equals(value)) {
                if (done) {
                    task.markAsDone();
                } else {
                    task.markAsProgress();
                }
                updated = true;
            }
        }

        if (!updated) {
            System.out.println("Id not found");
        }
    }

    public void delete(Long id) {
        tasks.removeIf(t -> t.getId().equals(id));
    }

    public void listAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No task to show.");
        }

        tasks.forEach(System.out::println);
    }

    public void listTasksByStatus(TaskStatus status) {
        tasks.stream().filter(task -> task.getStatus().equals(status)).forEach(System.out::println);
    }

}
