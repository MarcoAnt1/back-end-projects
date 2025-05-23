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

    public void updateDescription(String value, String description) {
        for (var task: tasks) {
            if (task.getId().toString().equals(value)) {
                task.setDescription(description);
            }
        }
    }

    public void updateStatus(String value, boolean done) {
        for (var task: tasks) {
            if (task.getId().toString().equals(value)) {
                if (done) {
                    task.markAsDone();
                } else {
                    task.markAsProgress();
                }
            }
        }
    }


    public void delete(String id) {
        tasks.removeIf(t -> t.getId().toString().equals(id));
    }
}
