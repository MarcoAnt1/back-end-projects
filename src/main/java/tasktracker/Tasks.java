package tasktracker;

import java.util.ArrayList;
import java.util.List;

public class Tasks {
    public final List<Task> tasks;
    private Long uniqueIdStarter = 1L;

    public Tasks() {
        tasks = new ArrayList<>();
    }

    public void add(String description) {
        var newTask = new Task(uniqueIdStarter++, description);
        tasks.add(newTask);
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
        tasks.forEach(System.out::println);
    }

    public void listTasksByStatus(TaskStatus status) {
        tasks.stream().filter(task -> task.getStatus().equals(status)).forEach(System.out::println);
    }

}
